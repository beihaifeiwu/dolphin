Mybatis Generator Extent
===========================================
# maven 插件

XMBG扩展自Mybatis Generator插件，增加了一些：  

1. 扩展的方法
2. 类型转换
3. 命名转换（通过正则表达式）
4. 增量生成
5. 自定义注释
6. 其它

##扩展的方法包括：

###批量插入和更新

通过扩展的插件`com.freetmp.mbg.plugin.batch.BatchInsertPlugin`以及`com.freetmp.mbg.plugin.batch.BatchUpdatePlugin`实现

###存在即更新否则插入

通过MBG插件`com.freetmp.mbg.plugin.UpsertPlugin`实现，包括批量操作，生成示例如下：

```xml
<update id="batchUpsert" parameterType="map" >
    <foreach collection="records" item="record" index="index" separator=" ; " >
      update ss_task
      <set >
        <if test="record.id != null" >
          id = #{record.id,jdbcType=BIGINT},
        </if>
        <if test="record.title != null" >
          title = #{record.title,jdbcType=VARCHAR},
        </if>
        <if test="record.description != null" >
          description = #{record.description,jdbcType=VARCHAR},
        </if>
        <if test="record.userId != null" >
          user_id = #{record.userId,jdbcType=BIGINT},
        </if>
      </set>
      <where >
        <include refid="Identifiers_Array_Where" />
      </where>
      ; insert into ss_task(title,description,user_id)
       select #{record.title,jdbcType=VARCHAR},#{record.description,jdbcType=VARCHAR},#{record.userId,jdbcType=BIGINT}
       where not exists (select 1 from ss_task
      <where >
        <include refid="Identifiers_Array_Where" />
      </where>
       )
    </foreach>
</update>
```

存在即更新的操作分为两步，1）判断记录是否存在 2）根据1的结果更新或者插入。标准的sql语法中并没有定义这样的语义或者操作，每个数据库对于存在
即更新的支持都是不一样的，XMBG为提供统一的结构，将这个操作分为两个完全独立的操作，先更新后插入，无论记录存不存在这两个步骤都会执行。为实
现存在即更新的语义，需要一些判断条件以决定记录是否存在，XMBG生成的方法需要用户指定可用作判断条件的字段，方法签名如下：

```java
    int upsert(@Param("record") Task record, @Param("array") String[] array);
    int batchUpsert(@Param("records") List<Task> list, @Param("array") String[] array);
```
其中array参数就是可用作判断条件的字段名称组成的数组，判断条件生成的xml结构如下：

```xml
<sql id="Identifiers_Array_Where" >
<foreach collection="array" item="item" index="index" separator=" and " >
  <if test="item == 'id'" >
    id = #{record.id,jdbcType=BIGINT}
  </if>
  <if test="item == 'title'" >
    title = #{record.title,jdbcType=VARCHAR}
  </if>
  <if test="item == 'description'" >
    description = #{record.description,jdbcType=VARCHAR}
  </if>
  <if test="item == 'userId'" >
    user_id = #{record.userId,jdbcType=BIGINT}
  </if>
</foreach>
</sql>
```
####1.0.0

之前版本的upsert方法在不支持多语句执行的数据库上无法使用（Mysql，PostgreSQL可以），所以使用和分页查询类似的方式，对不同的数据库提供不同
的插件：  

* 针对PostgreSQL的插件 `com.freetmp.mbg.plugin.upsert.PostgreSQLUpsertPlugin`
* 针对MySql的插件 `com.freetmp.mbg.plugin.upsert.MySqlUpsertPlugin`
* 针对DB2的插件 `com.freetmp.mbg.plugin.upsert.DB2UpsertPlugin`
* 针对Hsqldb的插件 `com.freetmp.mbg.plugin.upsert.HsqldbUpsertPlugin`
* 针对Oracle的插件 `com.freetmp.mbg.plugin.upsert.OracleUpsertPlugin`
* 针对SQLServer的插件 `com.freetmp.mbg.plugin.upsert.SQLServerUpsertPlugin`
* 其它待续

