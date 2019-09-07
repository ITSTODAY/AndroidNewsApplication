package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newsapp.wxapi.WXEntryActivity;
import com.githang.statusbar.StatusBarCompat;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.tencent.mm.opensdk.utils.Log;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.wechat.friends.Wechat;

public class AllActivity extends AppCompatActivity {
    private String image;
    private String publishtime;
    private String video;
    private String title;
    private String content;
    private String newsID;
    private String publisher;
    private String category;
    private AllAdapter adapterMain;
    private Context mContext;
    private ListView myView;
    private String[] all;
    private String[] images;
    private TextView titleV;
    private TextView companyV;
    private ImageView returning;
    private ImageView sharing;
    private ImageView collect;
    private int ID;
    private User user;
    private boolean isCollected;
    private Intent intent;
    private StandardGSYVideoPlayer player;

    private void onDownLoad(String url) {
        DownLoadImageService service = new DownLoadImageService(getApplicationContext(),
                url,
                new ImageDownLoadCallBack() {

                    @Override
                    public void onDownLoadSuccess(File file) {
                    }
                    @Override
                    public void onDownLoadSuccess(Bitmap bitmap) {
                        // 在这里执行图片保存方法
                    }

                    @Override
                    public void onDownLoadFailed() {
                        // 图片保存失败
                    }
                });
        //启动图片下载线程
        new Thread(service).start();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_layout);
        Intent intent = getIntent();
        image = intent.getStringExtra("image");
        publishtime = intent.getStringExtra("publishTime");
        video = intent.getStringExtra("video");
        title = intent.getStringExtra("title");
        content = intent.getStringExtra("content");
        newsID = intent.getStringExtra("newsID");
        publisher = intent.getStringExtra("publisher");
        category = intent.getStringExtra("category");
        ID = intent.getExtras().getInt("ID");
        user = new User();
        user.setID(ID);
        mContext = AllActivity.this;

        //System.out.println(all[0]);
        Pattern p = Pattern.compile("\\[(.+)\\]");
        Matcher m = p.matcher(image);
        String temp;
        if(m.find()){
            temp = m.group(1);
        }else{
            temp = "";
        }
        if(temp==""){
            image = null;
        }
        images = temp.split(",");
        if(images.length>0){
            onDownLoad(images[0]);
        }
        adapterMain = new AllAdapter(images, content.split("\n\n"), mContext);

        titleV = (TextView) findViewById(R.id.title_all);
        companyV = (TextView) findViewById(R.id.company_all);
        player = (StandardGSYVideoPlayer) findViewById(R.id.video_player);
        video = video.replaceAll("\\[","");
        video = video.replaceAll(" ","");
        if(video.equals(""))
        {
            player.setVisibility(View.GONE);
        }
        else{
            player.setUpLazy(video,true,null,null,title);
            player.getTitleTextView().setVisibility(View.GONE);
            player.getBackButton().setVisibility(View.GONE);
            player.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    player.startWindowFullscreen(mContext, false, true);
                }
            });
            //player.setPlayTag();
        }

        titleV.setText(title);
        companyV.setText(publisher);



        myView = (ListView) findViewById(R.id.all_the_content);
        adapterMain = new AllAdapter(images, content.split("\n\n"), mContext);
        myView.setAdapter(adapterMain);

        returning = (ImageView) findViewById(R.id.returnit);
        sharing = (ImageView) findViewById(R.id.share);
        collect = (ImageView) findViewById(R.id.Collect);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.colorPrimary));

        init3View();
    }

    private void init3View(){
        returning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //share
        sharing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final OnekeyShare oks = new OnekeyShare();
                String platform = Wechat.NAME;
                //指定分享的平台，如果为空，还是会调用九宫格的平台列表界面
                if (platform != null) {
                    oks.setPlatform(platform);
                }
                //关闭sso授权
                //oks.disableSSOWhenAuthorize();
                // text是分享文本，所有平台都需要这个字段
                oks.setTitle(content.substring(0,30)+"……");
                //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
                if(images.length>0)
                    oks.setImageUrl(images[0].replaceAll(" ",""));
                // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
                // url仅在微信（包括好友和朋友圈）中使用
                //oks.setUrl("http://sharesdk.cn");

                oks.setCallback(new PlatformActionListener() {
                    @Override
                    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                         Log.i("sss","onComplete");
                    }

                    @Override
                    public void onError(Platform platform, int i, Throwable throwable) {

                    }

                    @Override
                    public void onCancel(Platform platform, int i) {

                    }
                });
                //启动分享
                oks.show(AllActivity.this);
            }
        });
        //collect
        if(user.isFavorite(this.newsID)){
            collect.setImageResource(R.drawable.shoucangshine);
            isCollected = true;
            intent = new Intent();
            intent.putExtra("collected",1);
            intent.putExtra("newsID",newsID);
            AllActivity.this.setResult(130, intent);
        }else{
            isCollected = false;
            intent = new Intent();
            intent.putExtra("collected",0);
            intent.putExtra("newsID",newsID);
            AllActivity.this.setResult(130, intent);
        }
        collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isCollected){
                    //user.deleteFavorite(newsID);
                    isCollected = false;
                    collect.setImageResource(R.drawable.shoucang);
                    Toast.makeText(mContext,"取消收藏",Toast.LENGTH_LONG).show();
                    intent = new Intent();
                    intent.putExtra("collected",0);
                    intent.putExtra("newsID",newsID);
                    AllActivity.this.setResult(130, intent);
                }else{
                    /*
                    News tempt = new News();
                    tempt.category = category;
                    tempt.newsID = newsID;
                    tempt.image = image;
                    tempt.content = content;
                    tempt.title = title;
                    tempt.video = video;
                    tempt.content = content;
                    tempt.publisher = publisher;
                    tempt.publishTime = publishtime;
                    */
                    //user.addFavorite(tempt);
                    isCollected = true;
                    collect.setImageResource(R.drawable.shoucangshine);
                    Toast.makeText(mContext,"收藏成功",Toast.LENGTH_LONG).show();
                    intent = new Intent();
                    intent.putExtra("collected",1);
                    intent.putExtra("newsID",newsID);
                    AllActivity.this.setResult(130, intent);
                }
            }
        });
    }
}