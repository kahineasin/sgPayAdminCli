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

# 指令
1. 

```bash
# 普通模式
java -jar ./sgPayAdminCli-0.0.1-jar-with-dependencies.jar  import /path/to/data.xlsx -s myserver.com -u ubuntu --key ~/.ssh/id_rsa

# JSON 模式（供 AI Agent 解析）
java -jar ./sgPayAdminCli-0.0.1-jar-with-dependencies.jar import /path/to/data.xlsx -s myserver.com --json
```

2. 导入数据 

```
java -jar ./sgPayAdminCli-0.0.1-jar-with-dependencies.jar import D:\cache\html1\cliTestData\comic.xlsx -r D:\cache\html1\cliTestData\漫画\漫画预览图 -o D:\cache\html1\cliTestData\out -t comic
```