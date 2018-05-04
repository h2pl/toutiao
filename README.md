# toutiao
仿照今日头条的主页toutiao.com做的一个Java web项目。使用SpringBoot+Mybatis+velocity开发。开发工具和Java语言介绍

内容包括：
开发工具和Java语言
Spring入门，模板语法和渲染
数据库交互iBatis集成
用户注册登录管理
资讯发布，图片上传，资讯首页
评论中心，站内信
Redis入门以及Redis实现赞踩功能
异步设计和站内邮件通知系统
多种资讯排序算法
JavaWeb项目测试和部署，课程总结回顾

# quick-start

[1项目基本配置](#项目基本配置)

[2基本框架开发](#基本框架开发)

[3数据库配置和首页的创建](#数据库配置和首页的创建)




## 项目基础配置

    创建git仓库，本地配置idea并测试pull和push。
    
    创建SpringBoot工程，导入Web，Velocity和Aop的包。
    
    生成Maven项目，pom.xml包含上述依赖，应用名称是toutiao,小组id是com.nowcoder。
    
## 基本框架开发
    
    创建基本的controller，service和model层。
    
    controller中使用注解配置，requestmapping，responsebody基本可以解决请求转发以及响应内容的渲染。responsebody自动选择viewresolver进行解析。
    
    使用pathvariable和requestparam传递参数。
    
    使用velocity编写页面模板，注意其中的语法使用。常用$!{}和${}
    
    使用http规范下的httpservletrequest和httpservletresponse来封装请求和相响应，使用封装好的session和cookie对象。
    
    使用重定向的redirectview和统一异常处理器exceptionhandler
    
AOP和IOC

    IOC解决对象实例化以及依赖传递问题，解耦。
    
    AOP解决纵向切面问题，主要实现日志和权限控制功能。
    
    aspect实现切面，并且使用logger来记录日志，用该切面的切面方法来监听controller。

## 数据库配置和首页的创建

    使用mysql创建数据库和表，建议自己写一下sql到mysql命令行跑一下。
    
    加入mybatis和mysql的maven仓库，注意，由于现在版本的springboot不再支持velocity进而导致我使用较早版本的springboot，所以这里提供一可以正常运行的版本设置。

    springboot使用1.4.0

    mybatis-spring-boot-starter使用1.2.1

    mysql-connector-java使用5.1.6

    亲测可用。
    
    接下来写controller，dao和service。注意mybatis的注解语法以及xml的配置要求，xml要求放在resource中并且与dao接口在相同的包路径下。
    
    application.properties增加spring配置数据库链接地址
    
    两个小工具：
    ViewObject:方便传递任何数据到
    VelocityDateTool:velocity自带工具类
    
    写好静态文件html css和js。并且注意需要配置
    spring.velocity.suffix=.html 保证跳转请求转发到html上
    spring.velocity.toolbox-config-location=toolbox.xml
    
    至此主页基本完成，具体业务逻辑请参考代码。
   
## 用户注册登录以及使用token

完成用户注册和登录的controller,service和dao层代码

新建数据表login_ticket用来存储ticket字段。该字段在用户登录成功时被生成并存入数据库，并被设置为cookie，
下次用户登录时会带上这个ticket，ticket是随机的uuid，有过期时间以及有效状态。

使用拦截器interceptor来拦截所有用户请求，判断请求中是否有有有效的ticket，如果有的话则将用户信息写入Threadlocal。
所有线程的threadlocal都被存在一个叫做hostholder的实例中，根据该实例就可以在全局任意位置获取用户的信息。

该ticket的功能类似session，也是通过cookie写回浏览器，浏览器请求时再通过cookie传递，区别是该字段是存在数据库中的，并且可以用于移动端。

通过用户访问权限拦截器来拦截用户的越界访问，比如用户没有管理员权限就不能访问管理员页面。

配置了用户的webconfiguration来设置启动时的配置，这里可以将上述的两个拦截器加到启动项里。

配置了json工具类以及md5工具类，并且使用Java自带的盐生成api将用户密码加密为密文。保证密码安全。

数据安全性的保障手段：https使用公钥加密私钥解密，比如支付宝的密码加密，单点登录验证，验证码机制等。

ajax异步加载数据 json数据传输等。



    
    


<html>
<!--在这里插入内容-->
</html>


<html>
<!--在这里插入内容-->
</html>


