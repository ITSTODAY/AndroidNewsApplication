package com.example.newsapp;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class Keyword{
    public double score;
    public String word;
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

    public boolean save(){
        ObjectMapper mapper = new ObjectMapper();
        try{
            File file = new File(newsID);
            mapper.writeValue(file, this);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    public News(){
    }

    public static News Json2News(String fileName){
        try{
            File file=new File(fileName);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(file, News.class);
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
        if(words!=""){
            url+="words="+words+"&";
        }
        if(categories!=""){
            url+="categories="+categories+"&";
        }
        url=url.substring(0,url.length()-1);
        System.out.println(url);
        int index = url.indexOf("?");
        String result = url.substring(0,index+1);
        String temp = url.substring(index+1);
        try {
            //URLEncode转码会将& ： / = 等一些特殊字符转码,(但是这个字符  只有在作为参数值  时需要转码;例如url中的&具有参数连接的作用，此时就不能被转码)
            String encode = URLEncoder.encode(temp, "utf-8");
            System.out.println(encode);
            encode = encode.replace("%3D",  "=");
            encode = encode.replace("%2F", "/");
            encode = encode.replace("+", "%20");
            encode = encode.replace("%26", "&");
            result += encode;
            //System.out.println("转码后的url:"+result);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}

public class NewsCollection {
    public String pageSize;
    public int total;
    public List<News> data;
    public String currentPage;

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
            return newsCollection;
        }catch (Exception e){
            System.out.println(e.getClass());
        }
        return null;
    }
    public static NewsCollection Request2News(Request request){
        try {
            URL url= new URL(request.getUrl());
            InputStreamReader is = new InputStreamReader(url.openStream(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(is);

            StringBuffer buffer = new StringBuffer();
            String res;
            while((res=br.readLine())!=null){
                buffer.append(res);
            }

            NewsCollection newsCollection = Json2News(buffer.toString());
            return newsCollection;
        }catch (Exception e){
            System.out.println(e.getClass());
        }
        return null;
    }
    public static void main(String[] args){
        Request request = new Request();
        request.categories="教育";
        request.words="清华";

    }
}
