package com.example.newsapp;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.view.menu.MenuView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.AbsListView;
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


import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private ViewPager my_viewpager;
    private TabLayout my_tab;
    private List<Fragment> fragments;
    private List<String> titles;
    private MyAdapter mAdapter;
    private Context mContext;
    private String user = null;
    private MenuItem nowLogin;
    private Toolbar myToolbar;
    private int ID = -1;
    private int tag = 0;
    private String[] myCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FileHelper.fileHelper = new FileHelper(getApplicationContext());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,SearchActivity.class);
                intent.putExtra("ID",ID);
                startActivity(intent);
            }
        });

        nowLogin = (MenuItem) navigationView.getCheckedItem();

        this.mContext = MainActivity.this;
        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        intent.putExtra("first",1);
        startActivityForResult(intent,1);
    }

    private void initViewTab(){
        my_tab=(TabLayout) findViewById(R.id.my_tab);
        my_viewpager=(ViewPager) findViewById(R.id.my_viewpager);
        fragments=new ArrayList<>();       //碎片的集合
        fragments.add(new Page().setType("主页").setUp(mContext).setUser(ID));
        for(String cate:myCategory){
            fragments.add(new Page().setType(cate).setUp(mContext).setUser(ID));
        }
        titles=new ArrayList<>();
        titles.add("主页");//标题的集合
        for(String title:myCategory){
            titles.add(title);
        }

        MyAdapter adapter=new MyAdapter(getSupportFragmentManager(),fragments, titles);
        my_viewpager.setAdapter(adapter);
        my_tab.setupWithViewPager(my_viewpager);

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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) { // Handle navigation view item clicks here.
        int id = item.getItemId();

        item.setChecked(false);
        if (id == R.id.LogIn) {
            if(this.user!=null){
                Toast.makeText(mContext,"你已经登陆了，亲爱的"+this.user,Toast.LENGTH_LONG);
            }else {
                nowLogin = item;
                Intent intent = new Intent(mContext, LoginActivity.class);
                intent.putExtra("first",0);
                startActivityForResult(intent, 1);
            }
        }else if(id == R.id.DailyNew){
            TabLayout tlo = (TabLayout)findViewById(R.id.my_tab);
            tlo.getTabAt(0).select();
        }else if(id == R.id.History){
            //System.out.println("dss");
            Intent intent = new Intent(MainActivity.this,HistoryActivity.class);
            intent.putExtra("ID",this.ID);
            intent.putExtra("isForYou",0);
            startActivity(intent);
        }else if(id == R.id.ForYou){
            Intent intent = new Intent(MainActivity.this,HistoryActivity.class);
            intent.putExtra("ID",this.ID);
            intent.putExtra("isForYou",1);
            startActivity(intent);
        }else if(id == R.id.Collection){
            Intent intent = new Intent(MainActivity.this,HistoryActivity.class);
            intent.putExtra("ID",this.ID);
            intent.putExtra("isForYou",130);
            startActivity(intent);
        }else if(id == R.id.Setting){
            Intent intent = new Intent(MainActivity.this,SettingActivity.class);
            intent.putExtra("ID",this.ID);
            startActivityForResult(intent,1);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==10086){
            User usr = new User();
            usr.setID(ID);
            myCategory = usr.GetCategory();
            //ChangeIt();
            initViewTab();
            return;
        }
        if(resultCode==1998&&tag==0){
            try{
                tag=1;
                String result = data.getExtras().getString("result");
                this.user = result;
                String Hello = "Hello, "+this.user+"!";
                Toast.makeText(mContext, Hello, Toast.LENGTH_LONG).show();
                this.ID = data.getExtras().getInt("ID");
                System.out.println("dsdsdsdsdsdsdsds" + this.ID);
                //tag = 1;
                User usr = new User();
                usr.setID(ID);
                myCategory = usr.GetCategory();
                initViewTab();
                nowLogin.setTitle(Hello);
                Intent intent = new Intent(mContext,Myservice.class);
                startService(intent);
                return;
            }catch(Exception e){return;}
        }
        if(resultCode==1998){
            try{
                String result = data.getExtras().getString("result");
                this.user = result;
                String Hello = "Hello, "+this.user+"!";
                nowLogin.setTitle(Hello);
                myToolbar = findViewById(R.id.toolbar);
                myToolbar.setTitle("Daily News For "+user);
                Toast.makeText(mContext, Hello, Toast.LENGTH_LONG).show();
                this.ID = data.getExtras().getInt("ID");
                User usr = new User();
                usr.setID(ID);
                myCategory = usr.GetCategory();
                initViewTab();
            }catch(Exception e){}
        }

    }


}
