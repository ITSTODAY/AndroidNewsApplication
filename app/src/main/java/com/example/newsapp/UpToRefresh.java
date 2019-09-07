package com.example.newsapp;

import android.os.AsyncTask;
import android.widget.BaseAdapter;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.Date;

public class UpToRefresh extends AsyncTask {
    private PullToRefreshListView mPullRefreshListView;
    private basicAdapter mAdapter;
    private NewsCollection news;
    private int number;
    private Date startDate;
    private Date endDate;
    private String nowtype;
    private String keyword;

    public UpToRefresh(PullToRefreshListView listView,
                      basicAdapter adapter,NewsCollection news,int number,
                      Date start, Date end, String nowtype, String keyword) {
        mPullRefreshListView = listView;
        mAdapter = adapter;
        this.news = news;
        this.number = number;
        this.nowtype = nowtype;
        this.keyword = keyword;
        this.endDate = end;
        this.startDate = start;
    }


    @Override
    protected Object doInBackground(Object[] objects) {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        PullToRefreshBase.Mode mode = mPullRefreshListView.getCurrentMode();
        if(mode == PullToRefreshBase.Mode.PULL_FROM_START) {

        }
        else {
            NewsCollection newNews = NewsCollection.Request2News(new Request(20, startDate , new Date(), nowtype, keyword));
            mAdapter.ChangeIt(newNews);
        }
        // 加载完成后停止刷新
        mPullRefreshListView.onRefreshComplete();
    }
}
