package com.example.newsapp;


import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class User extends SugarRecord<User> {

    @Ignore
    private static String page="http://localhost:8080/service2_war_exploded/";

    @Ignore
    private Integer userId;

    @Ignore
    private List<News> newsList;

    @Ignore
    Random random = new Random();

    public String userID;

    public String password;

    public int getuserId(){
        return userId;
    }

    public User(){
    }

    public User(String userID, String password){
        this.userID=userID;
        this.password=password;
    }

    public boolean SignIn(){
        String request=page+"Login?state=3&ID="+this.userID+"&PW="+this.password;
        MyThread myThread = new MyThread(request, 3);
        myThread.start();
        try {
            myThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            this.userId=myThread.userId;
        }
        System.out.println(this.userId);
        return myThread.userId!=null;
    }
    public boolean SignUp(){
        String request=page+"Login?state=2&ID="+this.userID+"&PW="+this.password;
        MyThread myThread = new MyThread(request, 2);
        myThread.start();
        try {
            myThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            this.userId=myThread.userId;
        }
        return myThread.userId!=null;
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
        NewsCollection newsCollection = new Record(this, "browse", "").findall();
        try {
            newsList = newsCollection.data;
        }catch (Exception e){
            newsList = null;
        }
        return newsCollection;
    }
    public boolean addBrowse(int number){
        News news = newsList.get(number);
        String newsId=news.newsID;
        Record record = new Record(this, "browse", newsId);
        record.save();
        return news.save();
    }
    public boolean addFavorite(News news){
        news=newsList.get(0);
        String newsId=news.newsID;
        Record record = new Record(this, "favorite", newsId);
        record.save();
        return true;
    }
    public boolean blockKeyword(String keyword){
        Record record = new Record(this, "block", keyword);
        record.save();
        return true;
    }
    public static void main(String[] args){
        User user = new User("13","13");
        System.out.println(user.SignIn());
//        Request request = new Request();
//        request.words="市场";
//        //request.categories="经济";
//        request.endDate=new Date();
//        user.Request2News(request);
//        user.addBrowse("111");
        System.out.println(user.GetBrowseHistory().data.get(0).newsID);
    }
}

