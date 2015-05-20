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
class UpsertGenerateSpec extends AbstractPluginSpec {

  def "check generated method signature"() {
    setup:
    AbstractUpsertPlugin plugin = new AbstractUpsertPlugin() {
      @Override
      protected void generateSqlMapContent(IntrospectedTable introspectedTable, XmlElement parent) {}

      @Override
      protected void generateSqlMapContentSelective(IntrospectedTable introspectedTable, XmlElement parent) {}
    }
    Interface interfaze = Spy(Interface, constructorArgs: [User.class.canonicalName + "Mapper"])
    TopLevelClass topLevelClass = Mock()

    when:
    plugin.clientGenerated(interfaze, topLevelClass, introspectedTable)

    then:
    4 * interfaze.addMethod(_) >>
        { Method method -> method.getFormattedContent(0, true) == "int upsert(@Param(\"record\") User record, @Param(\"array\") String[] array);" } >>
        { Method method -> method.getFormattedContent(0, true) == "int upsertSelective(@Param(\"record\") User record, @Param(\"array\") String[] array);" } >>
        { Method method -> method.getFormattedContent(0, true) == "int batchUpsert(@Param(\"records\") List<User> list, @Param(\"array\") String[] array);" } >>
        { Method method -> method.getFormattedContent(0, true) == "int batchUpsertSelective(@Param(\"records\") List<User> list, @Param(\"array\") String[] array);" }
    1 * interfaze.addImportedTypes({ it.size() >= 3 })
    1 * introspectedTable.rules >> rules
    1 * introspectedTable.tableConfiguration >> tableConfiguration
  }

  def "check generated upsert series xml for mysql"() {
    setup:
    MySqlUpsertPlugin plugin = new MySqlUpsertPlugin();
    Document document = Spy()
    XmlElement root = Spy(XmlElement, constructorArgs: ["mapper"])

    when:
    plugin.sqlMapDocumentGenerated(document, introspectedTable)

    then:
    _ * introspectedTable.getAllColumns() >> introspectedColumns
    _ * introspectedTable.aliasedFullyQualifiedTableNameAtRuntime >> "user"
    _ * document.rootElement >> root
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
    _ * introspectedTable.getAllColumns() >> introspectedColumns
    _ * introspectedTable.aliasedFullyQualifiedTableNameAtRuntime >> "user"
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
    _ * introspectedTable.getAllColumns() >> introspectedColumns
    _ * introspectedTable.aliasedFullyQualifiedTableNameAtRuntime >> "user"
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
    _ * introspectedTable.getAllColumns() >> introspectedColumns
    _ * introspectedTable.aliasedFullyQualifiedTableNameAtRuntime >> "user"
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
    _ * introspectedTable.getAllColumns() >> introspectedColumns
    _ * introspectedTable.aliasedFullyQualifiedTableNameAtRuntime >> "user"
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
    _ * introspectedTable.getAllColumns() >> introspectedColumns
    _ * introspectedTable.aliasedFullyQualifiedTableNameAtRuntime >> "user"
    _ * document.rootElement >> root
    _ * root.addElement(_) >> { Element element -> println element.getFormattedContent(0) }
  }

}
