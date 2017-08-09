# DataPartition
基于mycat分片算法，对未分片数据进行分片,并导入
### 适用对象
使用mycat做为数据库中间件或使用mycat分片算法的应用系统的数据切分需求
### 支持数据库
+ mysql
+ oracle
+ db2
+ sqlserver

### 支持算法
+ 分片枚举
+ 固定分片hash算法
+ 范围约定
+ 取模
+ 按日期(天)分片
+ 取模范围约束
+ 一致性hash
+ 按单月小时拆分
+ 范围求模分片
+ 日期范围 hash 分片
+ 自然月分片

### 初始配置
1. 配置pom.xml
```xml
<properties>
    <!-- logback相关配置 -->
    <dyn.logback.home>/Users/dongweizhao/logs</dyn.logback.home>
    <dyn.logback.level>debug</dyn.logback.level>
    <!--分片数据文件根目录-->
    <dyn.generate.path>/Users/dongweizhao/DM_generate</dyn.generate.path>
    <!--分片数量-->
    <dyn.generate.count>32</dyn.generate.count>
    <!--空值替换符 mysql中为\N-->
    <dyn.nullValue>\\N</dyn.nullValue>
    <dyn.datasource.url>jdbc:oracle:thin:@localhost:1521:orcl</dyn.datasource.url>
    <dyn.datasource.username>test</dyn.datasource.username>
    <dyn.datasource.password>test</dyn.datasource.password>
    <!--数据库类型-->
    <dyn.dialect>oracle</dyn.dialect>
    <!--每页查询数据大小-->
    <dyn.maxResults>100</dyn.maxResults>
    <!-- 文件内容固定分隔符-->
    <dyn.separator>|||</dyn.separator>
    <!--需要分片的表,多表已","分割, ps:多表不能换行-->
    <dyn.tables>t_cap_user_auth_info</dyn.tables>
    <!--需要分片的表对应的分片键,多表已","分割, ps:多表不能换行-->
    <dyn.partitonKey>user_id</dyn.partitonKey>
    <!--需要导入的目标数据库节点-->
    <dyn.targert.node.datasource>
        DBS[0]='mysql -h localhost -u cap -pcap cap_1';
        DBS[1]='mysql -h localhost -u cap -pcap cap_2';
    </dyn.targert.node.datasource>
</properties>
```

### 源码启动

  调用Startup进行启动,进行数据分片文件生成

### 基于编译包使用
1.  执行mvn package 对程序进行打包，获取程序zip包，解压

2. 在bin目录下调用startup.sh启动,进行数据分片文件生成

### 数据导入
在bin目录下调用import.sh T 进行分片数据导入(import.sh 可以自定义导入具体查看import.sh中注释)
