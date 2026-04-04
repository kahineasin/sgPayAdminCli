# 打包
测试

```
mvn package assembly:single
```
正式(jar内排除yml)

```
mvn clean package assembly:single -Pprod-build
```

# 运行
//test on openjdk version "17.0.8" 2023-07-18

```
java -jar ./sgPayAdminCli-0.0.1-jar-with-dependencies.jar
```
# 测试指令
//test on openjdk version "17.0.8" 2023-07-18 

1.demo 

	```bash 
	# 普通模式
	java -jar ./sgPayAdminCli-0.0.1-jar-with-dependencies.jar  import /path/to/data.xlsx -s myserver.com -u ubuntu --key ~/.ssh/id_rsa
	
	# JSON 模式（供 AI Agent 解析）
	java -jar ./sgPayAdminCli-0.0.1-jar-with-dependencies.jar import /path/to/data.xlsx -s myserver.com --json
	```
2.上传图片 

	```bash
	java -jar ./sgPayAdminCli-0.0.1-jar-with-dependencies.jar upload -l D:\cache\html1\cliTestData\out -r /root/myapp/shop/static/resourceImg

# 指令
//test on openjdk version "17.0.8" 2023-07-18

1.导入数据 
如果中途报错,下次不想重新来,可以加参数 -b 101 ,这样从excel的101行开始导入,之前的行跳过(可以通过打印的lastId判断应该从哪一行继续)(如果-b的值不准确,可能导致数据重复)

	```bash
	java -jar ./sgPayAdminCli-0.0.1-jar-with-dependencies.jar import D:\cache\html1\cliTestData\comic.xlsx -r D:\cache\html1\cliTestData\漫画\漫画预览图 -o D:\cache\html1\cliTestData\out -t comic
	```	
如果上传失败,执行以下删除"今天导入的数据"

	```bash
	java -jar ./sgPayAdminCli-0.0.1-jar-with-dependencies.jar delete -t comic
	```	 
2.上传图片 (慎重)
这里需要慎重,稍微浏览out目录下的文件是否类似"comic,comic60,comic120,comic200",再确定"-r 服务器图片路径"是否正确

	```bash
	java -jar ./sgPayAdminCli-0.0.1-jar-with-dependencies.jar upload -l D:\cache\html1\cliTestData\out -r /root/myapp/shop/static/resourceImg
	```
3.查看今天上传的数据量

	```bash
	java -jar ./sgPayAdminCli-0.0.1-jar-with-dependencies.jar total -t comic
	```