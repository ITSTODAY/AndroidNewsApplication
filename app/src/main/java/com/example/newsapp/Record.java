package com.example.newsapp;

import com.orm.SugarRecord;

public class Record extends SugarRecord<Record> {

    public User User;

    public String type; //block/preference, favorite,/browse

    public String newsID; //keyword

    public Record(){
    }

    public Record(User User, String type, String newsID){
        this.User=User;
        this.type=type;
        this.newsID=newsID;
    }


}