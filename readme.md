#安装  
三方jar库位于lib目录下
帮助类：  
mvn install:install-file -DgroupId=pf.java -DartifactId=pfHelper -Dversion=0.0.1-SNAPSHOT -Dfile=pfHelper-0.0.1-SNAPSHOT.jar -Dpackaging=jar -DgeneratePom=true  
sqlserver的jdbc:  
mvn install:install-file -Dfile=sqljdbc42.jar -Dpackaging=jar -DgroupId=com.microsoft.sqlserver -DartifactId=sqljdbc4 -Dversion=6.0

# 发包运行方式

## 快捷方式运行(推荐)
1. 电脑重启时自动执行的任务请运行run.vbs(这样会补执行当 天|月 可能由于停机而漏掉的任务)
2. 手动启动时可以选择运行run_jumpPassedTask.vbs(这样启动不会执行当 天|月 超过时间点的任务)

## cmd指令(可选,只是列出参数作为参考)
1. 测试: java -jar pfTransferTask-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev --server.port=28613 --jumpThePassedTask=true
2. 服务器运行:  --jumpThePassedTask=true
3. 服务器停电后自动重启:  无参数


#Windows Swt UI方式

#api接口

##服务器域名
transfer.perfect99.com
开发 http://localhost:28612
正式 http://10.20.0.60:28611

##月结日结接口
 月结日结接口：  
