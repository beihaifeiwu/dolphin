package com.freetmp.mbg.plugin

import com.alibaba.druid.sql.SQLUtils
import com.alibaba.druid.util.JdbcUtils
import com.freetmp.mbg.plugin.page.*
import groovy.util.logging.Slf4j
import org.mybatis.generator.api.Plugin
import org.mybatis.generator.api.dom.java.Field
import org.mybatis.generator.api.dom.java.InnerClass
import org.mybatis.generator.api.dom.java.Method
import org.mybatis.generator.api.dom.xml.Attribute
import org.mybatis.generator.api.dom.xml.Element
import org.mybatis.generator.api.dom.xml.XmlElement
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.SelectByExampleWithoutBLOBsElementGenerator

/**
 * Created by LiuPin on 2015/5/21.
 */
@Slf4j
class PaginationPluginSpec extends AbstractPluginSpec {

  XmlElement selectByExample = Spy(constructorArgs: ["selectByExample"], attributes: [
      new Attribute("resultMap", "BaseResultMap"), new Attribute("parameterType", User.class.canonicalName + "Example")
  ])

  def buildExample() {
    UserExample example = new UserExample()
    example.createCriteria().andNameLike("%test%")
    example.boundBuilder().limit(10).offset(0).build()
  }

  def buildExampleWithoutPagination() {
    UserExample example = new UserExample()
    example.createCriteria().andNameLike("%test%")
    return example
  }

  def generateXml(Plugin plugin) {
    def generator = new SelectByExampleWithoutBLOBsElementGenerator()
    generator.context = mbgContext
    generator.introspectedTable = introspectedTable
    XmlElement root = new XmlElement("mapper")
    generator.addElements(root)
    plugin.sqlMapSelectByExampleWithoutBLOBsElementGenerated(root.elements[0], introspectedTable)
    return root.elements[0]
  }

  def parseSql(Plugin plugin) {
    parseSql(generateXml(plugin), buildExample())
  }

  def parseSqlWithoutPagination(Plugin plugin) {
    parseSql(generateXml(plugin), buildExampleWithoutPagination())
  }

  def "check generated example fields, method and inner class"() {
    setup:
    AbstractPaginationPlugin plugin = Spy()

    when:
    plugin.modelExampleClassGenerated(example, introspectedTable)

    then:
    1 * example.addField { Field field -> field.name == AbstractPaginationPlugin.LIMIT_NAME }
    1 * example.addField { Field field -> field.name == AbstractPaginationPlugin.OFFSET_NAME }
    1 * example.addMethod { Method method -> method.name == AbstractXmbgPlugin.uncapitalize(AbstractPaginationPlugin.BOUND_BUILDER_NAME) }
    1 * example.addInnerClass { InnerClass innerClass -> innerClass.type.shortName == AbstractPaginationPlugin.BOUND_BUILDER_NAME }
  }

  def "check generated xml mapper for mysql"() {
    setup:
    MySqlPaginationPlugin plugin = new MySqlPaginationPlugin();
    formatter = { log.info it; SQLUtils.formatMySql(it) }

    when:
    plugin.sqlMapSelectByExampleWithoutBLOBsElementGenerated(selectByExample, introspectedTable)

    then:
    1 * selectByExample.addElement { formatXml(it.getFormattedContent(0)) == "<if test=\"limit != null and limit>=0 and offset != null\" > limit #{offset} , #{limit} </if>" }

    when:
    print parseSqlWithoutPagination(plugin)

    then:
    systemOutRule.log ==
        """
select id, login_name, name, password, salt
  , roles, register_date
from user
where name like ?
"""

    when:
    systemOutRule.clearLog()
    print parseSql(plugin)

    then:
    systemOutRule.log ==
        """
select id, login_name, name, password, salt
  , roles, register_date
from user
where name like ?
limit ?, ?
"""
  }

  def "check generated xml mapper for postgresql"() {
    setup:
    PostgreSQLPaginationPlugin plugin = new PostgreSQLPaginationPlugin();
    formatter = { log.info it; SQLUtils.formatPGSql(it) }

    when:
    plugin.sqlMapSelectByExampleWithoutBLOBsElementGenerated(selectByExample, introspectedTable)

    then:
    1 * selectByExample.addElement { Element element -> formatXml(element.getFormattedContent(0)) == "<if test=\"limit != null and limit >= 0\" > limit #{limit} </if>" }
    1 * selectByExample.addElement { Element element -> formatXml(element.getFormattedContent(0)) == "<if test=\"offset != null and offset >= 0\" > offset #{offset} </if>" }

    when:
    print parseSqlWithoutPagination(plugin)

    then:
    systemOutRule.log ==
        """
select id, login_name, name, password, salt
  , roles, register_date
from user
where name like ?
"""
    when:
    systemOutRule.clearLog()
    print parseSql(plugin)

    then:
    systemOutRule.log ==
        """
select id, login_name, name, password, salt
  , roles, register_date
from user
where name like ?
limit ? offset ?
"""
  }

