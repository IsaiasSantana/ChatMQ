package com.sd.connection;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.sd.chatmq.ActivityChatMQ;
import com.sd.chatmq.ChatMessage;
import com.sd.util.DBHelper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;


/**
 * Created by Isaías on 20/04/2016.
 */
public class ConnectionRabbitmq
{

    private Thread subscribeThread;
    private Thread publishThread;
    private BlockingDeque<String> queue;
    private HashMap<String, List<ChatMessage>> conversas;  //Serve para guardar as mensagens que chegam dos vários contatos;
    private ConnectionFactory factory;
    private Connection connection;
    private  AMQP.Queue.DeclareOk queueOK;

    private static ConnectionRabbitmq connectionRabbitmq;
    private static boolean isConnected,isConnectionBroken;
    private static String mensagemResposta;

    private static final String HOST = "YOUR_HOST";
    private static final String EXCHANGE_TYPE = "direct"; //Usuario para usuario
    private static final String EXCHANGE_NAME = "chatSD";


    /**
     * Apenas existe uma única instância da conexão com o servidor
     */
    private ConnectionRabbitmq ()
    {
        queue = new LinkedBlockingDeque<>();
        factory = new ConnectionFactory();
        isConnected = false;
        conversas = new HashMap<>();
        mensagemResposta =null;

    }

    /**
     * Um objeto que permite se comunicar com o servidor do RabbitMQ.
     * O objeto não possui nenhuma configuração.
     * No onCreate, deve-se ser chamado na seguite ordem:
     * con = ConnectionRabbitmq.getConnectionRabbitmq();
     * con.setFactory();
     * con.publishToAMPQ();
     * con.subscribe(handler); Onde handler é o ouvinte de mensagens com o método handleMessage implementado.
     * @return retorna um objeto de conexão com o RabbitMQ.
     */
    public static ConnectionRabbitmq getConnectionRabbitmq()
    {
        if(ConnectionRabbitmq.connectionRabbitmq == null)
        {
            connectionRabbitmq = new ConnectionRabbitmq();
            connectionRabbitmq.setFactory();
            return connectionRabbitmq;
        }
        return ConnectionRabbitmq.connectionRabbitmq;
    }