1. 立刻运行定时任务  
[http://10.20.0.60:28612/Task?task=MonthOtherData](http://10.20.0.60:28612/Task?task=MonthOtherData)
[http://transfer.perfect99.com:28611/Task?task=MonthOtherData](http://transfer.perfect99.com:28611/Task?task=MonthOtherData)
2. 查看定时任务进度      
[http://10.20.0.60:28612/TaskStatus?task=MonthOtherData](http://10.20.0.60:28612/TaskStatus?task=MonthOtherData)
[http://transfer.perfect99.com:28611/TaskStatus?task=MonthOtherData](http://transfer.perfect99.com:28611/TaskStatus?task=MonthOtherData)

##业绩规则接口   
1. 活跃pv
http://transfer.perfect99.com:28611/GetPvStandard?date=2022-02-17%2000%3A00%3A00

task的值为：  
1. MonthOrdersData   月结订单部分  
2. MonthHyData       月结会员部分  
3. MonthOtherData    月结的其它表(如t_agent)
4. (更多见com.perfect99.pfTransferTask.model.TimeTaskEnum)  

#Spark方式
1. 开启sark模式  
com.perfect99.pfTransferTask.configuration.CommandLineRunnerImpl.runInCloud=true
2. 设置需要执行的任务(可以考虑优化自定义这部分的方式)   com.perfect99.pfTransferTask.configuration.CommandLineRunnerImpl.RunCloudSpark中

#迁移数据的任务定义
1. 任务实体在com.perfect99.pfTransferTask.projHelper.tasks包内(参考tag\_monthly\_user、tag\_user\_profile\_attr_all等类)
2. 任务实体对应的脚本在\LocalData\Txt
   脚本中可自动替换的变量有:
   {cmonth}  //2021.10
   {nmonth}  //2021-10
   {last_cmonth}
   {next_cmonth}
   {year}
   {last_year_cmonth} //上年的月
   {next_month_year}  //下月的年
   {ym}  //yyyyMM
   {active_pv_standard}  //多少pv算活跃

#地址  
##测试地址  
[http://localhost:28601/UploadJar  (post)](http://localhost:28601/UploadJar)    
[http://localhost:28601/StopSpring](http://localhost:28601/StopSpring)  
[http://localhost:28601/RunSpring](http://localhost:28601/RunSpring)  

##eureka地址：
[http://localhost:8761/eureka/apps](http://localhost:8761/eureka/apps)  

#系统目录(要放到jar同级):
1. ymlConfig
2. LocalData
3. lib

#erake配置  

```
zuul:
  sensitive-headers: false
  routes:
    api-a:
      path: /api-a/**
      serviceId: service-ribbon
    api-b:
      path: /api-b/**
      serviceId: service-feign
    api-c:
      path: /crm/**
      serviceId: service-crm
      strip-prefix: false    
    api-d:
      path: /seller/**
      serviceId: perfect-query
      strip-prefix: false
```

cloud结构
  erake必需
  service-config必需
  zuul   http://localhost:28701/seller/getdsellerno?hybh=00003049
  spring boot  http://localhost:28711/crm/getsalesday
  
三方包：
打包后可直接在服务器运行，不需要在服务器执行安装指令
1.pfHelper
  见https://gitee.com/sellgirl/PaySellgirl
2.为了有sqlserver的bulk
  6.0版本 sqljdbc42.jar
  mvn install:install-file -Dfile=sqljdbc42.jar -Dpackaging=jar -DgroupId=com.microsoft.sqlserver -DartifactId=sqljdbc4 -Dversion=6.0
3.swt
  mvn install:install-file -Dfile=org.eclipse.swt.win32.win32.x86_64-4.2.2.jar -Dpackaging=jar -DgroupId=org.eclipse.swt -DartifactId=org.eclipse.swt.win32.win32.x86_64 -Dversion=4.2.2

　　
  
#各个配置
1. 开发环境(DEV)：开发环境是程序猿们专门用于开发的服务器，配置可以比较随意， 为了开发调试方便，一般打开全部错误报告。

2. 测试环境(UAT)：一般是克隆一份生产环境的配置，一个程序在测试环境工作不正常，那么肯定不能把它发布到生产机上。

3. 预生产(pre)

4. 生产环境(PROD)：是指正式提供对外服务的，一般会关掉错误报告，打开错误日志。可以理解为包含所有的功能的环境，任何项目所使用的环境都以这个为基础，然后根据客户的个性化需求来做调整或者修改。

三个环境也可以说是系统开发的三个阶段：开发->测试->上线，其中生产环境也就是通常说的真实环境。

UAT环境：UAT，(User Acceptance Test),用户接受度测试 即验收测试，所以UAT环境主要是用来作为客户体验的环境。

仿真环境：顾名思义是和真正使用的环境一样的环境（即已经出售给客户的系统所在环境，也成为商用环境），所有的配置，页面展示等都应该和商家正在使用的一样，差别只在环境的性能方面。

#邮件指令:
1. pfTransferTask\_MonthOrdersData\_Stop
2. pfTransferTask\_MonthOrdersData\_Status
3. pfTransferTaskDataMonitor   //数据监测
4. pfTransferTask\_MonthTaxacData\_2021-08-12 03:00:00
5. pfTransferTaskFinishStatus  //查看所有任务的完成情况
6. pfTransferTaskRedoFailed    //重做失败的任务
7. pfTransferTaskUpdateSysSetting_needValidateLiGeDayOrderApi_1  //设置系统参数

备份脚本说明:
1.如果sql提供了sys_limit_id属性,抛错误时会提供最大值

找导入时的错误可以这样做:
1.accessTask按钮,找到lastInsertedId
2.脚本里加id>lastInsertedId
3.findErr按钮
注意,如果直接findErr的话,由于limit的offset有可能变得很大,那样越到后面越慢的

#环境变量:
```
yjQuery		YJQUERY_DB_IP  		10.0.0.11:1433
day  		DAY_DB_IP  		192.168.0.33:1433
				DAY_DB_IP_TEST		192.168.0.29:1433
bonus		BONUS_DB_IP		192.168.0.30:1433
				BONUS_DB_IP_TEST	192.168.0.29:1433
yunXiShop	YUNXISHOP_DB_IP		172.100.37.88:3306
liGeProd	LIGE_PROD_DB_IP		dzqv2n74qe5gzdn7ol10-rw4rm.rwlb.rds.aliyuncs.com
liGeShop	LIGESHOP_DB_IP		cloud.perfect99.com:10129
liGeOrder	cloud.perfect99.com:10077
userProfile 	USER_PROFILE_DB_IP	cloud.perfect99.com:20006
```

月结表说明
t_giftsq 礼券设置
  读全量数据
  要礼券:发放时给他礼券
  不要礼券:发放时发工资给他
  
t_taxac 为什么应该由对方推过来,原因是
礼券到期2部分:
  1.到期未用
            这部分可能产生的问题是,本来下单用掉礼券,那就应该是未到期;但后来如果把单退了,那就应该算作到期才对的
  2.未到期提现
银行手续费:
      扣工资时用的
      
todo:
1.sellgirl 读到product_product时 price money格式有问题
2.PfTransferTaskApp.java 发送dba异常邮件时,连续发了多个,没有时间间隔,可能是多线程的问题 

## 剪贴板
使用复制到剪贴板的功能时,需要在启动jar时加VM参数: -Djava.awt.headless=false

# 数据库
## sellgirl数据库
数据库的创建脚本在百度网盘的 我的网盘/Documents/BiscuitLu/Documents/SQL ServerManagement Studio/Projects/SellGirl目录中应该有
下载到了D:\download\SellGirl

# 项目结构说明
## 定时任务tab
CommandLineRunnerImpl.java
ITransferTimeTaskHelper helper=...

## 项目交接说明
1.	项目路径
D:\eclipse_workspace_gitlab_pfTransferTask\pfTransferTask	 (local)
http://gitlab.perfect99.com/wxj/pftransfertask	 (gitlab)
2.	数据库配置
D:\eclipse_workspace_gitlab_pfTransferTask\pfTransferTask\src\main\resources
3.	对外接口
月结/日结读数接口(王现伟月结时会调用)
http://transfer.perfect99.com:28611/TaskStatus?task=MonthOtherData
http://transfer.perfect99.com:28611/Task?task=MonthOtherData
4.	邮件指令
此指令 严萍 有时会调用邮件指令:
发送pfTransferTask_MonthTaxacData_2022-05-11 到PFEmailSend.EMAIL_OWNER_ADDR
系统是自动监听的,相关代码在TransferTimeTaskHelper.ListenEmail()
5.	设置系统邮件,用于系统邮件相关
PFEmailSend.EMAIL_OWNER_ADDR="";
6.	服务器
10.20.0.60  D:\wxj\pfTransferTask
可以通过桌面快捷方式”run_jumpPassedTask.vbs - 快捷方式”来运行
7.	数据库脚本
Java作业定义
D:\eclipse_workspace_gitlab_pfTransferTask\pfTransferTask\src\main\java\com\perfect99\pfTransferTask\projHelper\tasks\*.java
脚本在下面目录
D:\eclipse_workspace_gitlab_pfTransferTask\pfTransferTask\LocalData\Txt
D:\eclipse_workspace_gitlab_pfTransferTask\pfTransferTask\SystemLocalData\Txt

8.	启动参数
本地运行时
--spring.profiles.active=dev --server.port=28613 --jumpThePassedTask=true
服务器运行时
--jumpThePassedTask=true
