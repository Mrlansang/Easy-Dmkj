# Easy-DM

**项目描述：**

学校的第二课堂活动`APP`体验不好,故设计一个客戶端。可以在其中查询可以参加的活动,可以查看活动的具体信息,定时报名。

**开发工具：**

`Android Studio`、`FastJson`、`OKHttp`、`bcprov`、`Glide`、`Ubuntu18.04.5` 操作系统。

**实现技术：**

通过`OKHttp`创建`Header`加入设备信息,根据`PKCS5 Padding`完成`PKCS7 Padding`填充,完成加/解密,实现登录和获取信息。

采用`Material Design`设计了卡片式主⻚显示活动信息。`FastJson`完成活动信息解析,加载到详情⻚面。

`Alarm`机制实现定时自动报名。个人服务器推送软件更新。

获取经纬度之后使用高德`LBS`导航到活动地点。

**截图**

（这里使用外链）

![1869798806.jpg][1]

![1672663440.jpg][2]

![1624891364.jpg][3]

![1560405844.jpg][4]

![355815806.jpg][5]

![65243431.jpg][6]

**补充**

需要自己添加高德LBS的Key，否则无法使用导航。

由于登录界面有服务器信息，出于隐私保护没有上传Activity的代码，需要自己写一个。

AES和Des里的Key和加密Iv自己添加。


  [1]: http://blog.jiyehoo.com:81/usr/uploads/2020/09/1784097491.jpg
  [2]: http://blog.jiyehoo.com:81/usr/uploads/2020/09/2664312110.jpg
  [3]: http://blog.jiyehoo.com:81/usr/uploads/2020/09/2486206439.jpg
  [4]: http://blog.jiyehoo.com:81/usr/uploads/2020/09/2100319872.jpg
  [5]: http://blog.jiyehoo.com:81/usr/uploads/2020/09/4263075057.jpg
  [6]: http://blog.jiyehoo.com:81/usr/uploads/2020/09/2805451109.jpg
