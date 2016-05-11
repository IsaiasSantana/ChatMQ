package com.sd.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sd.chatmq.R;
import com.sd.fragments.ChatTabFragment;

/**
 * Created by alkxyly on 28/04/16.
 */
public class TabsAdapter extends FragmentPagerAdapter {

    private Context context;

    public TabsAdapter(Context context,FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle args = new Bundle();
        if(position == 0)
            args.putString("tab","conversas");
        else
            args.putString("tab","Contatos");

        //Fragment f = new ChatFragment();
        //f.setArguments(args);


        return new  ChatTabFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }
    @Override
    public CharSequence getPageTitle(int position){
        if(position == 0){
            return context.getString(R.string.conversas);
        }else
            return context.getString(R.string.contatos);

    }
}
