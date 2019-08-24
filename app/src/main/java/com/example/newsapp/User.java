package com.example.newsapp;


import com.orm.SugarRecord;

public class User extends SugarRecord<User> {

    public String userID;

    public String password;

    public User(){
    }

    public User(String userID, String password){
        this.userID=userID;
        this.password=password;
    }


    public static void main(String[] args){

    }
}

