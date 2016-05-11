package com.sd.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sd.chatmq.R;
import com.sd.fragments.Contatos;
import com.sd.fragments.conversas;

/**
 * Created by alkxyly on 29/04/16.
 */
public class FragmentAdapter extends FragmentPagerAdapter{
    private Context context;
    public FragmentAdapter(Context context,FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0)
                return new conversas();
        else if(position == 1)
                return new Contatos();
        else return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0){
            return context.getString(R.string.conversas);
        }else
            return context.getString(R.string.contatos);
    }
}
