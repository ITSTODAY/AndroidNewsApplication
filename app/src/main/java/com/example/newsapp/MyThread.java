package com.example.newsapp;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MyThread extends Thread{
    public NewsCollection newsCollection;
    public ArrayList<String> newsname;
    public Integer userId;
    private String getUrl;
    private int work;
    public MyThread(String url,int work){
        super();
        this.getUrl=url;
        this.work=work;
    }
    @Override
    public void run(){//1 查询 2注册 3登录 4添加浏览 56查询浏览 7添加偏好 8查询偏好 9查询单条浏览 10更改category
        super.run();
        try {
            int index = getUrl.indexOf("?");
            String result = getUrl.substring(0,index+1);
            String temp = getUrl.substring(index+1);
            try {
                //URLEncode转码会将& ： / = 等一些特殊字符转码,(但是这个字符  只有在作为参数值  时需要转码;例如url中的&具有参数连接的作用，此时就不能被转码)
                String encode = URLEncoder.encode(temp, "utf-8");
                encode = encode.replace("%3D",  "=");
                encode = encode.replace("%2F", "/");
                encode = encode.replace("+", "%20");
                encode = encode.replace("%26", "&");
                result += encode;
                //System.out.println("转码后的url:"+result);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            System.out.println(result);
            URL url= new URL(result);
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            InputStreamReader is = new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(is);
            StringBuffer buffer = new StringBuffer();
            String res;
            if(work==1) {
                while((res=br.readLine())!=null){
                    buffer.append(res);
                }
                res=buffer.toString();
                newsCollection = NewsCollection.Json2News(res);
            }else if(work==2||work==3||work==11){
                try{
                    while((res=br.readLine())!=null){
                        buffer.append(res);
                    }
                    res=buffer.toString();
                    if(res.equals("null"))
                        userId=-1;
                    else
                        userId=Integer.parseInt(res);
                }catch (Exception e){
                    userId=null;
                }
            }else if(work==4){
                int a=1;
            }else if(work==5){
                ArrayList<News> newsArrayList = new ArrayList<>();
                newsname= new ArrayList<>();
                while((res=br.readLine())!=null){
                    if(newsname.contains(res))continue;
                    newsname.add(res);
                }
                for(String news:newsname){
                    News tmp = News.Json2News(news,userId);
                    if(tmp!=null)
                        newsArrayList.add(tmp);
                }
                newsCollection=new NewsCollection(newsArrayList);
            }else if(work==6){
                newsname= new ArrayList<>();
                while((res=br.readLine())!=null){
                    if(newsname.contains(res))continue;
                    newsname.add(res);
                }
            }else if(work==7){
                int a=1;
            }else if(work==8){
                ArrayList<Keyword> keywords = new ArrayList<>();
                while((res=br.readLine())!=null){
                    Keyword keyword = new Keyword();
                    keyword.word=res;
                    res=br.readLine();
                    keyword.score=Double.valueOf(res);
                    keywords.add(keyword);
                }
                keywords.sort(new KeywordComparetor());
                Request request = new Request();
                request.endDate = new Date();
                request.size=5;
                for(int i=0;i<4&&i<keywords.size();++i){
                    request.words=keywords.get(i).word;
                    NewsCollection tmp =NewsCollection.Request2News(request);
                    if(i==0)
                        newsCollection=tmp;
                    else
                        newsCollection.data.addAll(tmp.data);
                    System.out.println(keywords.get(i).word+' '+keywords.get(i).score);
                }
                newsCollection.total=newsCollection.data.size();
            }else if(work==9){
                res=br.readLine();
                if(res.equals("null"))
                    userId=null;
                else
                    userId=Integer.parseInt(res);
            }else if(work==10){
                int a=1;
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            newsCollection=new NewsCollection();
            newsCollection.data=null;
        }
    }
}
class FileThread extends Thread{
    public String fileName;
    @Override
    public void run(){
        try {

            // 换行符
            final String newLine = "\r\n";
            //数据分隔线
            final String BOUNDARY = "【这里随意设置】";//可以随意设置，一般是用  ---------------加一堆随机字符
            //文件结束标识
            final String boundaryPrefix = "--";

            // 服务器的域名
            URL url = new URL("http://localhost:8080/service2_war_exploded/Upload?filename="+fileName);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置为POST情
            conn.setRequestMethod("POST");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            //conn.setDoInput(true);/不必加，默认为true
            //conn.setUseCaches(false);//用于设置缓存，默认为true，不改也没有影响（至少在传输单个文件这里没有）

            // 设置请求头参数
            //关于keep-alive的说明：https://www.kafan.cn/edu/5110681.html
            //conn.setRequestProperty("connection", "Keep-Alive");//现在的默认设置一般即为keep-Alive，因此此项为强调用，可以不加
            //conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows Nt 5.1; SV1)");//用于模拟浏览器，非必须

            //用于表示上传形式，必须
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            //这里是Charset，网上大多都是Charsert？？？我的天，笑哭。不过好像没什么影响...不知道哪位大佬解释一下
            conn.setRequestProperty("Charset", "UTF-8");

            //获取conn的输出流用于向服务器输出信息
            OutputStream out = new DataOutputStream(conn.getOutputStream());

            out.write(fileName.getBytes());
            //输出结束，关闭输出流
            out.flush();
            out.close();
        } catch (Exception e) {
            System.out.println("发送POST请求出现异常！" + e);
            e.printStackTrace();
        }
    }
}
