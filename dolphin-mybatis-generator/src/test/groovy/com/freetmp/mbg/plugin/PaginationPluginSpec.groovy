package com.freetmp.mbg.plugin
import com.freetmp.mbg.plugin.page.*
import groovy.util.logging.Slf4j
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

    when:
    plugin.sqlMapSelectByExampleWithoutBLOBsElementGenerated(selectByExample, introspectedTable)

    then:
    1 * selectByExample.addElement {Element element ->log.info element.getFormattedContent(0);element != null}
  }

  def "check generated xml mapper for postgresql"(){
    setup:
    PostgreSQLPaginationPlugin plugin = new PostgreSQLPaginationPlugin();

    when:
    plugin.sqlMapSelectByExampleWithoutBLOBsElementGenerated(selectByExample, introspectedTable)

    then:
    1 * selectByExample.addElement {Element element ->log.info element.getFormattedContent(0);element != null}
    1 * selectByExample.addElement {Element element ->log.info element.getFormattedContent(0);element != null}
  }

  def "check generated xml mapper for oracle"(){
    setup:
    OraclePaginationPlugin plugin = new OraclePaginationPlugin();

    when:
    plugin.sqlMapSelectByExampleWithoutBLOBsElementGenerated(selectByExample, introspectedTable)

    then:
    1 * selectByExample.addElement {Element element ->log.info element.getFormattedContent(0);element != null}
  }

  def "check generated xml mapper for sqlserver"(){
    setup:
    SQLServerPaginationPlugin plugin = new SQLServerPaginationPlugin();
    SelectByExampleWithoutBLOBsElementGenerator generator = new SelectByExampleWithoutBLOBsElementGenerator();
    generator.context = mbgContext
    generator.introspectedTable = introspectedTable



    when:
    plugin.sqlMapSelectByExampleWithoutBLOBsElementGenerated(selectByExample, introspectedTable)

    then:
    1 * selectByExample.addElement {Element element ->log.info element.getFormattedContent(0);element != null}
    1 * selectByExample.addElement {int index, Element element -> log.info element.getFormattedContent(0);element != null}
  }

  def "check generated xml mapper for hsqldb"(){
    setup:
    HsqldbPaginationPlugin plugin = new HsqldbPaginationPlugin();

    when:
    plugin.sqlMapSelectByExampleWithoutBLOBsElementGenerated(selectByExample, introspectedTable)

    then:
    1 * selectByExample.addElement {Element element ->log.info element.getFormattedContent(0);element != null}
  }

  def "check generated xml mapper for db2"(){
    setup:
    DB2PaginationPlugin plugin = new DB2PaginationPlugin();

    when:
    plugin.sqlMapSelectByExampleWithoutBLOBsElementGenerated(selectByExample, introspectedTable)

    then:
    1 * selectByExample.addElement {Element element ->log.info element.getFormattedContent(0);element != null}
  }

}
