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

import com.sd.adapter.ConversaAdpter;
import com.sd.chatmq.ActivityChat;
import com.sd.chatmq.R;
import com.sd.domain.Conversa;
import com.sd.domain.ConversaService;

import java.util.List;


public class conversas extends BaseFragment {

    protected RecyclerView recyclerView;

    private List<Conversa> conversas;
    private LinearLayoutManager mLinearLayoutManager;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_conversas,container,false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewConversa);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedIntenceState){
        super.onActivityCreated(savedIntenceState);
        taskConversa();
    }
    private void taskConversa(){
        this.conversas = ConversaService.getConversas(getContext());
        recyclerView.setAdapter(new ConversaAdpter(getContext(),conversas,onClickConversa()));
    }
    private ConversaAdpter.ConversaOnClickListener onClickConversa(){
        return new ConversaAdpter.ConversaOnClickListener() {
            @Override
            public void onClickConversa(View view, int idx) {
                Conversa v = conversas.get(idx);
                Intent intent = new Intent(getActivity(), ActivityChat.class);
                intent.putExtra("numeroCelular",v.getContato().getTelefone());
                startActivity(intent);
            }
        };
    }
}
