package com.example.newsapp;

import android.content.Context;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

class Keyword implements Comparable{
    public double score;
    public String word;
    @Override
    public int compareTo(Object o) {
        return Double.compare(score, ((Keyword) o).score);
    }
}
class KeywordComparetor implements Comparator {
    @Override
    public int compare(Object arg0, Object arg1) {
        return Double.compare(((Keyword)arg1).score,((Keyword)arg0).score);
    }
}
class Person{
    public int count;
    public String linkedURL;
    public String mention;
}

class Location{
    public double lng;
    public int count;
    public String linkedURL;
    public double lat;
    public String mention;
}

class News{
    public String image;
    public String publishTime;
    @JsonIgnore
    public Date publishDate;
    public ArrayList<Keyword> keywords;
    public String language;
    public String video;
    public String title;
    public ArrayList<Keyword> when;
    public String content;
    public ArrayList<Person> persons;
    public String newsID;
    public String crawlTime;
    public ArrayList<Person> organizations;
    public String publisher;
    public ArrayList<Location> locations;
    public ArrayList<Keyword> where;
    public ArrayList<Keyword> who;
    public String category;

    @JsonIgnore
    private static ObjectMapper objectMapper;

    @JsonIgnore
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");

    public boolean save(Integer username){
        if(username==null)username=0;
        ObjectMapper mapper = new ObjectMapper();
        try{
            String json=mapper.writeValueAsString(this);
            FileHelper.fileHelper.save(username+"-"+newsID,json);
//            File file = new File(username+"/"+newsID);
//            mapper.writeValue(file, this);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    public News(){
    }
    public boolean upload(Integer username){
        if(username==null)username=0;
        try{
            FileThread fileThread = new FileThread();
            fileThread.fileName=username+"-"+newsID;
            fileThread.start();
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
    public static News Json2News(String fileName,Integer username){
        if(username==null)username=0;
        try{
            ObjectMapper mapper = new ObjectMapper();
            String json = FileHelper.fileHelper.read(username+"-"+fileName);
            News news = mapper.readValue(json,News.class);
//            File file=new File(username+"/"+fileName);
//            News news = mapper.readValue(file, News.class);
            news.publishDate = dateFormat.parse(news.publishTime);
            return news;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }
}

class Request{
    public int size;
    public Date startDate;
    public Date endDate;
    public String words;
    public String categories;
    static public SimpleDateFormat DateForm = new SimpleDateFormat("yyyy-MM-dd");

    public Request(){

    }
    public Request(int size, Date startDate, Date endDate, String words, String categories){
        this.size=size;
        this.startDate=startDate;
        this.endDate=endDate;
        this.words=words;
        this.categories=categories;
    }
    public String getUrl(){
        String url="https://api2.newsminer.net/svc/news/queryNewsList?";
        if(size!=0){
            url+="size="+size+"&";
        }
        if(startDate!=null){
            url+="startDate="+DateForm.format(startDate)+"&";
        }
        if(endDate!=null){
            url+="endDate="+DateForm.format(endDate)+"&";
        }
        if(words!=null&&!words.equals("")){
            words = words.replaceAll(" |,","+");
            url+="words="+words+"&";
        }
        if(categories!=null&&!categories.equals("")){
            url+="categories="+categories+"&";
        }
        url=url.substring(0,url.length()-1);
//        System.out.println(url);
//        int index = url.indexOf("?");
//        String result = url.substring(0,index+1);
//        String temp = url.substring(index+1);
//        try {
//            //URLEncode转码会将& ： / = 等一些特殊字符转码,(但是这个字符  只有在作为参数值  时需要转码;例如url中的&具有参数连接的作用，此时就不能被转码)
//            String encode = URLEncoder.encode(temp, "utf-8");
//            System.out.println(encode);
//            encode = encode.replace("%3D",  "=");
//            encode = encode.replace("%2F", "/");
//            encode = encode.replace("+", "%20");
//            encode = encode.replace("%26", "&");
//            result += encode;
//            //System.out.println("转码后的url:"+result);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        return url;
    }
}

public class NewsCollection {
    public String pageSize;
    public int total;
    public List<News> data;
    public String currentPage;
    @JsonIgnore
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");

    public NewsCollection(){
    }

    public NewsCollection(List<News> news){
        data=news;
        total=news.size();
    }
    public static NewsCollection Json2News(String json){
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try {
            NewsCollection newsCollection = mapper.readValue(json, NewsCollection.class);
            for(News news:newsCollection.data){
                news.publishDate = dateFormat.parse(news.publishTime);
            }
            return newsCollection;
        }catch (Exception e){
            System.out.println(e.getClass());
        }
        return null;
    }
    public static NewsCollection Request2News(Request request){
        MyThread myThread = new MyThread(request.getUrl(),1);
        myThread.start();
        try {
            myThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            for (News news : myThread.newsCollection.data) {
                news.publishDate = dateFormat.parse(news.publishTime);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            myThread.newsCollection = new NewsCollection();
            myThread.newsCollection.data = new ArrayList<>();
        }

        return myThread.newsCollection;
    }
    public static void main(String[] args){
        Date startData = new Date(0);
        Date endDate = new Date();
        System.out.println(NewsCollection.Request2News(new Request(20,startData,endDate,null,null)).data.get(0).publishDate);
    }
}