  def "check generated xml mapper for oracle"() {
    setup:
    OraclePaginationPlugin plugin = new OraclePaginationPlugin();
    formatter = { log.info it; SQLUtils.formatOracle(it) }

    when:
    plugin.sqlMapSelectByExampleWithoutBLOBsElementGenerated(selectByExample, introspectedTable)

    then:
    1 * selectByExample.addElement(0, { Element element -> formatXml(element.getFormattedContent(0)) == "<if test=\"limit != null and limit>=0 and offset != null\" > select * from ( select tmp_page.*, rownum row_id from ( </if>" })
    1 * selectByExample.addElement { Element element -> formatXml(element.getFormattedContent(0)) == "<if test=\"limit != null and limit>=0 and offset != null\" > <![CDATA[ ) tmp_page where rownum <= #{limit} + #{offset} ) where row_id > #{offset} ]]> </if>" }

    when:
    print parseSqlWithoutPagination(plugin)

    then:
    systemOutRule.log ==
        """
select id, login_name, name, password, salt
  , roles, register_date
from user
where name like ?
"""
    when:
    systemOutRule.clearLog()
    print parseSql(plugin)

    then:
    systemOutRule.log ==
        """
select *
from (
  select tmp_page.*, rownum as row_id
  from (
    select id, login_name, name, password, salt
      , roles, register_date
    from user
    where name like ?
  ) tmp_page
  where rownum <= ? + ?
)
where row_id > ?
"""
  }

  def "check generated xml mapper for sqlserver"() {
    setup:
    SQLServerPaginationPlugin plugin = new SQLServerPaginationPlugin();
    formatter = { log.info it; SQLUtils.formatSQLServer(it) }

    when: "without pagination and order by"
    print parseSqlWithoutPagination(plugin)

    then:
    systemOutRule.log ==
        """
select id, login_name, name, password, salt
  , roles, register_date
from user
where name like ?
"""

    when: "with pagination and without order by"
    systemOutRule.clearLog()
    print parseSql(plugin)

    then:
    systemOutRule.log ==
        """
select id, login_name, name, password, salt
  , roles, register_date
from (select id, login_name, name, password, salt
    , roles, register_date, row_number() over (order by id) as row_num
  from user
  where name like ?
  ) tmp
where tmp.row_num between ? and ? + ?
order by row_num
"""

    when: "without pagination and with order by"
    systemOutRule.clearLog()
    UserExample example = buildExampleWithoutPagination()
    example.setOrderByClause("id asc")
    print parseSql(generateXml(plugin), example)

    then:
    systemOutRule.log ==
        """
select id, login_name, name, password, salt
  , roles, register_date
from user
where name like ?
order by id asc
"""

    when: "with pagination and with order by"
    systemOutRule.clearLog()
    example = buildExample()
    example.setOrderByClause("id asc")
    print parseSql(generateXml(plugin), example)

    then:
    systemOutRule.log ==
        """
select id, login_name, name, password, salt
  , roles, register_date
from (select id, login_name, name, password, salt
    , roles, register_date, row_number() over (order by id asc) as row_num
  from user
  where name like ?
  ) tmp
where tmp.row_num between ? and ? + ?
order by row_num
"""
  }

  def "check generated xml mapper for hsqldb"() {
    setup:
    HsqldbPaginationPlugin plugin = new HsqldbPaginationPlugin();
    formatter = { log.info it; SQLUtils.format(it, JdbcUtils.H2) }

    when:
    plugin.sqlMapSelectByExampleWithoutBLOBsElementGenerated(selectByExample, introspectedTable)

    then:
    1 * selectByExample.addElement { Element element -> formatXml(element.getFormattedContent(0)) == "<if test=\"limit != null and limit>=0 and offset != null\" > limit #{limit} offset #{offset} </if>" }

    when:
    print parseSqlWithoutPagination(plugin)

    then:
    systemOutRule.log ==
        """
select id, login_name, name, password, salt
  , roles, register_date
from user
where name like ?
"""

    when:
    systemOutRule.clearLog()
    print parseSql(plugin)

    then:
    systemOutRule.log ==
        """
select id, login_name, name, password, salt
  , roles, register_date
from user
where name like ?
limit ?, ?
"""
  }

  def "check generated xml mapper for db2"() {
    setup:
    DB2PaginationPlugin plugin = new DB2PaginationPlugin();
    formatter = { log.info it; SQLUtils.format(it, JdbcUtils.DB2) }

    when:
    plugin.sqlMapSelectByExampleWithoutBLOBsElementGenerated(selectByExample, introspectedTable)

    then:
    1 * selectByExample.addElement(0, { Element element -> formatXml(element.getFormattedContent(0)) == "<if test=\"limit != null and limit>=0 and offset != null\" > select * from (select tmp_page.*, rownumber() over() as row_id from ( </if>" })
    1 * selectByExample.addElement { Element element -> formatXml(element.getFormattedContent(0)) == "<if test=\"limit != null and limit>=0 and offset != null\" > ) as tmp_page ) where row_id between #{offset} and #{offset} + #{limit} </if>" }

    when:
    print parseSqlWithoutPagination(plugin)

    then:
    systemOutRule.log ==
        """
select id, login_name, name, password, salt
  , roles, register_date
from user
where name like ?
"""

    when:
    systemOutRule.clearLog()
    print parseSql(plugin)

    then:
    systemOutRule.log ==
        """
select *
from (select tmp_page.*, rownumber() over () as row_id
  from (select id, login_name, name, password, salt
      , roles, register_date
    from user
    where name like ?
    ) tmp_page
  )
where row_id between ? and ? + ?
"""

  }

}
