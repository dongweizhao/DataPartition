# DataPartition
基于mycat分片算法，对未分片数据进行分片
### 适用对象
使用mycat做为数据库中间件或使用mycat分片算法的数据切分需求
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

### 使用说明
1. pom.xml

  <pre>
  <code>
    <properties>
               <!-- logback相关配置 -->
               <dyn.logback.home>/Users/dongweizhao/logs</dyn.logback.home>
               <dyn.logback.level>debug</dyn.logback.level>
               <!--数据文件根目录-->
               <dyn.generate.path>/Users/dongweizhao/DM_generate</dyn.generate.path>
               <!--分片数量-->
               <dyn.generate.count>32</dyn.generate.count>
               <!--中间库地址配置-->
               <!--<dyn.datasource.url>-->
                   <!--<![CDATA[jdbc:mysql://127.0.0.1:3306/cap_2?zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=utf-8]]></dyn.datasource.url>-->
               <!--<dyn.datasource.username>cap</dyn.datasource.username>-->
               <!--<dyn.datasource.password>cap</dyn.datasource.password>-->
               <!--空值替换符 mysql中为\N-->
               <dyn.nullValue>\N</dyn.nullValue>
               <dyn.datasource.url>jdbc:oracle:thin:@10.211.55.4:1521:orcl</dyn.datasource.url>
               <dyn.datasource.username>eadt1</dyn.datasource.username>
               <dyn.datasource.password>eadt1</dyn.datasource.password>
               <dyn.dialect>oracle</dyn.dialect>
               <dyn.maxResults>100</dyn.maxResults>
               <!--分片文件内容分割符-->
               <dyn.separator>|||</dyn.separator>
           </properties>
    </code>
  </pre>
