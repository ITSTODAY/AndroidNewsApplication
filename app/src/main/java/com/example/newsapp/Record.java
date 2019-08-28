package com.example.newsapp;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

public class Record extends SugarRecord<Record> {

    @Ignore
    private static String page="http://localhost:8080/service2_war_exploded/";

    public int User;

    public String type; //block/preference, favorite,/browse

    public String newsID; //keyword

    public Record(){
    }

    public Record(User User, String type, String newsID){
        this.User=User.getuserId();
        this.type=type;
        this.newsID=newsID;
    }
    @Override
    public void save(){
//        super.save();
        String request=page+"Login?state=4&userid="+this.User+"&type="+this.type+"&newsID="+this.newsID;
        System.out.println(request);
        MyThread myThread = new MyThread(request, 4);
        myThread.start();
    }
    public NewsCollection findall(){
        String request=page+"Login?state=5&userid="+this.User+"&type="+this.type;
        System.out.println(request);
        MyThread myThread = new MyThread(request, 5);
        myThread.start();
        try {
            myThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return myThread.newsCollection;
    }
}