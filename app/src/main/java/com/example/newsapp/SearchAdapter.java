package com.example.newsapp;

import android.content.Context;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchAdapter extends BaseAdapter {
    private int ID;
    private User user;
    private ArrayList<String> data;
    private Context mContext;
    private SearchView sw;
    private String filter = "";

    public SearchAdapter(ArrayList<String> data, Context mContext, int ID, SearchView sw){
        this.data =data;
        this.mContext = mContext;
        this.ID = ID;
        user = new User();
        user.setID(ID);
        this.sw = sw;
    }

    static class SearchHolder{
        public TextView text;
        public ImageView image;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        SearchHolder holder = null;
        if(convertView==null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.search_little, parent, false);
            holder = new SearchHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.image_search);
            holder.text = (TextView) convertView.findViewById(R.id.text_search);
            convertView.setTag(holder);
        }else{
            holder = (SearchHolder) convertView.getTag();
        }
        if(!filter.equals("")){
            if(!data.get(position).contains(filter)){
                holder.image.setVisibility(View.GONE);
                holder.text.setVisibility(View.GONE);
                return convertView;
            }
        }
        holder.text.setVisibility(View.VISIBLE);
        holder.image.setVisibility(View.VISIBLE);

        holder.text.setText(data.get(position));
        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sw.setQuery(((TextView)view).getText(),false);
            }
        });

        holder.image.setAlpha(0.5f);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.deleteSearch(data.get(position));
                data.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    public void setFilterText(String str){
        this.filter = str;
        notifyDataSetChanged();
    }

    public void clearTextFilter(){
        this.filter = "";
        notifyDataSetChanged();
    }
}
