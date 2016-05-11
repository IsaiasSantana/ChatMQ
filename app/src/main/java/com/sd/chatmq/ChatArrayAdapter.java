package com.sd.chatmq;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Um adaptador para o ListView das mensagens.
 * @author Isaías Santana
 */
class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

    private TextView chatText, chatTime;
    private List<ChatMessage> chatMessageList = new ArrayList<>();
    private HashMap<String,List<ChatMessage>> conversas = new HashMap<>();
    private static int count = 1;

    public void incCount(){count ++;}

    private void setConversas(ChatMessage message)
    {

        if(conversas.isEmpty())
        {
           inserir(message);
        }
        else
        {
            if(conversas.containsKey(message.getRemetente()))
            {
                obterElemento(message);
            }
            else inserir(message);
        }
    }

    private void inserir(ChatMessage message)
    {
        List<ChatMessage> chatMessageList = new ArrayList<>();
        chatMessageList.add(message);
        conversas.put(message.getRemetente(),chatMessageList);
        this.chatMessageList = chatMessageList;
    }

    private void obterElemento(ChatMessage message)
    {
        List<ChatMessage> chatMessageList = conversas.get(message.getRemetente());
        chatMessageList.add(message);
        this.chatMessageList = chatMessageList;
        conversas.put(message.getRemetente(),chatMessageList);
    }

    @Override
    public void add(ChatMessage object)
    {
     //   setConversas(object);
        chatMessageList.add(object);
        super.add(object);

    }

    public ChatArrayAdapter(Context context, int textViewResourceId)
    {
        super(context, textViewResourceId);
    }

    public int getCount()
    {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index)
    {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        ChatMessage chatMessageObj = getItem(position);
        View row;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (chatMessageObj.isLeft())
            row = inflater.inflate(R.layout.right, parent, false);
        else
            row = inflater.inflate(R.layout.left, parent, false);

        chatText = (TextView) row.findViewById(R.id.msgr);
        chatTime = (TextView) row.findViewById(R.id.lbl1);
        chatText.setText(chatMessageObj.getMessage());
        chatTime.setText(chatMessageObj.getHora());
        return row;
    }
}