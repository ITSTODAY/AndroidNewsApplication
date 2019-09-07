package com.example.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class AllAdapter extends BaseAdapter {
    private String[] all;
    private String[] image;
    private Context mContext;
    private int lengthAll;


    public AllAdapter(String[] image, String[] all, Context mContext){
        this.all = all;
        this.image = image;
        this.mContext = mContext;
        if(image==null){
            image = new String[1];
            image[0] = "";
            lengthAll = all.length;
        }else{
            lengthAll =  all.length + image.length;
        }
        System.out.println("aaaaaaaa"+all.length);
        System.out.println("bbbbbbbb"+image.length);
    }

    @Override
    public int getCount() {
        return lengthAll;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    //flag 0 text 1 image;

    @Override
    public View getView(int position, View convertView, ViewGroup parents) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            int flag = 1;
            if(flag==1){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.text_layout,parents,false);
            }else{
                convertView = LayoutInflater.from(mContext).inflate(R.layout.text_layout,parents,false);
            }
            holder.image = (ImageView) convertView.findViewById(R.id.image_show);
            holder.text = (TextView) convertView.findViewById(R.id.text_show);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(position/2>=image.length){
            holder.text.setText(all[position-image.length]);
            holder.text.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.GONE);
            //System.out.println(all[position-image.length]);
        }else if((position+1)/2>=all.length && position!=this.getCount()-1){
            if(image[position-all.length+1].replaceAll(" ","").equals("")){
                holder.image.setVisibility(View.GONE);
                holder.text.setVisibility(View.GONE);
                return convertView;
            }
            Glide
                    .with(mContext)
                    .load(image[position-all.length+1].replaceAll(" ",""))
                    .placeholder(R.drawable.picture_loading)
                    //.skipMemoryCache(false)
                    .into((ImageView)holder.image);
            holder.image.setVisibility(View.VISIBLE);
            holder.text.setVisibility(View.GONE);
        }else if(position==this.getCount()-1){
            holder.text.setText(all[all.length-1]);
            holder.text.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.GONE);
            //System.out.println(all[all.length-1]);
        }else if(position%2==0){
            if(image[position/2].replaceAll(" ","").equals("")){
                holder.image.setVisibility(View.GONE);
                holder.text.setVisibility(View.GONE);
                return convertView;
            }
            Glide
                    .with(mContext)
                    .load(image[position/2].replaceAll(" ",""))
                    .placeholder(R.drawable.picture_loading)
                    //.skipMemoryCache(false)
                    .into((ImageView)holder.image);
            holder.image.setVisibility(View.VISIBLE);
            holder.text.setVisibility(View.GONE);
        }else{
            holder.text.setText(all[position/2]);
            holder.text.setVisibility(View.VISIBLE);
            holder.image.setVisibility(View.GONE);
            //System.out.println(all[position/2]);
        }

        /*if(position<image.length){
            Glide
                    .with(mContext)
                    .load(image[position].replaceAll(" ",""))
                    .placeholder(R.drawable.picture_loading)
                    .skipMemoryCache(false)
                    .into((ImageView)holder.image);

            System.out.println(image[position]);

        }
        else{
            holder.text.setText(all);
            holder.text.setVisibility(View.VISIBLE);
        }*/

        //System.out.println("nnnnnn"  + position);
        //System.out.println("mmmm" + getCount());

        return convertView;
    }

    static class ViewHolder{
        ImageView image;
        TextView text;
    }

}
