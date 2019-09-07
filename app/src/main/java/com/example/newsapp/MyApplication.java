package com.example.newsapp;
import com.mob.MobSDK;
import com.orm.SugarApp;
public class MyApplication extends SugarApp {
    @Override
    public void onCreate(){
        super.onCreate();
        MobSDK.init(this);
    }
}
