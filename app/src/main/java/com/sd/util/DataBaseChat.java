package com.sd.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Isaías on 03/05/2016.
 */
public class DataBaseChat extends SQLiteOpenHelper
{

    public DataBaseChat(Context context)
    {
        super(context,DBHelper.DB_NAME,null,DBHelper.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        updateDatabase(db,0,DBHelper.DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    private void initDB(SQLiteDatabase db)
    {
        db.execSQL(DBHelper.CREATE_TABLE_CONTATOS);
        DBHelper.insertContact(db,"Isaías Santana","79998319218","https://media.licdn.com/mpr/mpr/shrinknp_200_200/AAEAAQAAAAAAAAJ-AAAAJDMzYWJjOTQzLTViOGMtNDdiYy1iZDJmLTNiYzJkMmFlYmYwYw.jpg");
        DBHelper.insertContact(db,"Gilmar","799990799494","https://pbs.twimg.com/profile_images/731960109/Motumbo-ameditacao.jpg");
        DBHelper.insertContact(db,"Alkxyly Samyr","79998094028","https://bitbucket-assetroot.s3.amazonaws.com/c/photos/2016/Jan/31/1510303635-1-alkxyly-avatar.png");
        DBHelper.insertContact(db,"Tharlysson Breno","79998933449","https://img.new.livestream.com/accounts/00000000001b9886/90511baf-d205-4ec2-be95-da6b05c64c3e_170x170.jpg");
        DBHelper.insertContact(db,"Rafael Pereira","7981616618","https://pbs.twimg.com/profile_images/731960109/Motumbo-ameditacao.jpg");

        db.execSQL(DBHelper.CREATE_TABLE_CONVERSAS);
        db.execSQL(DBHelper.CREATE_TABLE_MEU_NUMERO);
    }

    private void updateDatabase(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if(oldVersion < newVersion)
        {
            initDB(db);
        }
         if(newVersion > oldVersion)
         {

         } //alguma atualização.
    }
}
