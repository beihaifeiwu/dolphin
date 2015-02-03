# Mybatis Generator Extent
## maven 插件

XMBG扩展自Mybatis Generator插件，增加了一些：  

1. 扩展的方法
2. 类型转换
3. 命名转换（通过正则表达式）
4. 增量生成
5. 其它

##扩展的方法包括：

###批量插入和更新

通过扩展的插件`com.freetmp.mbg.plugin.batch.BatchInsertPlugin`以及`com.freetmp.mbg.plugin.batch.BatchUpdatePlugin`实现

###存在即更新否则插入

通过MBG插件`com.freetmp.mbg.plugin.UpsertPlugin`实现，包括批量操作

###分页查询

类`com.freetmp.mbg.plugin.page.AbstractPaginationPlugin`实现了分页查询的统一数据模型，并在生成的Example模型上添加了操作数据模型
的方法，数据模型包括limit和offset，其中limit是当前页的最大记录数，offset是当前页距离数据库表首行的偏移量。操作方法除了通常的getter和
setter方法外，还增加了fluent API操作，通过一个静态内部类`AbstractPaginationPlugin.PageBuiler`来实现，使用如下:

    XxxExample example = new XxxExample();
    example.bound().offset(0).limit(20).build();
    
由于Jdbc的基于游标的分页方式性能较低，因而使用物理分页就成为了唯一的选择，物理分页会因为实际采用数据库的不同有不同的实现方式需要针对具
体的数据库选用不同的插件：

* 针对PostgreSQL的插件 `com.freetmp.mbg.plugin.page.PostgreSQLPaginationPlugin`
* 针对MySql的插件 `com.freetmp.mbg.plugin.page.MySqlPaginationPlugin`
* 其它待续

##类型转换

###地理信息类型转换

为屏蔽各个数据库对地理信息系统实现的不统一，XMBG采用第三方地理信息类库geolatte-geom实现对地理信息数据的建模，其Maven构件地址为：  

    <dependency>
        <groupId>org.geolatte</groupId>
        <artifactId>geolatte-geom</artifactId>
        <version>0.14</version>
    </dependency>

地理信息在数据库中的存储形式一般为二进制格式，不便于使用和阅读，每个数据库地理信息扩展都会提供自己的专有函数将二进制数据转换为文本形式,
但是仅凭数据库函数只能转换到文本形式，虽然通过geolatte的DSL很容易将其转换为对应的数据模型，但是XMBG希望这种转换能够自动完成，为此XMBG
提供了一个标准的Mybatis类型解析器`com.freetmp.mbg.typehandler.GeometryTypeHandler`,使用方法和标准的类型解析器使用方法相同：  

    <!-- mybatis-config.xml -->
    <typeHandlers>
     <typeHandler handler="com.freetmp.mbg.typehandler.GeometryTypeHandler"/>
    </typeHandlers>

####Postgis

PostgreSQL的地理信息系统扩展插件Postgis为PostgreSQL数据库提供了存储和处理地理信息数据的函数和数据类型，Postgis的地理信息数据的存取
需要配合使用另外一个数据库驱动`org.postgis:postgis-jdbc:1.3.3`，地理信息数据在Postgis中存储为二进制（WKB）格式的数据，在将其转换
为geolatte中的对应模型之前需要将其转换为文本形式（WKT）的数据，在Postgis中这通常通过函数调用实现，因此XMBG必须对MBG生成的基本的sql
语句进行替换，具体点就是凡是数据写入的地方均使用函数调用`ST_GeomFromText(xx,xx)`,在select查询的字段中凡是地理信息数据的均使用函数
调用`ST_AsText(xx)`代替，这种替换使用插件`com.freetmp.mbg.plugin.geom.PostgisGeoPlugin`实现。

##命名转换

原生的MBG并没有提供灵活的命名转换策略，仅提供了一种配置可以解决列名前缀问题
[columnRenamingRule](http://mybatis.github.io/generator/configreference/columnRenamingRule.html),使用方法如下：

    <columnRenamingRule searchString="^CUST_" replaceString="" />

但对于将列名转换为Java的驼峰命名的形式，MBG则没有给出解决方法。对于此XMBG通过插件`com.freetmp.mbg.plugin.ColumnNameConversionPlugin`
提供了一种解决方案，通过正则表达式来匹配列名中的每一个单词项，然后再将所有的单词项组合在一下，且首个单词小写。作为插件的用法如下：

    <plugin type="com.palmaplus.mbg.plugin.ColumnNameConversionPlugin">
        <property name="columnPattern" value="[A-Z][a-z]*" />
    </plugin>

##增量生成

一般情况下，使用MBG生成代码之后就不会再使用MBG了，这样增量生成也就没有用武之地了，但是当数据库结构发生变化时，手动修改MBG的生成代码就
会变得比较费时费力，而且使用MBG重新生成又会丢失生成后做的修改，这时就用到了增量生成，增量生成不会删除之前生成的代码，只会把数据库表中与
之前生成不一致的添加之前生成的文件中，这种方式非常适合数据库表增加字段的情况。

MBG本身并没有提供增量生成的支持，MBG的eclipse插件借助AST抽象语法树实现了对增量生成的支持，MBG的maven插件却没有相应的支持，XMBG通过
依赖第三方包装的AST构件提供了相应的内容合并插件`com.freetmp.mbg.plugin.ContentMergePlugin`从而实现了对增量生成的支持，由于是通过
插件来实现的，当然也受到一些插件的限制。插件中有些是添加新的生成内容的，为避免把这部分代码遗漏，必须保证 **内容合并插件最后被执行**

##其它

###Mapper文件覆写

MBG默认没有启用对Mapper（记录sql与方法映射的xml文件）的覆写，使用了追加的方式，追加的方式与增量生成的机制相冲突，因而需要启用对Mapper
文件的覆写，但是MBG并没有提供提供覆写Mapper的入口（可能是一个bug），所以XMBG提供了`com.freetmp.mbg.plugin.MapperOverwriteEnablePlugin`
插件对Mapper文件覆写提供支持

###QueryDsl支持

QueryDsl是一个很灵活的数据访问层，XMBG也提供了对QueryDSL的支持，通过插件`com.freetmp.mbg.plugin.QueryDslPlugin`为每一个生成的
实体Model添加类级别的注解`com.mysema.query.annotations.QueryEntity`，让QueryDSL的maven插件可以识别

##XMBG插件的使用

XMBG兼容大部分[MBG的配置](http://mybatis.github.io/generator/configreference/xmlconfig.html)，
在MBG配置的基础上XMBG添加了一些针对上述五部分内容的配置，具体说明如下:
