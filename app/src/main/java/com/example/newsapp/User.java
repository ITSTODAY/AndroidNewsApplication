package com.example.newsapp;


import android.content.Context;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class User extends SugarRecord<User> implements Serializable{

    @Ignore
    private static String page="http://183.172.198.49:8080/service2_war_exploded/";

    @Ignore
    private Integer userId;

    @Ignore
    private List<News> newsList;

    @Ignore
    private ArrayList<String> browsenHistory;

    @Ignore
    private ArrayList<String> favoriteHistory;

    @Ignore
    private ArrayList<String> blockedWords;

    public String userID;

    public String password;

    public int getuserId(){
        return userId;
    }
    public void setID(int userId){
        this.userId=userId;
    }
    public User(){
    }

    public User(String userID, String password){
        this.userID=userID;
        this.password=password;
    }

    public int SignIn(){
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
        if(myThread.userId==null)
            return 0;//没注册过
        else if(myThread.userId==-1)
            return -1;//密码错误
        else{
            return 1;
        }
    }
    public int SignUp(){
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
        if(myThread.userId==null)
            return 0;//没注册过
        else if(myThread.userId==-1)
            return -1;//密码错误
        else return 1;
    }
    public NewsCollection Request2News(Request request){
        request.words=request.words.replaceAll(" ","\\+");
        NewsCollection newsCollection = NewsCollection.Request2News(request);
        try {
            newsList = newsCollection.data;
            PurifyNews(newsCollection);
        }catch (Exception e){
            newsList = null;
        }
        return newsCollection;
    }
    public void PurifyNews(NewsCollection newsCollection){
        if(this.blockedWords.isEmpty()) {
            blockedWords = new Record(this, "block", "").findAllNewsName();
        }
        class filter implements Predicate<News>{
            @Override
            public boolean test(News news){
                for(Keyword keyWords:news.keywords){
                    if(blockedWords.contains(keyWords.word))return true;
                }
                return false;
            }
        }
        if(!this.blockedWords.isEmpty())
            newsList.removeIf(new filter());
        newsCollection.total=newsList.size();
    }
    public void SavePreference(Keyword keyword){
        new Preference(this,keyword).save();
    }
    public NewsCollection GetRecommendNews(){
        return new Preference(this,null).findall();
    }
    public void SaveSearchHistory(String SearchWords){
        SearchWords = SearchWords.replaceAll(" ","\\+");
        Record record = new Record(this, "search", SearchWords);
        record.save();
    }
    public List<String> GetSearchHistory(){
        ArrayList<String> news = new Record(this,"search","").findAllNewsName();
        for(String word:news){
            word.replaceAll("\\+"," ");
        }
        return news;
    }
    public NewsCollection GetOnLineBrowseHistory(){
        NewsCollection newsCollection = new Record(this, "browse", "").findall();
//        try {
//            newsList = newsCollection.data;
//            browsenHistory.clear();
//            for(News news:newsList){
//                browsenHistory.add(news.newsID);
//            }
//        }catch (Exception e){
//            newsList = null;
//        }
        return newsCollection;
    }
    public NewsCollection GetLocalBrowseHistory(){
        NewsCollection newsCollection = new Record(this, "browse", "").LocalFindAll();
//        try {
//            newsList = newsCollection.data;
//            browsenHistory.clear();
//            for(News news:newsList){
//                browsenHistory.add(news.newsID);
//            }
//        }catch (Exception e){
//            newsList = null;
//        }
        return newsCollection;
    }
    public NewsCollection GetBrowseHistory(){
        NewsCollection newsCollection = this.GetOnLineBrowseHistory();
        if(newsCollection==null||newsCollection.data==null)newsCollection = this.GetLocalBrowseHistory();
        return newsCollection;
    }
    public NewsCollection GetFavorite(){
        NewsCollection newsCollection = new Record(this, "favorite", "").findall();
//        try {
//            newsList = newsCollection.data;
//            favoriteHistory.clear();
//            for(News news:newsList){
//                favoriteHistory.add(news.newsID);
//            }
//        }catch (Exception e){
//            newsList = null;
//        }
        return newsCollection;
    }

    //    public boolean isBrowsed(String newsId){
//        if(browsenHistory.isEmpty()){
//            this.GetBrowseHistory();
//        }
//        return browsenHistory.contains(newsId);
//    }
//    public boolean isFavorite(String newsId){
//        if(favoriteHistory.isEmpty()){
//            this.GetFavorite();
//        }
//        return favoriteHistory.contains(newsId);
//    }
    public boolean isBrowsed(String newsId){
        Record record = new Record(this,"browse",newsId);
        return record.query();
    }
    public boolean isFavorite(String newsId){
        Record record = new Record(this,"favorite",newsId);
        return record.query();
    }
    public boolean addBrowse(News news){
        String newsId=news.newsID;
        Record record = new Record(this, "browse", newsId);
        record.save();
        for(Keyword keyword:news.keywords){
            this.SavePreference(keyword);
        }
//        this.browsenHistory.add(newsId);
        return news.save(this.userId);
    }
    public boolean addFavorite(News news){
        String newsId=news.newsID;
        Record record = new Record(this, "favorite", newsId);
        record.save();
        for(Keyword keyword:news.keywords){
            this.SavePreference(keyword);
        }
//        this.favoriteHistory.add(newsId);
        return news.save(this.userId);
    }
    public boolean addCategory(String[] category){
        StringBuffer buffer = new StringBuffer();
        for(String c:category){
            buffer.append(c).append("+");
        }
        String res=buffer.substring(0,buffer.length()-1);
        Record record = new Record(this, "category", res);
        record.update();
        return true;
    }
    public boolean deleteBrowse(String newsId){
        Record record = new Record(this, "browse", newsId);
        // browsenHistory.remove(newsId);
        return record.Delete();
    }
    public boolean deleteFavorite(String newsId){
        Record record = new Record(this, "favorite", newsId);
        //favoriteHistory.remove(newsId);
        return record.Delete();
    }
    public boolean deleteSearch(String searchHistory){
        Record record = new Record(this, "search", searchHistory);
        return record.Delete();
    }
    public String[] GetCategory(){
        Record record = new Record(this,"category","");
        List<String> res= record.findAllNewsName();
        String category;
        if(!res.isEmpty())
            category = res.get(0);
        else
            category = "娱乐+军事+教育+文化+健康+财经+体育+汽车+科技+社会";
        return category.split("\\+");
    }
    public boolean blockKeyword(String keyword){
        Record record = new Record(this, "block", keyword);
        record.save();
        //this.blockedWords.add(keyword);
        return true;
    }
    public static void main(String[] args){
        User user = new User("13","13");
        System.out.println(user.SignIn());
        user.setID(21);
        Request request = new Request();
        request.words="市场";
        //request.categories="经济";
        request.endDate=new Date();
        //       NewsCollection newsCollection = user.Request2News(request);
//        user.addBrowse(newsCollection.data.get(0));
        System.out.println(user.GetRecommendNews().data.size());
        //user.addBrowse(newsCollection.data.get(0));
//        user.addFavorite(newsCollection.data.get(0));
//        user.addBrowse(newsCollection.data.get(1));
//        System.out.println(user.GetLocalHistory().data.get(0).newsID);
        //System.out.println(user.deleteBrowse("201909010046ade3daa1d79e4b2fb7557a1ce072cd60"));
    }
}
