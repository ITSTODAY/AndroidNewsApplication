package com.example.newsapp;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Record extends SugarRecord<Record> {

    @Ignore
    private static String page="http://183.172.198.49:8080/service2_war_exploded/";

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
        super.save();
        String request=page+"Login?state=4&userid="+this.User+"&type="+this.type+"&newsID="+this.newsID;
        System.out.println(request);
        MyThread myThread = new MyThread(request, 4);
        myThread.start();
    }
    public boolean Delete(){
        List<Record> records=Select.from(Record.class).where(Condition.prop("User").eq(this.User),
                Condition.prop("type").eq(this.type),Condition.prop("news_ID").eq(this.newsID)).list();
        if(records.isEmpty())return false;
        records.get(0).delete();
        System.out.println("deleted!");
        String request=page+"Login?state=11&userid="+this.User+"&type="+this.type+"&newsID="+this.newsID;
        MyThread myThread = new MyThread(request, 11);
        myThread.start();
        return myThread.userId!=null;
    }
    public void update(){
        String request=page+"Login?state=10&userid="+this.User+"&type="+this.type+"&newsID="+this.newsID;
        MyThread myThread = new MyThread(request, 10);
        myThread.start();
    }
    public boolean query(){
        List<Record> records=Select.from(Record.class).where(Condition.prop("User").eq(this.User),
                Condition.prop("type").eq(this.type),Condition.prop("news_ID").eq(this.newsID)).list();
        String request=page+"Login?state=9&userid="+this.User+"&type="+this.type+"&newsID="+this.newsID;
        MyThread myThread = new MyThread(request, 9);
        myThread.userId=this.User;
        myThread.start();
        try {
            myThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return myThread.userId!=null&&!records.isEmpty();
    }
    public NewsCollection findall(){
        String request=page+"Login?state=5&userid="+this.User+"&type="+this.type;
        MyThread myThread = new MyThread(request, 5);
        myThread.userId=this.User;
        myThread.start();
        try {
            myThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return myThread.newsCollection;
    }
    public NewsCollection LocalFindAll(){
        ArrayList<News> newsArrayList = new ArrayList<>();
        List<Record> records=Select.from(Record.class).where(Condition.prop("User").eq(this.User),Condition.prop("type").eq(this.type)).list();
        for(Record record:records){
            News tmp = News.Json2News(record.newsID,this.User);
            if(tmp!=null)
                newsArrayList.add(tmp);
        }
        return new NewsCollection(newsArrayList);
    }
    public ArrayList<String> findAllNewsName(){
        String request=page+"Login?state=5&userid="+this.User+"&type="+this.type;
        System.out.println(request);
        MyThread myThread = new MyThread(request, 6);
        myThread.userId=this.User;
        myThread.start();
        try {
            myThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return myThread.newsname;
    }
    public NewsCollection findLocalAll(){
        ArrayList<News> newsArrayList = new ArrayList<>();
        File mydir = new File(String.valueOf(this.User));
        assert mydir.isDirectory();
        String[] newsname=mydir.list();
        for(String news:newsname){
            newsArrayList.add(News.Json2News(news,this.User));
        }
        return new NewsCollection(newsArrayList);
    }
}
class Preference{
    @Ignore
    private static String page="http://183.172.198.49:8080/service2_war_exploded/";

    public int User;

    public Keyword keyword;

    public Preference(User User, Keyword keyword){
        this.User=User.getuserId();
        this.keyword=keyword;
    }
    public void save(){
//        super.save();
        String url=page+"Login?state=7&userid="+this.User+"&keyword="+keyword.word+"&score="+keyword.score;

        MyThread myThread = new MyThread(url, 7);
        myThread.start();
    }
    public NewsCollection findall(){
        String request=page+"Login?state=8&userid="+this.User;
        System.out.println(request);
        MyThread myThread = new MyThread(request, 8);
        myThread.userId=this.User;
        myThread.start();
        try {
            myThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return myThread.newsCollection;
    }
}