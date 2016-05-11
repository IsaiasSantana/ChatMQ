package com.sd.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sd.adapter.TabsAdapter;
import com.sd.chatmq.R;

/**
 * Created by alkxyly on 28/04/16.
 */
public class ChatTabFragment extends BaseFragment implements TabLayout.OnTabSelectedListener{

    private ViewPager mViewPager;
    private TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_chat_tab,container,false);

        //ViewPager
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(new TabsAdapter(getContext(),getChildFragmentManager()));

        //Tabs
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        int cor = getContext().getResources().getColor(R.color.white);

        tabLayout.setTabTextColors(cor,cor);

        //adicionando as tabs
        tabLayout.addTab(tabLayout.newTab().setText("Conversas"));
        tabLayout.addTab(tabLayout.newTab().setText("Contatos"));

        //Listener para tratar eventos de clique
        tabLayout.setOnTabSelectedListener(this);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        return view;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
