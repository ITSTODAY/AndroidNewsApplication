package com.example.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.format.DateUtils;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshWebView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;



import java.util.Arrays;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private PullToRefreshListView mPullRefreshListView;
    //普通的listview对象
    private ListView actualListView;
    //添加一个链表数组，来存放string数组，这样就可以动态增加string数组中的内容了
    private LinkedList<String> mListItems;
    //给listview添加一个普通的适配器
    private ArrayAdapter<String> mAdapter;

    //一个可以下拉刷新的listView对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        initView();
        mPullRefreshListView.setRefreshing(true);
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            startService(new Intent(getBaseContext(), Myservice.class));
        } else if (id == R.id.nav_gallery) {
            stopService(new Intent(getBaseContext(), Myservice.class));
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    private void initView() {
        initPTRListView();
        initListView();
    }

    /**
     * 设置下拉刷新的listview的动作
     */
    private void initPTRListView() {
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pullToRefresh);
        //设置拉动监听器
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                //设置下拉时显示的日期和时间
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                // 更新显示的label
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                // 开始执行异步任务，传入适配器来进行数据改变
                //new GetDataTask(mPullRefreshListView, mAdapter,mListItems).execute();
            }
        });

        // 添加滑动到底部的监听器
        mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                Toast.makeText(getApplication(), "已经到底了", Toast.LENGTH_SHORT).show();
            }
        });

        //mPullRefreshListView.isScrollingWhileRefreshingEnabled();//看刷新时是否允许滑动
        //在刷新时允许继续滑动
        mPullRefreshListView.setScrollingWhileRefreshingEnabled(true);
        //mPullRefreshListView.getMode();//得到模式
        //上下都可以刷新的模式。这里有两个选择：Mode.PULL_FROM_START，Mode.BOTH，PULL_FROM_END
        mPullRefreshListView.setMode(Mode.BOTH);

        /**
         * 设置反馈音效
         */
        SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(this);
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

        String []data = new String[] {"android","ios","wp","java","c++","c#"};
        mListItems = new LinkedList<String>();
        //把string数组中的string添加到链表中
        mListItems.addAll(Arrays.asList(data));

        mAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_list_item_1, mListItems);
        actualListView.setAdapter(mAdapter);
    }

}
