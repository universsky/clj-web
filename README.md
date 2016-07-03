# Clojure Web Development

A Clojure library designed to ... well, that part is up to you.

# 使用Korma 来访问数据库

## 访问数据库
Java 提供了标准的 JDBC 接口访问数据库，Clojure 的数据库接口
``` clojure
clojure.java.jdbc
```

是对 Java JDBC 的封装。我们只需要引用 clojure.java.jdbc 以及对应的数据库驱动，就可以在 Clojure 代码中访问数据库。

clojure.java.jdbc 是一个比较底层的接口。我们使用 DSL 的模式来编写数据库代码，类似 Java 的 Hibernate，选择 Korma 来编写访问数据库的代码。

由于 Clojure 是 Lisp 方言，它继承了 Lisp 强大的“代码即数据”的功能，在 Clojure 代码中，编写 SQL 语句对应的 DSL 十分自然，完全无需 Hibernate 复杂的映射配置。

## 配置 MySQL 数据库
我们先配置好 MySQL 数据库，然后创建一个表来测试 Clojure 代码.

创建 schema

```sql
CREATE SCHEMA `cljweb` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;

```

创建表

```sql
create table courses (
 `id` varchar(32) not null primary key,
 `name` varchar(50) not null,
 `price` real not null,
 `online` bool not null,
 `days` bigint not null
);

```

新建一个 db.clj 文件，选择菜单“File”-“New”-“Other...”，选择“Clojure”-“Clojure Namespace”，填入名称

```
course.db
```
就可以创建一个 db.clj 文件。


## 添加jar 包依赖
在编写代码前，我们首先要在 project.clj 文件中添加依赖项。

```clojure
[org.clojure/java.jdbc "0.3.6"]
[mysql/mysql-connector-java "5.1.25"]
[korma "0.3.0"]
```

我们执行
```
lein pom
```
可以发现新生成的 pom.xml, 添加了新的依赖:

```xml
<dependencies>
    <dependency>
      <groupId>org.clojure</groupId>
      <artifactId>clojure</artifactId>
      <version>1.8.0</version>
    </dependency>
    <dependency>
      <groupId>org.clojure</groupId>
      <artifactId>java.jdbc</artifactId>
      <version>0.3.6</version>
    </dependency>
    <dependency>
      <groupId>mysql</groupId>
      <artifactId>mysql-connector-java</artifactId>
      <version>5.1.25</version>
    </dependency>
    <dependency>
      <groupId>korma</groupId>
      <artifactId>korma</artifactId>
      <version>0.3.0</version>
    </dependency>
  </dependencies>

```


## 引用 Korma

使用 Korma 操作数据库十分简单，只需要先引用 Korma。


```clojure
(ns
  ^{:author jack}
  org.lightsword.course.db
  (:use korma.db korma.core) )

 ```

## 数据库连接的配置信息

定义数据库连接

```clojure
(defdb korma-db (mysql {
                        :db "cljweb",
                        :host "localhost",
                        :port 3306,
                        :user "root",
                        :password "root"

                        }))

 ```

## 使用数据库表 entity

然后定义一下要使用的 entity，也就是表名。

定义 entity

```clojure
(declare courses)
(defentity courses)
```

## 插入一条记录

现在，就可以对数据库进行操作了。插入一条记录。

执行 insert

```sql
(insert courses
        (values {:id "007", :name "Clojure Programming", :price 45.8, :online true, :days 30})
)
```

使用 Clojure 内置的 map 类型，十分直观。

## 查询

查询语句通过 select 宏实现了 SQL DSL 到 Clojure 代码的自然映射。
执行 select

```sql
(select courses
        (where {:online true})
        (order :name :asc)
)
```

这完全得益于 Lisp 的 S 表达式的威力，既不需要直接拼凑 SQL，也不需要重新发明类似 HQL 的语法。

利用 Korma 提供的 sql-only 和 dry-run，可以打印出生成的 SQL 语句.

## 完整的程序

