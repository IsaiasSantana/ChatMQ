package com.sd.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sd.adapter.ContatosAdapter;
import com.sd.chatmq.ActivityChat;
import com.sd.chatmq.ActivityChatMQ;
import com.sd.chatmq.R;
import com.sd.domain.Contato;

import java.util.List;


public class Contatos extends BaseFragment
{

    private RecyclerView recyclerViewContatos;
    private List<Contato> contatosList;
    private LinearLayoutManager linearLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        recyclerViewContatos = (RecyclerView) view.findViewById(R.id.recyclerViewContatos);
        linearLayoutManager = new LinearLayoutManager(getActivity());

        recyclerViewContatos.setLayoutManager(linearLayoutManager);
        recyclerViewContatos.setItemAnimator(new DefaultItemAnimator());
        recyclerViewContatos.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedIntenceState)
    {
        super.onActivityCreated(savedIntenceState);
        taskConversa();
    }

    private void taskConversa()
    {
        this.contatosList = ActivityChatMQ.contatos;
        recyclerViewContatos.setAdapter(new ContatosAdapter(getContext(), contatosList,onClickContatos()));
    }

    private ContatosAdapter.ContatosOnClickListener onClickContatos()
    {
        return new ContatosAdapter.ContatosOnClickListener()
        {
            @Override
            public void onClickContato(View view, int idx)
            {
                Contato v = contatosList.get(idx);
                Intent intent = new Intent(getActivity(),ActivityChat.class);
                intent.putExtra("numeroCelular",v.getTelefone());
                startActivity(intent);
            }
        };
    }

}
