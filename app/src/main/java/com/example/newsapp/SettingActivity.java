package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.WindowManager;

import com.githang.statusbar.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity implements ChannelAdapter.onItemRangeChangeListener {

    private RecyclerView mRecyclerView;
    private User user;
    private int ID;
    private List<ChannelBean> mList;
    private ChannelAdapter mAdapter;
    private String all[] = {"娱乐","军事","教育","文化","健康","财经","体育","汽车","科技","社会"};
    private String select[];
    private String recommend[];
    private String city[] = {""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.colorPrimary));

        Intent mee = new Intent();
        this.setResult(10086, mee);

        Intent intent = getIntent();
        ID = intent.getExtras().getInt("ID");
        user = new User();
        user.setID(ID);

        String[] reselect = user.GetCategory();
        select = new String[reselect.length+1];
        select[0] = "主页";
        int placebo = 1;
        for(String tag:reselect){
            select[placebo] = tag;
            placebo++;
        }


        recommend = new String[all.length-reselect.length];
        int place = 0;
        for(String tag:all){
            boolean flag = true;
            for(String tagg:select){
                if(tag.equals(tagg)){
                    flag = false;
                    break;
                }
            }
            if(flag){
                recommend[place] = tag;
                place++;
            }
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        mList = new ArrayList<>();
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mList.get(position).getSpanSize();
            }
        });
        mRecyclerView.setLayoutManager(manager);
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setMoveDuration(300);     //设置动画时间
        animator.setRemoveDuration(0);
        mRecyclerView.setItemAnimator(animator);
        ChannelBean title = new ChannelBean();
        title.setLayoutId(R.layout.adapter_title);
        title.setSpanSize(4);
        mList.add(title);
        for (String bean : select) {
            mList.add(new ChannelBean(bean, 1, R.layout.adapter_channel, true));
        }
        ChannelBean tabBean = new ChannelBean();
        tabBean.setLayoutId(R.layout.adapter_tab);
        tabBean.setSpanSize(4);
        mList.add(tabBean);
        List<ChannelBean> recommendList = new ArrayList<>();
        for (String bean : recommend) {
            recommendList.add(new ChannelBean(bean, 1, R.layout.adapter_channel, true));
        }
        List<ChannelBean> cityList = new ArrayList<>();
        for (String bean : city) {
            cityList.add(new ChannelBean(bean, 1, R.layout.adapter_channel, false));
        }
        ChannelBean moreBean = new ChannelBean();
        moreBean.setLayoutId(R.layout.adapter_more_channel);
        moreBean.setSpanSize(4);
        //cityList.add(moreBean);
        mList.addAll(recommendList);
        mAdapter = new ChannelAdapter(this, mList, recommendList, cityList, ID);
        mAdapter.setFixSize(1);
        mAdapter.setSelectedSize(select.length);
        mAdapter.setRecommend(true);
        mAdapter.setOnItemRangeChangeListener(this);
        mRecyclerView.setAdapter(mAdapter);
        WindowManager m = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int spacing = (m.getDefaultDisplay().getWidth() - dip2px(this, 70) * 4) / 5;
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(4,spacing,true));
        ItemDragCallback callback=new ItemDragCallback(mAdapter,2);
        ItemTouchHelper helper=new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecyclerView);
    }

    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void refreshItemDecoration() {
        mRecyclerView.invalidateItemDecorations();
    }
}
