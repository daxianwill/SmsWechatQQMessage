# 一、背景：
现在好多手环与手机端的APP都能实时的显示新消息，新消息有短信、电话、微信、QQ等。由于最近在做一个手环的项目，有同样的需求，研究了其他家这种功能的实现，在网上查了些高手的资料，自己的总结如下：

# 二、功能分析：
1、此监听功能，必须在后台长期运行，需些写一个生命周期长的服务
2、查了微信、QQ开发API，都没有提供这种接口。偶然间，发现当开启QQ状态栏消息权限时，手环能够接受到信息；关闭此权限时，就没有此功能。所以，判断可能市场上的这些APP都是监听状态栏信息的。

# 三、以下是这个Demo大体流程

## 第1步：服务的编写


![这里写图片描述](http://img.blog.csdn.net/20170623222533052?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMjY0NDAyMjE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

## 第2步：监听状态栏信息服务

![这里写图片描述](http://img.blog.csdn.net/20170623222717791?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMjY0NDAyMjE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

## 第3步：监听系统短信数据库

![这里写图片描述](http://img.blog.csdn.net/20170623222734331?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMjY0NDAyMjE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

[这是在GitHub上的Demo源码](https://github.com/YangJiexian/SmsWechatQQMessage)


# 四、发送接收判断
## 发送
如果监听sms数据库变化，发送一条短信要经过type的6,4,2三个状态变化，如果只想监听接受到的短信内容

## 接收
判断type=1即可，如果判断发送短信，判断type=2即可，这样就不会出现重复操作。

# 五、数据库中sms相关的字段如下：      

_id                      primary key     integer                  与words表内的source_id关联
thread_id              会话id，一个联系人的会话一个id，与threads表内的_id关联      integer 
address                 对方号码          text
address    对方号码          text  
person     联系人id           integer    
date       发件日期           integer  
protocol     通信协议，判断是短信还是彩信    integer  0：SMS_RPOTO, 1：MMS_PROTO
read         是否阅读           integer   default 0 0：未读， 1：已读    
status      状态           integer   default-1。 -1：接收，0：complete,64： pending, 128failed
type         短信类型           integer 1：inbox  2：sent 3：draft56  4：outbox  5：failed  6：queued
body                      内容 
service_center      服务中心号码 
subject                  主题  reply_path_present  
locked 
error_code 
seen

# 六、短信URI
content://sms/              所有短信
content://sms/sent          已发送
content://sms/draft         草稿
content://sms/outbox        发件箱
content://sms/failed        发送失败
content://sms/queued        待发送列表

