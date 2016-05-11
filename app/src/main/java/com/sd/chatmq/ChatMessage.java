package com.sd.chatmq;

/**
 * Created by Zeta Apponomics 3 on 24-11-2014.
 * Modified by Isaías Santana on 21-04-2016, on 07/05/2016
 * Esta classe representa as mensagens do chat e se são do remetente ou destinatário.
 */
public class ChatMessage {
    private boolean left; //Util para quando for salvar as mensagens no banco de dados.
    private String message; // A mensagem recibida ou enviada.
    private String remetente;
    private String hora;
    private int ordemMessage;
    /**
     *
     * @param left true se for o emissor que está enviando, false recebe do mensagem do destinatário
     * @param message a mensagem recebida ou enviada
     */
    public ChatMessage(boolean left, String message) {
        super();
        this.left = left;
        this.message = message;
    }

    public ChatMessage(boolean left, String message, String hora) {
        super();
        this.left = left;
        this.message = message;
        this.hora = hora;
    }


    public ChatMessage(boolean left, String message,int ordemMessage) {
        super();
        this.left = left;
        this.message = message;
        this.ordemMessage = ordemMessage;
    }


    public ChatMessage(boolean left, String message,int ordemMessage,String remetente)
    {
        super();
        this.left = left;
        this.message = message;
        this.ordemMessage = ordemMessage;
        this.remetente = remetente;
    }


    public boolean isLeft()
    {
        return left;
    }
    public String getMessage(){return message;}

    public String getRemetente() {
        return remetente;
    }

    public String getHora()
    {
        return hora;
    }

    public int getOrdemMessage()
    {
        return ordemMessage;
    }
}
