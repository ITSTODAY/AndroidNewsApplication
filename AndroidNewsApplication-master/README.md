# AndroidNewsApplication

# NewSCollection类

  新闻获取接口函数：
    public static NewsCollection Request2News(Request request)；
  
  参数Requ类共定义5个变量，具体含义与官方文档相同：
  
    public int size//需要的新闻数量;
    public Date startDate//起止时间;
    public Date endDate;
    public String words//关键词;
    public String categories//类别;
    
    不需要某个变量可以不填。
    
  返回值类型为NewsCollection，与官方json文件中的结构一致。
