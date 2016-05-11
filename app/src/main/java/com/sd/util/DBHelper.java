package com.sd.util;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Isaías on 03/05/2016.
 */
public class DBHelper
{
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "CHAT_MESSAGE";
    public static final String TABLE_CONTATOS = "CONTATOS";
    public static final String TABLE_CONVERSAS = "CONVERSAS";
    public static final String TABLE_MEU_NUMERO = "MEU_NUMERO";

    public static final String CREATE_TABLE_CONTATOS = "CREATE TABLE "
                                                       +TABLE_CONTATOS
                                                       +"(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                                                       +"NOME TEXT, "
                                                       +"NUMERO_CEL TEXT," +
                                                        "URL_IMAGE TEXT);";

    public static final String CREATE_TABLE_CONVERSAS = "CREATE TABLE "
            +TABLE_CONVERSAS+" (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            +"NUM_REMETENTE TEXT, "
            +"NUM_DESTINATARIO TEXT, "
            +"MENSAGEM TEXT, "
            +"ORDEM_CONVERSA INTEGER, "
            +"HORA TEXT, " +
            "ENVIADA_POR NUMERIC );";


    public static final String CREATE_TABLE_MEU_NUMERO = "CREATE TABLE "+TABLE_MEU_NUMERO+"(_id INTEGER PRIMARY KEY AUTOINCREMENT, NUMERO TEXT);";


    public static void insertContact(SQLiteDatabase db,String nome, String contato,String urlImage)
    {
        ContentValues contactValues = new ContentValues();

        contactValues.put("NOME",nome);
        contactValues.put("NUMERO_CEL",contato);
        contactValues.put("URL_IMAGE",urlImage);

        db.insert(TABLE_CONTATOS,null,contactValues);
    }

    public static void salvarConversas(SQLiteDatabase db, String numeroQueEnvia, String numeroQueRecebe, String mensagem, boolean ladoConversa,String hora)
    {
        ContentValues conversa = new ContentValues();
        conversa.put("NUM_REMETENTE", numeroQueEnvia);
        conversa.put("NUM_DESTINATARIO",numeroQueRecebe);
        conversa.put("MENSAGEM",mensagem);
        conversa.put("ENVIADA_POR",ladoConversa);
        conversa.put("HORA",hora);

        db.insert(TABLE_CONVERSAS,null,conversa);
    }

    public static void insertMeuNumero(SQLiteDatabase db, String numero)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("NUMERO",numero);
        db.insert(TABLE_MEU_NUMERO,null,contentValues);
    }

    public static void salvarConversas(SQLiteDatabase db, String num_remetente,String numDestinatario,String mensagem, int ordemConversa,String hora, boolean enviadaPor)
    {
        ContentValues contentValuesConversas = new ContentValues();

        contentValuesConversas.put("NUM_REMETENTE",num_remetente);
        contentValuesConversas.put("NUM_DESTINATARIO",numDestinatario);
        contentValuesConversas.put("MENSAGEM",mensagem);
        contentValuesConversas.put("ORDEM_CONVERSA",ordemConversa);
        contentValuesConversas.put("HORA",hora);
        contentValuesConversas.put("ENVIADA_POR",enviadaPor);

        db.insert(TABLE_CONVERSAS,null,contentValuesConversas);
    }

    public static void updateURLImageTableContatos(SQLiteDatabase db,String urlImage)
    {
        ContentValues updates = new ContentValues();
    }


}
