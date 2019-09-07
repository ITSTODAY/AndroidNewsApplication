package com.example.newsapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.orm.SugarRecord.find;

public class Myservice extends Service {

    private User user;
    private List<News> newsList;
    Random random = new Random();

    public class APIBinder extends Binder {
        public Myservice getService(){
            return Myservice.this;
        }
        public NewsCollection Request2News(Request request){
            NewsCollection newsCollection = NewsCollection.Request2News(request);
            try {
                newsList = newsCollection.data;
            }catch (Exception e){
                newsList = null;
            }
            return newsCollection;
        }
        public NewsCollection GetBrowseHistory(){
            List<Record> browseRecords = Record.find(Record.class, "User = ? and type = ?", String.valueOf(user.getId()), "browse");
            newsList.clear();
            for(Record record:browseRecords){
                newsList.add(News.Json2News(record.newsID,1));
            }
            System.out.println(newsList.size());
            return new NewsCollection(newsList);
        }
        public boolean addBrowse(String newsId){
            int number = random.nextInt(newsList.size());
            News news = newsList.get(number);
            newsId=news.newsID;
            Record record = new Record(user, "browse", newsId);
            record.save();
            return news.save(number);
        }
        public boolean addFavorite(News news){
            news=newsList.get(0);
            String newsId=news.newsID;
            Record record = new Record(user, "favorite", newsId);
            record.save();
            return true;
        }
        public boolean blockKeyword(String keyword){
            Record record = new Record(user, "block", keyword);
            record.save();
            return true;
        }
        public boolean SignIn(User user1){
            List<User> users = User.find(User.class, "userID = ? and password = ?",user1.userID,user1.password);
            if(users.isEmpty()||users.size()>1)return false;
            user=users.get(0);
            System.out.println("Sign in successfully.");
            return true;
        }
        public boolean SignUp(User user1){
            user=user1;
            user.save();
            System.out.println("Sign up successfully.");
            return true;
        }

    }
    public APIBinder myBinder;
    NotificationManager notificationManager;
    Notification.Builder localBuilder;

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myBinder = new APIBinder();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        localBuilder = new Notification.Builder(this);
        localBuilder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0));
        localBuilder.setAutoCancel(false);
        localBuilder.setSmallIcon(R.drawable.ic_launcher_foreground);
        localBuilder.setTicker("最新新闻通知");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // 第三个参数表示通知的重要程度，默认则只在通知栏闪烁一下
            NotificationChannel notificationChannel = new NotificationChannel("AppTestNotificationId", "AppTestNotificationName", NotificationManager.IMPORTANCE_DEFAULT);
            // 注册通道，注册后除非卸载再安装否则不改变
            notificationManager.createNotificationChannel(notificationChannel);
            localBuilder.setChannelId("AppTestNotificationId");
        }
// 发出通知
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //    Toast.makeText(this, "service started", Toast.LENGTH_LONG).show();
        Request request = new Request();
        request.endDate=new Date();
        request.startDate=new Date();
        request.startDate.setDate(request.startDate.getDay()-1);
        myBinder.Request2News(request);
        System.out.println("service get:"+newsList.size());
        class Timer extends Thread{
            @Override
            public void run(){
                try{
                    for(News news:newsList){
                        Thread.sleep(15000);
                        localBuilder.setContentTitle(news.title);
                        localBuilder.setContentText(news.content.substring(0,50)+"……");
                        notificationManager.notify(1, localBuilder.build());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        Timer timer = new Timer();
        timer.start();
        Toast.makeText(getApplicationContext(),"0", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
    }


}
