package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;

import java.util.Date;
import java.util.LinkedList;

public class Page extends Fragment {
    private int user = -1;
    private String type;
    private PullToRefreshListView mPullRefreshListView;//普通的listview对象
    private ListView actualListView;//添加一个链表数组，来存放string数组，这样就可以动态增加string数组中的内容了
    private LinkedList<String> mListItems;//给listview添加一个普通的适配器
    private basicAdapter mAdapter = null; //一个可以下拉刷新的listView对象
    private NewsCollection news;
    private Date startData;
    private Date endDate;
    private Context mContext;
    private int newsnumber;
    private String keyword = null;

    public Page setUp(Context mContext){
        this.mContext = mContext;
        return this;
    }

    public Page setType(String type) {
        this.type = type;
        if(this.type == "主页"){
            this.type = null;
        }
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.pull_to_refresh,container,false);
        this.mPullRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pullToRefresh);
        initBasic();
        initView();
        return view;
    }

    private void initBasic(){
        //long l = System.currentTimeMillis();
        this.startData = new Date(0);
        this.endDate = new Date();
        User usr = new User();
        usr.setID(user);
        this.news = usr.Request2News(new Request(20, startData, endDate,null, type));
        newsnumber = 20;
        //System.out.println("nnnnnnnnn"+this.news.total);
    }

    private void initView() {
        initPTRListView();
        initListView();
    }

    private void initPTRListView() {
        /*mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {  //设置下拉时显示的日期和时间
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL); // 更新显示的label
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label); // 开始执行异步任务，传入适配器来进行数据改变 //new GetDataTask(mPullRefreshListView, mAdapter,mListItems).execute();
            }
        });*/
        // 添加滑动到底部的监听器
        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                new DownToLoad(mPullRefreshListView, mAdapter, news, newsnumber, startData, endDate, type, keyword, user).execute();
                newsnumber = 20;
                endDate = new Date();
                //news = mAdapter.getNews();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                new DownToLoad(mPullRefreshListView, mAdapter, news, newsnumber, startData, endDate, type, keyword, user).execute();
                newsnumber+=20;
                //news = mAdapter.getNews();
            }
        });
        //mPullRefreshListView.setOnRefreshListener(new MainLastListener(this));
        //mPullRefreshListView.setScrollingWhileRefreshingEnabled(true);
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

        /**
         * 设置反馈音效
         */
        SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(this.mContext);
        //soundListener.addSoundEvent(State.PULL_TO_REFRESH, R.raw.pull_event);
        //soundListener.addSoundEvent(State.RESET, R.raw.reset_sound);
        //soundListener.addSoundEvent(State.REFRESHING, R.raw.refreshing_sound);
        mPullRefreshListView.setOnPullEventListener(soundListener);
    }

    /**
     * 设置listview的适配器
     */
    private void initListView() {
        //通过getRefreshableView()来得到一个listview对象
        actualListView = mPullRefreshListView.getRefreshableView();
        mAdapter = new basicAdapter(this.news,this.mContext);
        mAdapter.setUser(this.user);
        actualListView.setAdapter(mAdapter);
        actualListView.setOnItemClickListener(new OnClickOnNews());
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
        for(News newss: news.data){
            if(newss.newsID==newsId){
                thisNews = newss;
                break;
            }
        }
        if(thisNews==null) return;
        User usr = new User();
        usr.setID(user);
        if(!usr.isBrowsed(thisNews.newsID)){
            view.setAlpha(0.5f);
        }
        usr.addBrowse(thisNews);
        Intent intent = new Intent(this.mContext, AllActivity.class);
        intent.putExtra("image",thisNews.image);
        intent.putExtra("publishTime",thisNews.publishTime);
        intent.putExtra("video",thisNews.video);
        intent.putExtra("title",thisNews.title);
        intent.putExtra("content",thisNews.content);
        intent.putExtra("newsID",thisNews.newsID);
        intent.putExtra("publisher",thisNews.publisher);
        intent.putExtra("category",thisNews.category);
        intent.putExtra("ID",this.user);
        startActivityForResult(intent,1);
    }

    public Page setUser(int user){
        this.user = user;
        if(mAdapter!=null)
            mAdapter.setUser(user);
        return this;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==130){
            try{
                int isCollect = data.getExtras().getInt("collected");
                String newsID = data.getExtras().getString("newsID");
                if(isCollect==1){
                    System.out.println("I'm in this 1");
                    User me = new User();
                    me.setID(user);
                    if(me.isFavorite(newsID)){
                        return;
                    }else{
                        News temp = null;
                        for(News newss: news.data){
                            if(newss.newsID.equals(newsID)){
                                temp = newss;
                                break;
                            }
                        }
                        if(temp!=null){
                            me.addFavorite(temp);
                            System.out.println("setting!!!!!");
                        }
                    }
                }else{
                    System.out.println("I'm in this 2");
                    User me = new User();
                    me.setID(user);
                    if(!me.isFavorite(newsID)){
                        return;
                    }else{
                        News temp = null;
                        for(News newss: news.data){
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
