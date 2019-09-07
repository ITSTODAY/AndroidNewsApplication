package com.example.newsapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class MyAdapter extends FragmentPagerAdapter {
    List<Fragment> fragments;//适配器
    List<String> titles;
    private int ID;

    public void setID(int id){
        this.ID =id;
    }

    public MyAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
        super(fm);
        this.fragments = fragments;
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);  //返回碎片集合的第几项
    }

    @Override
    public int getCount() {
        return fragments.size();    //返回碎片集合大小
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);    //返回标题的第几项
    }
}

