package com.sd.chatmq;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;


import com.sd.adapter.FragmentAdapter;
import com.sd.connection.ConnectionRabbitmq;
import com.sd.domain.Contato;
import com.sd.util.DBHelper;
import com.sd.util.DataBaseChat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Isaías Santana
 */
public class ActivityChatMQ extends AppCompatActivity
{
    private ConnectionRabbitmq connectionRabbitmq;

    private static DataBaseChat dataBaseChat; // O banco de dados.
    private static Contato contato; // Mantém registro do número do celular do usuário.
    private static final int CODIGO = 1; // Serve para enviar uma inteção para a tela de cadastro.
    public static List<Contato> contatos; // Todos os contatos do usuário.
    private boolean conectado; // Verifica se está conectado com o broker

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout  tabLayout =  (TabLayout) findViewById(R.id.tabLayoutPrincipal);

        ViewPager   mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(new FragmentAdapter(this,getSupportFragmentManager()));

        tabLayout.setupWithViewPager(mViewPager);

        dataBaseChat = new DataBaseChat(this);

        conectado = false;
        contatos = new ArrayList<>();

        buscarMeuNumero();
        lerContatos();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_chat_mq   , menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(ConnectionRabbitmq.isIsConnected())
        {
            connectionRabbitmq.getSubscribeThread().interrupt();
            connectionRabbitmq.closeConection();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == CODIGO)
        {
            switch (resultCode)
            {
                case Activity.RESULT_CANCELED:
                    finish(); //Encerra a aplicação só permite uso, caso tenha o contato cadastrado.
                    break;

                case Activity.RESULT_OK:
                    String celular = data.getStringExtra("CELULAR");
                    new InserirContatoUsuario().execute(celular);
                    init();
                    Toast.makeText(this,celular,Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    /**
     * Inicializa as a conexão com o rabbitMQ
     */
    private void init()
    {
        if(!conectado)
        {

            final Handler handlerMessage = new Handler(Looper.getMainLooper())
            {
                @Override
                public void handleMessage(Message msg)
                {

                    if(ActivityChat.getHandlerMessageChat() != null)
                    {
                        Bundle bundle = new Bundle();
                        bundle.putString("msg",msg.getData().getString("msg"));
                        Message mens = ActivityChat.getHandlerMessageChat().obtainMessage();
                        mens.setData(bundle);
                        ActivityChat.getHandlerMessageChat().sendMessage(mens);
                    }

                }
            };

            connectionRabbitmq = ConnectionRabbitmq.getConnectionRabbitmq();
            connectionRabbitmq.setIsConnected(false);
            connectionRabbitmq.subscribe(handlerMessage);
            conectado = true;
        }
    }
    /**
     *
     * @return retorna o contato do usuário.
     */
    public static Contato getContato()
    {
        return contato;
    }

    private class InserirContatoUsuario extends AsyncTask<String,Void,Void>
    {
        protected Void doInBackground(String... strings)
        {
            SQLiteDatabase database = dataBaseChat.getWritableDatabase();

            DBHelper.insertMeuNumero(database,strings[0]);
            contato = new Contato(0,null,strings[0],strings[0]);
            return null;
        }
    }

    private void buscarMeuNumero()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor;
                SQLiteDatabase database = dataBaseChat.getReadableDatabase();
                cursor = database.query(DBHelper.TABLE_MEU_NUMERO,new String[]{"NUMERO"},"_id = 1",null,null,null,null);
                if(cursor.moveToFirst())
                {
                    contato = new Contato(0,null,cursor.getString(0),cursor.getString(0));
                    init();
                }else{
                    cursor.close();
                    startActivityForResult(new Intent(ActivityChatMQ.this,ActivityCadastro.class),CODIGO);
                }
            }
        }).start();
    }

    private void lerContatos()
    {
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    SQLiteDatabase database = dataBaseChat.getReadableDatabase();
                    Cursor cursor = database.query(DBHelper.TABLE_CONTATOS, new String[]{"_id","NOME","NUMERO_CEL","URL_IMAGE"},null,null,null,null,null);
                    if(cursor.moveToFirst())
                    {
                        Contato contato = new Contato(cursor);
                        contatos.add(contato);
                    }
                    while (cursor.moveToNext())
                    {
                        contatos.add(new Contato(cursor));

                    }
                    cursor.close();
                }
                catch (SQLiteException e)
                {
                    Toast.makeText(ActivityChatMQ.this,"Banco indisponível", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    public static DataBaseChat getDataBaseChat()
    {
        return dataBaseChat;
    }
}