###分页查询

类`com.freetmp.mbg.plugin.page.AbstractPaginationPlugin`实现了分页查询的统一数据模型，并在生成的Example模型上添加了操作数据模型
的方法，数据模型包括limit和offset，其中limit是当前页的最大记录数，offset是当前页距离数据库表首行的偏移量。操作方法除了通常的getter和
setter方法外，还增加了fluent API操作，通过一个静态内部类`AbstractPaginationPlugin.PageBuiler`来实现，使用如下:

```java
    XxxExample example = new XxxExample();
    example.bound().offset(0).limit(20).build();
```    

####1.0.0
```java
    UserExample userExample = new UserExample();
    userExample.boundBuilder().limit(10).offset(1).build();
```
    
由于Jdbc的基于游标的分页方式性能较低，因而使用物理分页就成为了唯一的选择，物理分页会因为实际采用数据库的不同有不同的实现方式需要针对具
体的数据库选用不同的插件：

* 针对PostgreSQL的插件 `com.freetmp.mbg.plugin.page.PostgreSQLPaginationPlugin`
* 针对MySql的插件 `com.freetmp.mbg.plugin.page.MySqlPaginationPlugin`
* 针对DB2的插件 `com.freetmp.mbg.plugin.page.DB2PaginationPlugin`
* 针对Hsqldb的插件 `com.freetmp.mbg.plugin.page.HsqldbPaginationPlugin`
* 针对Oracle的插件 `com.freetmp.mbg.plugin.page.OraclePaginationPlugin`
* 针对SQLServer的插件 `com.freetmp.mbg.plugin.page.SQLServerPaginationPlugin`
* 其它待续

分页查询针对的方法为selectByExample，生成示例如下：

```xml
  <select id="selectByExample" resultMap="BaseResultMap" parameterType="com.freetmp.xmbg.mysql.entity.TaskExample">
    select
    <if test="distinct" >
      distinct
    </if>
    <include refid="Base_Column_List" />
    from ss_task
    <if test="_parameter != null" >
      <include refid="Example_Where_Clause" />
    </if>
    <if test="orderByClause != null" >
      order by ${orderByClause}
    </if>
    <if test="limit != null and limit>=0 and offset != null" >
      limit #{offset} , #{limit}
    </if>
  </select>
```

##类型转换

###地理信息类型转换

为屏蔽各个数据库对地理信息系统实现的不统一，XMBG采用第三方地理信息类库geolatte-geom实现对地理信息数据的建模，其Maven构件地址为：  

```xml
    <dependency>
        <groupId>org.geolatte</groupId>
        <artifactId>geolatte-geom</artifactId>
        <version>0.14</version>
    </dependency>
```

地理信息在数据库中的存储形式一般为二进制格式，不便于使用和阅读，每个数据库地理信息扩展都会提供自己的专有函数将二进制数据转换为文本形式,
但是仅凭数据库函数只能转换到文本形式，虽然通过geolatte的DSL很容易将其转换为对应的数据模型，但是XMBG希望这种转换能够自动完成，为此XMBG
提供了一个标准的Mybatis类型解析器`com.freetmp.mbg.typehandler.GeometryTypeHandler`,使用方法和标准的类型解析器使用方法相同：  

```xml
    <!-- mybatis-config.xml -->
    <typeHandlers>
     <typeHandler handler="com.freetmp.mbg.typehandler.GeometryTypeHandler"/>
    </typeHandlers>
```
####Postgis

PostgreSQL的地理信息系统扩展插件Postgis为PostgreSQL数据库提供了存储和处理地理信息数据的函数和数据类型，Postgis的地理信息数据的存取
需要配合使用另外一个数据库驱动`org.postgis:postgis-jdbc:1.3.3`，地理信息数据在Postgis中存储为二进制（WKB）格式的数据，在将其转换
为geolatte中的对应模型之前需要将其转换为文本形式（WKT）的数据，在Postgis中这通常通过函数调用实现，因此XMBG必须对MBG生成的基本的sql
语句进行替换，具体点就是凡是数据写入的地方均使用函数调用`ST_GeomFromText(xx,xx)`,在select查询的字段中凡是地理信息数据的均使用函数
调用`ST_AsText(xx)`代替，这种替换使用插件`com.freetmp.mbg.plugin.geom.PostgisGeoPlugin`实现。

