package com.sd.chatmq;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.sd.connection.ConnectionRabbitmq;
import com.sd.util.DBHelper;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.view.Menu;

/**
 * @author Isaías Santana
 * Created on 04/05/2016 , modified on 07/05/2016
 * Representa a tela de chat.
 */
public class ActivityChat extends AppCompatActivity {

    private static ConnectionRabbitmq connectionRabbitmq;
    private static ChatArrayAdapter chatArrayAdapter;
    private static String destinatario;
    private ListView listViewMensagens; // o a lista de mensagens do chat.
    private EditText editTextMensagem;
    private Thread subscribeThread; // uma Thread para ouvir as mensagens que chegam para o chat
    private static Handler handlerMessageChat;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_chat);

        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Handler getHandlerMessageChat()
    {
        return handlerMessageChat;
    }

    private void init() throws IOException
    {
        connectionRabbitmq = ConnectionRabbitmq.getConnectionRabbitmq();
        connectionRabbitmq.publishToAMQP();
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(),R.layout.right);
        listViewMensagens = (ListView) findViewById(R.id.list_msgs);
        editTextMensagem = (EditText) findViewById(R.id.message);
        listViewMensagens.setAdapter(chatArrayAdapter);
        destinatario = getIntent().getStringExtra("numeroCelular"); // Obtém o número do celular do usuário de destino.
        buscarConversas();
        subscribe();
    }

    /**
     * busca todas as conversas desse contato para esse destinatário.
     */
    private void buscarConversas()
    {
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                SQLiteDatabase database = ActivityChatMQ.getDataBaseChat().getReadableDatabase();
                Cursor cursor = database.query(DBHelper.TABLE_CONVERSAS, new String[]{"NUM_DESTINATARIO","MENSAGEM","ENVIADA_POR","HORA"},"NUM_REMETENTE = ? AND NUM_DESTINATARIO = ?",new String[]{ActivityChatMQ.getContato().getTelefone(),destinatario},null,null,"_id");

                if(cursor.moveToFirst())
                {
                    boolean lado = (cursor.getInt(2) == 1);
                    chatArrayAdapter.add( new ChatMessage(lado,cursor.getString(1),cursor.getString(3)));
                }

                while(cursor.moveToNext())
                {
                    boolean lado = (cursor.getInt(2) == 1);
                    chatArrayAdapter.add( new ChatMessage(lado,cursor.getString(1),cursor.getString(3)));
                }
                cursor.close();
            }
        }).start();
    }


    /**
     * Ao abrir a tela de chat, possa ser que já tenham mensagens prontas para serem exibidas. Esse
     * Método busca se tem mensagens que não foram exibidas na fila interna de mensagens da conexão.
     * @param origem O número do usuário que envia a mensagem.
     * @param destino O número do usuário que recebe a mensagem.
     */
    private void inserirMensagens(String origem, String destino)
    {
        List<ChatMessage> mensagensAnteriores = connectionRabbitmq.getConversas().get(origem+":"+destino);
        if(mensagensAnteriores != null)
            if(!mensagensAnteriores.isEmpty())
                for(ChatMessage mensagem : mensagensAnteriores)
                {
                    Log.i("Anteriores",mensagem.getMessage());
                    chatArrayAdapter.add(new ChatMessage(mensagem.isLeft(),mensagem.getMessage()));
                }
    }


    /**
     * Verifica se há mensagens para exibir, o método só pegará a mensagem e a exibirá, se estiver
     * Com a tela de chat aberta e com o contato especifico para a mensagem que chegou.
     * @throws IOException Uma parada não planejada da Thread
     */
    private void subscribe() throws IOException
    {
       subscribeThread = new Thread(new Runnable() {
            @Override
            public void run() {

                Looper.prepare();
                handlerMessageChat = new Handler()
                {
                    @Override
                    public void handleMessage(Message msg)
                    {
                        super.handleMessage(msg);
                        String mensagem = msg.getData().getString("msg");
                        final String[] detalhesMensagem = mensagem.split(":");
                        final String hora = detalhesMensagem[3]+":"+detalhesMensagem[4];

                        if (detalhesMensagem[0].equals(destinatario)) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    chatArrayAdapter.add(new ChatMessage(false, detalhesMensagem[2],hora));
                                    ConnectionRabbitmq.setMensagemResposta(null);
                                }
                            });
                        }
                    }
                };
                Looper.loop();
            }
        });
        subscribeThread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_mq, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Envia uma mensagem para um contato especifico. O padrão para enviar a mensagem é:
     * númeroDoUsuárioQueEnvia:númeroDoUsuárioQueRecebe:mensagem. Esse padrão é para poder manter
     * um HashMap das mensagens que chegam dos vários contatos.
     * @param view
     */
    public void sendMessage(View view)
    {
        String message = editTextMensagem.getText().toString();

        if (!message.matches(""))
        {
            DateFormat ft = SimpleDateFormat.getTimeInstance();
            String hora[] = ft.format(new Date()).split(":");
            String horaFormato = hora[0]+":"+hora[1];
            chatArrayAdapter.add(new ChatMessage(true, message,horaFormato));
            connectionRabbitmq.publishMessage(ActivityChatMQ.getContato().getTelefone()+":"+destinatario+":"+message+":"+horaFormato); // Envia a mensagem para o broker do RabbitMQ.
            editTextMensagem.setText("");
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        connectionRabbitmq.getPublishThread().interrupt();
        if(subscribeThread != null) subscribeThread.interrupt();

    }
}