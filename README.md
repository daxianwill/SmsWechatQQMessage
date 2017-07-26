背景：现在好多手环与手机端的APP都能实时的显示新消息，新消息有短信、电话、微信、QQ等。由于最近在做一个手环的项目，有同样的需求，研究了其他家这种功能的实现，在网上查了些高手的资料，自己的总结如下：

功能分析：1、此监听功能，必须在后台长期运行，需些写一个生命周期长的服务
2、查了微信、QQ开发API，都没有提供这种接口。偶然间，发现当开启QQ状态栏消息权限时，手环能够接受到信息；关闭此权限时，就没有此功能。所以，判断可能市场上的这些APP都是监听状态栏信息的。

以下是我做的一个小demo

第一步：服务的编写


![这里写图片描述](http://img.blog.csdn.net/20170623222533052?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMjY0NDAyMjE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

第二步：监听状态栏信息服务

![这里写图片描述](http://img.blog.csdn.net/20170623222717791?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMjY0NDAyMjE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

第三步：监听系统短信数据库

![这里写图片描述](http://img.blog.csdn.net/20170623222734331?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcXFfMjY0NDAyMjE=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

[这是在GitHub上的Demo源码](https://github.com/YangJiexian/SmsWechatQQMessage)