##命名转换

原生的MBG并没有提供灵活的命名转换策略，仅提供了一种配置可以解决列名前缀问题
[columnRenamingRule](http://mybatis.github.io/generator/configreference/columnRenamingRule.html),使用方法如下：
```xml
    <columnRenamingRule searchString="^CUST_" replaceString="" />
```
但对于将列名转换为Java的驼峰命名的形式，MBG则没有给出解决方法。对于此XMBG通过插件`com.freetmp.mbg.plugin.ColumnNameConversionPlugin`
提供了一种解决方案，通过正则表达式来匹配列名中的每一个单词项，然后再将所有的单词项组合在一下，且首个单词小写。作为插件的用法如下：
```xml
    <plugin type="com.freetmp.mbg.plugin.ColumnNameConversionPlugin">
        <property name="columnPattern" value="[A-Z][a-z]*" />
    </plugin>
```
##增量生成

一般情况下，使用MBG生成代码之后就不会再使用MBG了，这样增量生成也就没有用武之地了，但是当数据库结构发生变化时，手动修改MBG的生成代码就
会变得比较费时费力，而且使用MBG重新生成又会丢失生成后做的修改，这时就用到了增量生成，增量生成不会删除之前生成的代码，只会把数据库表中与
之前生成不一致的添加之前生成的文件中，这种方式非常适合数据库表增加字段的情况。

MBG本身并没有提供增量生成的支持，MBG的eclipse插件借助AST抽象语法树实现了对增量生成的支持，MBG的maven插件却没有相应的支持，XMBG通过
依赖第三方包装的AST构件提供了相应的内容合并插件`com.freetmp.mbg.plugin.ContentMergePlugin`从而实现了对增量生成的支持，由于是通过
插件来实现的，当然也受到一些插件的限制。插件中有些是添加新的生成内容的，为避免把这部分代码遗漏，必须保证 **内容合并插件最后被执行**

####0.0.1
重构内容合并插件， **使用MBG的ShellCallback扩展点对Java源文件进行合并** ，并去除AST到MBG中Java dom的转换步骤，新旧源文件都使用ast进行解析
合并，这使XMBG可以提供对枚举、注解等类型的支持`com.freetmp.mbg.shellcallback.MergeSupportedShellCallback`, MBG中XmlFileMergerJaxp
提供了对XML合并的简单支持，但其合并方式会直接删除旧的生成xml节点和属性，与直观意义上的合并并不一致，所以XMBG仍需对xml的合并提供支持，但
ShellCallback并没有提供相应的扩展点，所以XML的合并仍是通过插件来实现的`com.freetmp.mbg.plugin.XMLMergePlugin`

####1.0.0
使用javaparser重构内容合并工具集，解决合并过程中造成的方法声明重复

##自定义注释

给自动生成的代码添加有意义的注释通常是有用的，尤其是代码的维护，MBG对注释生成的支持非常有限而且没有实际意义，XMBG通过继承扩展了注释生成器
`org.mybatis.generator.internal.DefaultCommentGenerator`的功能，包括XML中的注释以及Java源文件的版权声明，默认使用的是Apache的开源
版权声明。XMBG默认提供了英文和中文两个版本的注释资源，保存在类路径下的`i18n_for_CG`文件夹下，用户可以提供自己的注释资源，普通注释以`Comments`
命名，版权声明以`Copyrights`命名，可以参考默认资源的实现方式。

```properties
    # XML映射文件中POJO字段与数据库表列的对应声明
    BaseResultMap=the basic mapping of POJO fields and db table's columns
    ResultMapWithBLOBs=the mapping of POJO fields and db table's columns with type BLOB in it
    # 可重用的SQL片段声明
    Example_Where_Clause=the where condition clause of the helper class example
    Update_By_Example_Where_Clause=the where condition for updating the db data using the example helper class
    Base_Column_List=the basic columns of db table used by select
    Blob_Column_List=the columns of db table used by select with type BLOB in it
```
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

| 参数                      | 属性表达式                                   | 类型                | 注释 |
| ---------                 |   ----------                                  | ----              | -------- |
|configurationFile          |${mybatis.generator.configurationFile}         |java.io.File       |配置文件的位置，默认值为${basedir}/src/main/resources/generatorConfig.xml|
|contexts	                |${mybatis.generator.contexts}	                |java.lang.String	|使用逗号分隔的多个上下文标识符，若指定则使用指定的上下文，否则使用全部|
|jdbcDriver	                |${mybatis.generator.jdbcDriver}                |java.lang.String	|如果指定了sqlScript则使用本driver连接数据库|
|jdbcPassword	            |${mybatis.generator.jdbcPassword}	            |java.lang.String	|如果指定了sqlScript则使用本password连接数据库|
|jdbcURL	                |${mybatis.generator.jdbcURL}                   |java.lang.String	|如果指定了sqlScript则使用本url连接数据库|
|jdbcUserId	                |${mybatis.generator.jdbcUserId}                |java.lang.String	|如果指定了sqlScript则使用本user连接数据库|
|outputDirectory            |${mybatis.generator.outputDirectory}	        |java.io.File	    |指定XMBG生成文件的存放文件夹，当targetProject属性被设置为“Maven”时启用本属性|
|overwrite	                |${mybatis.generator.overwrite}                 |boolean	        |设置为true覆盖之前生成的文件|
|sqlScript	                |${mybatis.generator.sqlScript}                 |java.lang.String	|指定要在XMBG生成代码前运行的sql文件的路径|
|tableNames	                |${mybatis.generator.tableNames}                |java.lang.String	|使用逗号分隔的多个数据库表名|
|verbose	                |${mybatis.generator.verbose}                   |boolean	        |设置为true，XMBG会输出处理日志|
|disableExtendMethods       |${x.mybatis.generator.disableExtendMethods}    |boolean            |设置为true，关闭扩展方法的生成|
|disableGeom                |${x.mybatis.generator.disableGeom}             |boolean            |设置为true, 关闭地理信息相关代码的生成|
|disableNameConversion      |${x.mybatis.generator.disableNameConversion}   |boolean            |设置为true，关闭命名转换服务|
|disablePagination          |${x.mybatis.generator.disablePagination}       |boolean            |设置为true，关闭物理分页代码的生成|
|disableMergeSupport        |${x.mybatis.generator.disableMergeSupport}     |boolean            |设置为true，关闭内容合并服务|
|enableQueryDslSupport      |${x.mybatis.generator.enableQueryDslSupport}   |boolean            |设置为true，启用QueryDSL支持|
|columnPattern              |${x.mybatis.generator.columnPattern}           |java.lang.String   |如果启用了命名转换服务，可通过本属性指定匹配单个单词的正则表达式|
|srid                       |${x.mybatis.generator.srid}                    |java.lang.String   |如果启用地理信息相关代码生成，可通过本属性指定其使用的空间引用标识符|
|i18nPath                   |${x.mybatis.generator.i18nPath}                |java.io.File       |指定自定义的注释资源所在的文件夹|
|locale                     |${x.mybatis.generator.locale}                  |java.lang.String   |指定本次代码生成使用的locale，默认为en_US|
|projectStartYear           |${x.mybatis.generator.projectStartYear}        |java.lang.String   |指定项目开始的年份，用在版权声明中，默认为今年|
|addToProjectAsCompileSource|${x.mybatis.generator.addToProjectAsCompileSource} |boolean        |设置为true将生成的文件添加到compile阶段, 默认为false|
|addToProjectAsTestCompileSource|${x.mybatis.generator.addToProjectAsTestCompileSource} |boolean        |设置为true将生成的文件添加到test compile阶段，默认为true|