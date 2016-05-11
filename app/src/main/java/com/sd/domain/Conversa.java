package com.sd.domain;

/**
 * Created by alkxyly on 02/05/16.
 */
public class Conversa {

  private long id;
  private Contato contato;

    public Conversa(long id, Contato contato) {
        this.id = id;
        this.contato = contato;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Contato getContato() {
        return contato;
    }

    public void setContato(Contato contato) {
        this.contato = contato;
    }
}
