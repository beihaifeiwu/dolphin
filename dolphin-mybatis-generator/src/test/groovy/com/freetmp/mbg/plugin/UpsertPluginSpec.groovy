package com.freetmp.mbg.plugin

import com.freetmp.mbg.plugin.upsert.*
import org.mybatis.generator.api.IntrospectedTable
import org.mybatis.generator.api.dom.java.Interface
import org.mybatis.generator.api.dom.java.Method
import org.mybatis.generator.api.dom.java.TopLevelClass
import org.mybatis.generator.api.dom.xml.Document
import org.mybatis.generator.api.dom.xml.Element
import org.mybatis.generator.api.dom.xml.XmlElement

/**
 * Created by LiuPin on 2015/5/20.
 */
class UpsertPluginSpec extends AbstractPluginSpec {

  def "check generated method signature"() {
    setup:
    AbstractUpsertPlugin plugin = new AbstractUpsertPlugin() {
      @Override
      protected void generateSqlMapContent(IntrospectedTable introspectedTable, XmlElement parent) {}

      @Override
      protected void generateSqlMapContentSelective(IntrospectedTable introspectedTable, XmlElement parent) {}
    }

    when:
    plugin.clientGenerated(interfaze, topLevelClass, introspectedTable)

    then:
    with(interfaze){
      1 * addMethod { Method method -> method.getFormattedContent(0, true) == "int upsert(@Param(\"record\") User record, @Param(\"array\") String[] array);" }
      1 * addMethod { Method method -> method.getFormattedContent(0, true) == "int upsertSelective(@Param(\"record\") User record, @Param(\"array\") String[] array);" }
      1 * addMethod { Method method -> method.getFormattedContent(0, true) == "int batchUpsert(@Param(\"records\") List<User> list, @Param(\"array\") String[] array);" }
      1 * addMethod { Method method -> method.getFormattedContent(0, true) == "int batchUpsertSelective(@Param(\"records\") List<User> list, @Param(\"array\") String[] array);" }
      1 * addImportedTypes({ it.size() >= 3 })
    }
  }

  def "check generated upsert series xml for mysql"() {
    setup:
    MySqlUpsertPlugin plugin = new MySqlUpsertPlugin();

    when:
    plugin.sqlMapDocumentGenerated(document, introspectedTable)

    then:
    _ * root.addElement(_) >> { Element element -> println element.getFormattedContent(0) }
  }

  def "check generated upsert series xml for oracle"() {
    setup:
    OracleUpsertPlugin plugin = new OracleUpsertPlugin();
    Document document = Spy()
    XmlElement root = Spy(XmlElement, constructorArgs: ["mapper"])

    when:
    plugin.sqlMapDocumentGenerated(document, introspectedTable)

    then:
    _ * document.rootElement >> root
    _ * root.addElement(_) >> { Element element -> println element.getFormattedContent(0) }
  }

  def "check generated upsert series xml for postgresql"() {
    setup:
    PostgreSQLUpsertPlugin plugin = new PostgreSQLUpsertPlugin();
    Document document = Spy()
    XmlElement root = Spy(XmlElement, constructorArgs: ["mapper"])

    when:
    plugin.sqlMapDocumentGenerated(document, introspectedTable)

    then:
    _ * document.rootElement >> root
    _ * root.addElement(_) >> { Element element -> println element.getFormattedContent(0) }
  }

  def "check generated upsert series xml for sqlserver"() {
    setup:
    SQLServerUpsertPlugin plugin = new SQLServerUpsertPlugin();
    Document document = Spy()
    XmlElement root = Spy(XmlElement, constructorArgs: ["mapper"])

    when:
    plugin.sqlMapDocumentGenerated(document, introspectedTable)

    then:
    _ * document.rootElement >> root
    _ * root.addElement(_) >> { Element element -> println element.getFormattedContent(0) }
  }

  def "check generated upsert series xml for hsqldb"() {
    setup:
    HsqldbUpsertPlugin plugin = new HsqldbUpsertPlugin();
    Document document = Spy()
    XmlElement root = Spy(XmlElement, constructorArgs: ["mapper"])

    when:
    plugin.sqlMapDocumentGenerated(document, introspectedTable)

    then:
    _ * document.rootElement >> root
    _ * root.addElement(_) >> { Element element -> println element.getFormattedContent(0) }
  }

  def "check generated upsert series xml for db2"() {
    setup:
    DB2UpsertPlugin plugin = new DB2UpsertPlugin();
    Document document = Spy()
    XmlElement root = Spy(XmlElement, constructorArgs: ["mapper"])

    when:
    plugin.sqlMapDocumentGenerated(document, introspectedTable)

    then:
    _ * document.rootElement >> root
    _ * root.addElement(_) >> { Element element -> println element.getFormattedContent(0) }
  }

}