    /**
     * Inicializa as configurações, Host, USER_NAME, e PassWord.
     */
    private void setFactory()
    {
        try
        {
            this.factory.setUri(HOST);
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {}

    }

    /**
     * Adiciona uma mensagem na fila de mensagens interna da conexão.
     * @param message a mensagem para enviar.
     */
    public void publishMessage(String message) {
        try
        {
            queue.putLast(message);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Busca o número do usuário no banco de dados.
     * @return uma String representando o número do celular.
     */
    private String getUsuario()
    {
        SQLiteDatabase database = ActivityChatMQ.getDataBaseChat().getReadableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_MEU_NUMERO,new String[]{"NUMERO"},"_id = 1",null,null,null,null);
        if(cursor.moveToFirst()){
            String res = cursor.getString(0);
             cursor.close();
            return res;}
        return null;
    }

    /**
     * Cria a conexão com o rabbitMQ e ouve todas as mensagens que chegam.
     * @param handler serve para disparar as mensagens que chegam
     */
    public void subscribe(final Handler handler)
    {
        subscribeThread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                lacoInfinito:
                while (true)
                {
                   try
                   {
                       if(!isConnected)
                       {
                           connection = factory.newConnection();
                           Channel channel = connection.createChannel();
                           channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE);
                           channel.basicQos(1);

                           String nomeEChaveFila = getUsuario();

                           queueOK = channel.queueDeclare(nomeEChaveFila, true, false, false, null); //Uma fila duravel,não exclusiva e não auto deletavel.
                           channel.queueBind(queueOK.getQueue(), EXCHANGE_NAME, nomeEChaveFila);
                           isConnected = true;
                           isConnectionBroken = false;
                           subscribe(queueOK,channel,handler);
                       }
                   }
                   catch (Exception e1) {
                       Log.d("", "Connection broken: " + e1.getClass().getName());
                       isConnectionBroken = true;
                       try
                       {
                           isConnected =false;
                           Thread.sleep(5000); //sleep and then try again
                       }
                       catch (InterruptedException e)
                       {
                       }
                   }

                }
            }
        });
        subscribeThread.start();
    }


    /**
     * Escuta as mensagens que chegam.
     * @param q a fila que se está ouvindo.
     * @param channel o canal de comunicação para uma única conexão.
     * @param handlerMessage o handler para disparar as mensagens.
     * @throws IOException se a Thread parar sem esperar.
     */
    private void subscribe(AMQP.Queue.DeclareOk q,Channel channel,Handler handlerMessage) throws IOException
    {
        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(q.getQueue(), true, consumer);
        String message;
        SQLiteDatabase dbConversas = ActivityChatMQ.getDataBaseChat().getWritableDatabase();
        um:
        while(true)
        {
            if(!isConnectionBroken)
            {
                try
                {
                    //Espera a mensagem chegar, isso é bloqueante
                    QueueingConsumer.Delivery delivery = consumer.nextDelivery();

                    message = new String(delivery.getBody());
                    String[] split = extrairMensagem(message);

                    //Envia a mensagem para o handler
                    Message msg = handlerMessage.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("msg", message);
                    msg.setData(bundle);
                    handlerMessage.sendMessage(msg);

                    adicionarConversas(false, split[1]+":"+split[0],split[2]);
                    DBHelper.salvarConversas(dbConversas,split[1],split[0], split[2],false,split[3]+":"+split[4]);
                    Log.i("resposta ActivityChatMQ", "[r] " + message);


                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                    break um;
                }
            } else break um;
        }
    }

    /**
     * Este metodo, tem por objetivo pegar os cabeçalhos da mensagem.
     * @param mensagem a mensagem a ser capturada
     * @return retorna um vetor de 3 posições, na posição 0 contém o número que enviou a mensagem,
     * na posição 1 o número que irá receber a mensagem e a 2 é a mensagem em si.
     */
    public String[] extrairMensagem(String mensagem)
    {
        return mensagem.split(":");
    }

    /**
     * Adiciona uma mensagem, a um HashMap que representa as conversas do usuários para com seus
     * contatos.
     * @param lado se foi o usuário que enviou, ou recebeu a mensagem.
     * @param remetente o par, usuárioQueEnvia:usuárioQueRecebe como chave.
     * @param mensagem a mensagem de uma conversa.
     */
    private void adicionarConversas(boolean lado, String remetente, String mensagem)
    {
        if(conversas.isEmpty())
        {
            inserir(lado,remetente,mensagem);
        }
        else
        {
            if(conversas.containsKey(remetente)) obterEAdiconarConversa(lado,remetente,mensagem);
            else inserir(lado,remetente,mensagem);
        }
    }

    private void obterEAdiconarConversa(boolean lado, String remetente, String mensagem)
    {
        List<ChatMessage> batePapo = new ArrayList<>(conversas.get(remetente));
        batePapo.add(new ChatMessage(lado,mensagem,batePapo.size()+1));
        conversas.put(remetente,batePapo);
    }

    private void inserir(boolean lado, String remetente, String mensagem)
    {
        List<ChatMessage> batePapo = new ArrayList<>();
        batePapo.add(new ChatMessage(lado,mensagem,batePapo.size()+1));
        conversas.put(remetente,batePapo);
    }

    /**
     * Envia a uma mensagem para um determinado destinatário
     * Baseado no código disponível em: https://www.cloudamqp.com/blog/2014-10-28-rabbitmq-on-android.html
     * Esse método vai receber alguns parametros em breve, para enviar uma mensagem para um determinado contato.
     */
    public void publishToAMQP()
    {
        publishThread = new Thread(new Runnable() {
            @Override
            public void run() {
                dois:
                while(true) {
                    try
                    {
                        Channel channel = connection.createChannel();
                        channel.confirmSelect();

                        if(isConnected)
                        {
                            while (true)
                            {
                                if(!isConnectionBroken)
                                {
                                    String  message = queue.takeFirst();
                                    try
                                    {
                                        String[] destinatario = extrairMensagem(message);

                                        String pessoaQueEnvia = destinatario[0];
                                        String pessoaQueRecebe = destinatario[1];
                                        String mensagemAEnviar = destinatario[2];
                                        String hora = destinatario[3]+":"+destinatario[4];

                                        channel.basicPublish(EXCHANGE_NAME, destinatario[1], null, message.getBytes());
                                        channel.waitForConfirmsOrDie();
                                        adicionarConversas(true,pessoaQueEnvia+":"+pessoaQueRecebe,mensagemAEnviar);
                                        //Salva a mensagem no banco se conseguiu enviar.
                                        DBHelper.salvarConversas(ActivityChatMQ.getDataBaseChat().getWritableDatabase(),pessoaQueEnvia,pessoaQueRecebe,mensagemAEnviar,true,hora);
                                        Log.i("Send enviou", "[s] " + message);

                                    }
                                    catch (Exception e)
                                    {
                                        Log.d("","[f] " + message);
                                        queue.putFirst(message);
                                        throw e;
                                    }
                                }
                            }
                        }
                    }
                    catch (InterruptedException e)
                    {
                        break dois;
                    }
                    catch (Exception e)
                    {
                        Log.d("", "Connection broken: " + e.getClass().getName());
                        try
                        {
                            Thread.sleep(5000); //Espera 5 segundos e tenta novamente
                        }
                        catch (InterruptedException e1)
                        {
                            break dois;
                        }
                    }
                }
            }
        });
        publishThread.start();
    }

    /**
     * Thread ouvinte de recebimento de mensagens.
     * @return a Thread responsável por ouvir mensagens recebidas.
     */
    public Thread getSubscribeThread(){return subscribeThread;}

    /**
     * Thread que envia  mensagens.
     * @return a Thread responsável por enviar mensagens.
     */
    public Thread getPublishThread(){return publishThread;}

    public void setIsConnected(boolean isConnected)
    {
        this.isConnected = isConnected;
    }

    public HashMap<String,List<ChatMessage>> getConversas()
    {
        return conversas;
    }

    public AMQP.Queue.DeclareOk getQueueOK()
    {
        return queueOK;
    }

    public Connection getConnection()
    {
        return connection;
    }

    public static boolean isIsConnected()
    {
        return isConnected;
    }

    public static boolean isIsConnectionBroken()
    {
        return isConnectionBroken;
    }

    public static String getMensagemResposta()
    {
        return mensagemResposta;
    }

    public static void setMensagemResposta(String mensagemResposta){
        ConnectionRabbitmq.mensagemResposta = mensagemResposta;
    }

    public void closeConection()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    connection.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}