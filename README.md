## Storm 範例練習（log-topology)

### 一、來源：

  * [6. Packaging Storm Topologies](http://docs.hortonworks.com/HDPDocuments/HDP2/HDP-2.3.0/bk_storm-user-guide/content/packaging-storm-topologies.html)
  * [Storm Real-time Processing Cookbook](https://www.packtpub.com/big-data-and-business-intelligence/storm-real-time-processing-cookbook)
  * Bitbucket: https://bitbucket.org/qanderson/log-topology
  * [Eclipse Maven Drools 5.5.0.Final Example](http://hocinegrine.com/2013/08/21/eclipse-maven-drools-5-5-0-final-example/)

### 二、執行方式：

```shell
# 1. 先安裝 storm-cassandra 到 Maven 的 local repository
git clone https://github.com/quintona/storm-cassandra
cd storm-cassandra
mvn install -DskipTests

# 下載本 respository
git clone https://github.com/desp0916/log-topology

# 如果你沒有要用 Eclipse 編譯的話，此步驟可以省略。
# 產生 Eclipse 的 .classpath 和 .project 等檔案。
cd log-topology
mvn eclipse:eclipse

# 編譯、測試、打包
mvn clean package

# 將 Topology 提交到 Storm Cluster
storm jar target/log-topology-0.0.1-SNAPSHOT.jar ${ToplogyName} ${RedisHost}
```

### 三、Other Repositories
https://bitbucket.org/desp0916/log-topology

