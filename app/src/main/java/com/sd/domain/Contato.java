package com.sd.domain;

import android.database.Cursor;

/**
 * Created by alkxyly on 02/05/16.
 */
public class Contato {
    private long id;
    private String nome;
    private String telefone;
    private String urlFoto;


    public Contato(long id, String urlFoto, String telefone, String nome) {
        this.id = id;
        this.urlFoto = urlFoto;
        this.telefone = telefone;
        this.nome = nome;
    }

    public Contato(Cursor cursor)
    {
        this.id = cursor.getInt(0);
        this.nome = cursor.getString(1);
        this.telefone = cursor.getString(2);
        this.urlFoto = cursor.getString(3);
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }
}
