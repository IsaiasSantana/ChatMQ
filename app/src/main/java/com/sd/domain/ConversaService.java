package com.sd.domain;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alkxyly on 02/05/16.
 */
public class ConversaService {

    public static List<Conversa> getConversas(Context context){
        List<Conversa> conversas = new ArrayList<>();

        Contato alkxyly_samyr = new Contato(1,"https://bitbucket-assetroot.s3.amazonaws.com/c/photos/2016/Jan/31/1510303635-1-alkxyly-avatar.png","79998094028", "Alkxyly Samyr");

        Contato isaias  = new Contato(2,"https://media.licdn.com/mpr/mpr/shrinknp_200_200/AAEAAQAAAAAAAAJ-AAAAJDMzYWJjOTQzLTViOGMtNDdiYy1iZDJmLTNiYzJkMmFlYmYwYw.jpg","79998319218", "Isaias Santana");

        Contato motumbuu = new Contato(3,"https://pbs.twimg.com/profile_images/731960109/Motumbo-ameditacao.jpg","7981616618", "Motumbu");

        Contato tharlysson = new Contato(4,"https://img.new.livestream.com/accounts/00000000001b9886/90511baf-d205-4ec2-be95-da6b05c64c3e_170x170.jpg","79998933449", "Tharlysson");

        Contato icaro = new Contato(5,"https://lh3.googleusercontent.com/-q4_-BEQ4oAQ/AAAAAAAAAAI/AAAAAAAAAAA/Ul-WNlnUZz8/photo.jpg","079 991254487", "Icaro leilinha");

        Conversa conversa1 = new Conversa(1,alkxyly_samyr);
        Conversa conversa2 = new Conversa(2,isaias);
        Conversa conversa3 = new Conversa(3,motumbuu);
        Conversa conversa4 = new Conversa(4,tharlysson);
        Conversa conversa5 = new Conversa(5,icaro);

        conversas.add(conversa1);
        conversas.add(conversa2);
        conversas.add(conversa3);
        conversas.add(conversa4);
        conversas.add(conversa5);


        return conversas;
    }
}
