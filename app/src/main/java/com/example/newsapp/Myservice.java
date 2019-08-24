package com.example.newsapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import java.util.List;

public class Myservice extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder localBuilder = new Notification.Builder(this);
        localBuilder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0));
        localBuilder.setAutoCancel(false);
        localBuilder.setSmallIcon(R.drawable.ic_launcher_foreground);
        localBuilder.setTicker("Foreground service started");
        localBuilder.setContentTitle("Socket 服务端");
        localBuilder.setContentText("正在运行");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // 第三个参数表示通知的重要程度，默认则只在通知栏闪烁一下
            NotificationChannel notificationChannel = new NotificationChannel("AppTestNotificationId", "AppTestNotificationName", NotificationManager.IMPORTANCE_DEFAULT);
            // 注册通道，注册后除非卸载再安装否则不改变
            notificationManager.createNotificationChannel(notificationChannel);
            localBuilder.setChannelId("AppTestNotificationId");
        }
// 发出通知
        notificationManager.notify(1, localBuilder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    //    Toast.makeText(this, "service started", Toast.LENGTH_LONG).show();
        User user = new User("yzh","1234");
        user.save();
        Record r1 = new Record(user, "browse", "11010");
        r1.save();
        Toast.makeText(getApplicationContext(),"0", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        List<Record> record = Record.listAll(Record.class);
        System.out.println(record.get(0).newsID);

        Toast.makeText(this, "service stopped", Toast.LENGTH_LONG).show();
    }


}
