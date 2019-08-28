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

# User类
    用法大概是这个样子：
        User user = new User("13","13");//构造函数：用户名、密码
        System.out.println(user.SignIn());//登录是SignIn,注册是SignUp，会返回一个boolean值表示是否登录/注册成功
        Request request = new Request();
        request.words="市场";
        //request.categories="经济";
        request.endDate=new Date();
        user.Request2News(request);//用User类中的获取新闻接口表示用这个用户在浏览，否则添加不了浏览记录
        user.addBrowse(1);//添加浏览记录,表示浏览了newcollection中的第1篇
        System.out.println(user.GetBrowseHistory().data.get(0).newsID);//GetBrowseHistory会返回1个newcollection，里面是浏览过的新闻
        
   
