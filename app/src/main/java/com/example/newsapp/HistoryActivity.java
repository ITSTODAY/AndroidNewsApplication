package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.githang.statusbar.StatusBarCompat;

import java.util.Date;

public class HistoryActivity  extends AppCompatActivity {
    private int ID;
    private Context mContext;
    private HistoryAdapter mAdapter;
    private User user;
    private NewsCollection newsCollection;
    private int isForYou = 0;
    private String keyword;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);
        //System.out.println("ininini");
        Intent intent = getIntent();
        ID = intent.getExtras().getInt("ID");

        try{
            isForYou = intent.getExtras().getInt("isForYou");
        }catch (Exception e){
            isForYou = 0;
        }
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.colorPrimary));


        System.out.println("this ID is "+ID);
        user = new User();
        user.setID(ID);
        if(isForYou==0){
            newsCollection = user.GetBrowseHistory();
            mAdapter = new HistoryAdapter(newsCollection, HistoryActivity.this);
            ListView View = (ListView)findViewById(R.id.historyView);
            View.setAdapter(mAdapter);
            View.setOnItemClickListener(new OnClickOnNews());
        }else if(isForYou==1){
            //System.out.println("dsdsddsdsds+ssdsdssds");
            newsCollection = user.GetRecommendNews();
            mAdapter = new HistoryAdapter(newsCollection, HistoryActivity.this);
            ListView View = (ListView)findViewById(R.id.historyView);
            View.setAdapter(mAdapter);
            View.setOnItemClickListener(new OnClickOnNews());
        }else if(isForYou==2){
            keyword = intent.getExtras().getString("key");
            newsCollection = NewsCollection.Request2News(new Request(100, new Date(0),new Date(),keyword,null));
            mAdapter = new HistoryAdapter(newsCollection, HistoryActivity.this);
            ListView View = (ListView)findViewById(R.id.historyView);
            View.setAdapter(mAdapter);
            View.setOnItemClickListener(new OnClickOnNews());
        }else if(isForYou==130){
            newsCollection = user.GetFavorite();
            mAdapter = new HistoryAdapter(newsCollection, HistoryActivity.this);
            ListView View = (ListView)findViewById(R.id.historyView);
            View.setAdapter(mAdapter);
            View.setOnItemClickListener(new OnClickOnNews());
        }
    }

    class OnClickOnNews implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            TextView idText = (TextView)view.findViewById(R.id.newsid);
            String newsId = idText.getText().toString();
            showNewsPages(newsId,view);
        }
    }

    void showNewsPages(String newsId,View view){
        News thisNews = null;
        for(News newss: newsCollection.data){
            if(newss.newsID==newsId){
                thisNews = newss;
                break;
            }
        }
        if(thisNews==null) return;
        User usr = new User();
        usr.setID(ID);
        usr.addBrowse(thisNews);
        if(this.isForYou==1){
            view.setAlpha(0.5f);
        }
        Intent intent = new Intent(this, AllActivity.class);
        intent.putExtra("image",thisNews.image);
        intent.putExtra("publishTime",thisNews.publishTime);
        intent.putExtra("video",thisNews.video);
        intent.putExtra("title",thisNews.title);
        intent.putExtra("content",thisNews.content);
        intent.putExtra("newsID",thisNews.newsID);
        intent.putExtra("publisher",thisNews.publisher);
        intent.putExtra("category",thisNews.category);
        intent.putExtra("ID",this.ID);
        startActivityForResult(intent,1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==130){
            try{
                int isCollect = data.getExtras().getInt("collected");
                String newsID = data.getExtras().getString("newsID");
                if(isCollect==1){
                    User me = new User();
                    me.setID(ID);
                    if(me.isFavorite(newsID)){
                        return;
                    }else{
                        News temp = null;
                        for(News newss: newsCollection.data){
                            if(newss.newsID.equals(newsID)){
                                temp = newss;
                                break;
                            }
                        }
                        if(temp!=null){
                            me.addFavorite(temp);
                        }
                    }
                }else{
                    User me = new User();
                    me.setID(ID);
                    if(!me.isFavorite(newsID)){
                        return;
                    }else{
                        News temp = null;
                        for(News newss: newsCollection.data){
                            if(newss.newsID.equals(newsID)){
                                temp = newss;
                                break;
                            }
                        }
                        if(temp!=null){
                            me.deleteFavorite(newsID);
                        }
                    }
                }
            }catch (Exception e){
                return;
            }
        }
    }
}