```clojure

(ns org.lightsword.course.db
  (:use korma.db
        korma.core))


(defdb korma-db (mysql {
                        :db "cljweb",
                        :host "localhost",
                        :port 3306,
                        :user "root",
                        :password "root"

                        }))

(declare courses)
(defentity courses)


(defn create-course! [c]
  (println "create course:" c)
  (insert courses
          (values c)
  )
  )


(defn get-courses []
  (select courses
          (where {:online true})
          (order :name :asc)
          )
  )

(defn get-all []
  (select courses (order :name :asc))
  )

(defn init-courses! []
  (if (empty? (get-courses))
    (let [cs [{:id "s-201", :name "SQL", :price 99.9, :online false, :days 30 },
              {:id "s-202", :name "PHP", :price 69.9, :online false, :days 15},
              {:id "s-203", :name "F#",  :price 80.0, :online false, :days 20}]]

      (println "init courses ...")
      (dorun (map create-course! cs))
      )
    )

  )

(defn -main [& args]
  (init-courses!)
  (println (get-courses))

  (println (get-all))

  )





```

我们配置一下 main

project.clj

```clojure
(defproject org.lightsword/clj-web "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main org.lightsword.course.db
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/java.jdbc "0.3.6"]
                 [mysql/mysql-connector-java "5.1.25"]
                 [korma "0.3.0"]
                 ])


```

运行

```clojure

$ lein run
WARNING: update already refers to: #'clojure.core/update in namespace: korma.core, being replaced by: #'korma.core/update
WARNING: update already refers to: #'clojure.core/update in namespace: org.lightsword.course.db, being replaced by: #'korma.core/update
七月 03, 2016 1:19:55 下午 com.mchange.v2.log.MLog <clinit>
信息: MLog clients using java 1.4+ standard logging.
七月 03, 2016 1:19:55 下午 com.mchange.v2.c3p0.C3P0Registry banner
信息: Initializing c3p0-0.9.1.2 [built 21-May-2007 15:04:56; debug? true; trace: 10]
七月 03, 2016 1:19:55 下午 com.mchange.v2.c3p0.impl.AbstractPoolBackedDataSource getPoolManager
信息: Initializing c3p0 pool... com.mchange.v2.c3p0.ComboPooledDataSource [ acquireIncrement -> 3, acquireRetryAttempts -> 30, acquireRetryDelay -> 1000, autoCommitOnClose -> false, automaticTestTable -> null, breakAfterAcquireFailure -> false, checkoutTimeout -> 0, connectionCustomizerClassName -> null, connectionTesterClassName -> com.mchange.v2.c3p0.impl.DefaultConnectionTester, dataSourceName -> 1hge1619h1cm0f3t1s80nsr|d1f74b8, debugUnreturnedConnectionStackTraces -> false, description -> null, driverClass -> com.mysql.jdbc.Driver, factoryClassLocation -> null, forceIgnoreUnresolvedTransactions -> false, identityToken -> 1hge1619h1cm0f3t1s80nsr|d1f74b8, idleConnectionTestPeriod -> 0, initialPoolSize -> 3, jdbcUrl -> jdbc:mysql://localhost:3306/cljweb, maxAdministrativeTaskTime -> 0, maxConnectionAge -> 0, maxIdleTime -> 10800, maxIdleTimeExcessConnections -> 1800, maxPoolSize -> 15, maxStatements -> 0, maxStatementsPerConnection -> 0, minPoolSize -> 3, numHelperThreads -> 3, numThreadsAwaitingCheckoutDefaultUser -> 0, preferredTestQuery -> null, properties -> {user=******, password=******}, propertyCycle -> 0, testConnectionOnCheckin -> false, testConnectionOnCheckout -> false, unreturnedConnectionTimeout -> 0, usesTraditionalReflectiveProxies -> false ]


[{:id 007, :name Clojure Programming, :price 45.8, :online true, :days 30}]

[{:id 007, :name Clojure Programming, :price 45.8, :online true, :days 30} {:id s-203, :name F#, :price 80.0, :online false, :days 20} {:id s-202, :name PHP, :price 69.9, :online false, :days 15} {:id s-201, :name SQL, :price 99.9, :online false, :days 30}]



```

从运行日志我们可以看出, org.clojure/java.jdbc 底层调用的是

```
driverClass -> com.mysql.jdbc.Driver
```

