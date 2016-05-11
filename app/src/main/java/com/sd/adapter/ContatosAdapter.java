package com.sd.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sd.chatmq.R;
import com.sd.domain.Contato;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Isaías on 04/05/2016.
 */
public class ContatosAdapter extends RecyclerView.Adapter<ContatosAdapter.ContatosViewHolder>
{
    private Context context;
    private ContatosOnClickListener contatosOnClickListener;
    private List<Contato> contatos;

    public ContatosAdapter(Context context, List<Contato> contatos, ContatosOnClickListener contatosOnClickListener) {
        this.context = context;
        this.contatosOnClickListener = contatosOnClickListener;
        this.contatos = contatos;
    }

    @Override
    public ContatosViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.adpter_conversa,parent,false);
        ContatosViewHolder holder = new ContatosViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ContatosViewHolder holder,final int position)
    {
        Contato contato = contatos.get(position);
        holder.contato.setText(contato.getNome());
        holder.progressBar.setVisibility(View.VISIBLE);

        Picasso.with(context).load(contato.getUrlFoto()).fit().into(holder.img, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                holder.progressBar.setVisibility(View.GONE);
            }
        });

        if(contatosOnClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contatosOnClickListener.onClickContato(holder.itemView,position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return this.contatos != null ? this.contatos.size() : 0;
    }

    public interface ContatosOnClickListener{
        void onClickContato(View view, int idx);
    }

    public static class ContatosViewHolder extends RecyclerView.ViewHolder
    {
        public TextView contato;
        public ImageView img;
        public ProgressBar progressBar;
        public CardView cardView;

        public ContatosViewHolder(View itemView)
        {
            super(itemView);
            contato = (TextView) itemView.findViewById(R.id.nomeContato);
            img = (ImageView) itemView.findViewById(R.id.imgContato);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressImg);
            cardView = (CardView) itemView.findViewById(R.id.card_view_conversa);
        }
    }
}
