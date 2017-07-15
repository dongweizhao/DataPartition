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
    <dyn.nullValue>\N</dyn.nullValue>
    <dyn.datasource.url>jdbc:oracle:thin:@10.211.55.4:1521:orcl</dyn.datasource.url>
    <dyn.datasource.username>eadt1</dyn.datasource.username>
    <dyn.datasource.password>eadt1</dyn.datasource.password>
    <!--数据库类型-->
    <dyn.dialect>oracle</dyn.dialect>
    <!--每页查询数据大小-->
    <dyn.maxResults>100</dyn.maxResults>
    <!-- 文件内容固定分隔符-->
    <dyn.separator>|||</dyn.separator>
</properties>
```
2. beans.xml
 ```xml
 <bean id="gnrParams" class="org.pub.DataPartitoin.util.GnrParams">
    <property name="settings">
        <map>
            <entry key="maxResults" value="${maxResults}"/>
            <entry key="path" value="${generatePath}"/>
            <entry key="separator" value="${separator}"/>
            <entry key="dialect" value="${dialect}"/>
            <entry key="dataCount" value="${node.count}"></entry>
            <entry key="nullValue" value="${dyn.nullValue}"></entry>
        </map>
    </property>
    <property name="tables">
        <map>
            <!--添加分片表 key 分片表  value 分片键-->
            <entry key="eadept" value="deptid"></entry>
        </map>
    </property>
</bean>
 ```
