package com.example.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HistoryAdapter extends BaseAdapter {
    private NewsCollection news;
    private Context mContext;

    HistoryAdapter(NewsCollection news, Context mContext){
        this.news = news;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        if(news==null){
            return 0;
        }
        return news.data.size();
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
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolderHistory holder = null;
        if(convertView == null){
            if(System.currentTimeMillis()%5==1 | System.currentTimeMillis()%5==3)
                convertView = LayoutInflater.from(mContext).inflate(R.layout.shortinfo,parent,false);
            else if(System.currentTimeMillis()%5==2 | System.currentTimeMillis()%5==4)
                convertView = LayoutInflater.from(mContext).inflate(R.layout.shortinfoc,parent,false);
            else
                convertView = LayoutInflater.from(mContext).inflate(R.layout.shortinfod,parent,false);

            holder = new ViewHolderHistory();

            holder.pic1 = (ImageView) convertView.findViewById(R.id.image_short);
            //holder.pic2 = (ImageView) convertView.findViewById(R.id.image_short2);
            //holder.pic3 = (ImageView) convertView.findViewById(R.id.image_short3);
            holder.title = (TextView) convertView.findViewById(R.id.title_short);
            holder.company = (TextView) convertView.findViewById(R.id.company_short);
            holder.id = (TextView) convertView.findViewById(R.id.newsid);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolderHistory) convertView.getTag();
        }

        String[] urls = getUrl(position);
        if(urls[0]==""){
            holder.pic1.setVisibility(ImageView.GONE);
        }
        else{
            holder.pic1.setVisibility(ImageView.VISIBLE);
            Glide
                    .with(mContext)
                    .load(urls[0])
                    .placeholder(R.drawable.picture_loading)
                    .skipMemoryCache(false)
                    .into((ImageView)holder.pic1);
        }

        holder.title.setText(this.news.data.get(position).title);
        holder.company.setText(this.news.data.get(position).publisher + " " + this.news.data.get(position).publishTime);
        holder.id.setText(this.news.data.get(position).newsID);

        convertView.setId(position);
        //System.out.println("dssdsdss");
        return convertView;
    }

    private String[] getUrl(int position){
        String[] result;
        News data = this.news.data.get(position);
        String images;
        try{
            images = data.image;
        }catch (Exception e){
            String[] temp = new String[1];
            temp[0] = "";
            return temp;
        }
        if(images == "" | images == null){
            result = new String[1];
            result[0] = "";
            return result;
        }
        Pattern p = Pattern.compile("\\[(.+)\\]");
        Matcher m = p.matcher(images);
        if(m.find()){
            images = m.group(1);
            //System.out.println(images);
        }else{
            result = new String[1];
            result[0] = "";
            return result;
        }
        if(images == ""){
            result = new String[1];
            result[0] = "";
            return result;
        }
        String[] image = images.split(",");
        //System.out.println(image[0]);

        //System.out.println(image[0]);
        if(image.length>=3){
            //System.out.println(image[0]);
            //System.out.println("yas");
            result = new String[3];
            result[0] = image[0];
            result[1] = image[1];
            result[2] = image[2];
        }
        else {
            result = new String[1];
            result[0] = image[0];
        }

        return result;
    }

    static class ViewHolderHistory{
        ImageView pic1;
        ImageView pic2;
        ImageView pic3;
        TextView title;
        TextView company;
        TextView id;
    }
}
