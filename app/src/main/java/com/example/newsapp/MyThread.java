package com.example.newsapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MyThread extends Thread{
    public NewsCollection newsCollection;
    public Integer userId;
    private String getUrl;
    private int work;
    public MyThread(String url,int work){
        super();
        this.getUrl=url;
        this.work=work;
    }
    @Override
    public void run(){//1 查询 2注册 3登录
        super.run();
        try {
            URL url= new URL(getUrl);
            InputStreamReader is = new InputStreamReader(url.openStream(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(is);
            StringBuffer buffer = new StringBuffer();
            String res;
            if(work==1) {
                while((res=br.readLine())!=null){
                    buffer.append(res);
                }
                res=buffer.toString();
                newsCollection = NewsCollection.Json2News(res);
            }else if(work==2||work==3){
                try{
                    while((res=br.readLine())!=null){
                        buffer.append(res);
                    }
                    res=buffer.toString();
                    userId=Integer.parseInt(res);
                }catch (Exception e){
                    userId=null;
                }
            }else if(work==4){
                int a=1;
            }else if(work==5){
                ArrayList<News> newsArrayList = new ArrayList<>();
                ArrayList<String> newsname= new ArrayList<>();
                while((res=br.readLine())!=null){
                    if(newsname.contains(res))continue;
                    newsname.add(res);
                }
                for(String news:newsname){
                    newsArrayList.add(News.Json2News(news));
                }
                newsCollection=new NewsCollection(newsArrayList);
            }
        }catch (Exception e){
            System.out.println(e.getClass());
            newsCollection=null;
        }
    }
}
