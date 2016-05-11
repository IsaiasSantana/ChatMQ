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
import com.sd.domain.Conversa;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by alkxyly on 02/05/16.
 */
public class ConversaAdpter extends RecyclerView.Adapter<ConversaAdpter.ConversaViewHolder> {
    protected  static  final String TAG ="chatMQ";
    private final List<Conversa> conversas;
    private Context context;
    private ConversaOnClickListener conversaOnClickListener;

    public ConversaAdpter(Context context,List<Conversa> conversas,ConversaOnClickListener conversaOnClickListener) {
        this.context = context;
        this.conversaOnClickListener = conversaOnClickListener;
        this.conversas = conversas;
    }

    @Override
    public ConversaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adpter_conversa,parent,false);
        ConversaViewHolder holder = new ConversaViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ConversaViewHolder holder, final int position) {
        Conversa c = conversas.get(position);
        holder.contato.setText(c.getContato().getNome());
        holder.progressBar.setVisibility(View.VISIBLE);

        Picasso.with(context).load(c.getContato().getUrlFoto()).fit().into(holder.img, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                holder.progressBar.setVisibility(View.GONE);
            }
        });

        if(conversaOnClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    conversaOnClickListener.onClickConversa(holder.itemView,position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.conversas != null ? this.conversas.size() : 0;
    }
    public interface ConversaOnClickListener{
        public void onClickConversa(View view,int idx);
    }

    public static class ConversaViewHolder extends RecyclerView.ViewHolder{
        public TextView contato;
        public ImageView img;
        public ProgressBar progressBar;
        public CardView cardView;

        public ConversaViewHolder(View itemView) {
            super(itemView);
            contato = (TextView) itemView.findViewById(R.id.nomeContato);
            img = (ImageView) itemView.findViewById(R.id.imgContato);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressImg);
            cardView = (CardView) itemView.findViewById(R.id.card_view_conversa);
        }
    }
}

