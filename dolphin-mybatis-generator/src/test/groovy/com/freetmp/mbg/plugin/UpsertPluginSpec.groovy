package com.freetmp.mbg.plugin

import com.freetmp.mbg.plugin.upsert.*
import groovy.util.logging.Slf4j
import org.mybatis.generator.api.dom.java.Method
import org.mybatis.generator.api.dom.xml.Element

/**
 * Created by LiuPin on 2015/5/20.
 */
@Slf4j
class UpsertPluginSpec extends AbstractPluginSpec {

  def "check generated method signature"() {
    setup:
    AbstractUpsertPlugin plugin = Spy()

    when:
    plugin.clientGenerated(mapper, mapperImpl, introspectedTable)

    then:
    1 * mapper.addMethod { Method method -> method.getFormattedContent(0, true) == "int upsert(@Param(\"record\") User record, @Param(\"array\") String[] array);" }
    1 * mapper.addMethod { Method method -> method.getFormattedContent(0, true) == "int upsertSelective(@Param(\"record\") User record, @Param(\"array\") String[] array);" }
    1 * mapper.addMethod { Method method -> method.getFormattedContent(0, true) == "int batchUpsert(@Param(\"records\") List<User> list, @Param(\"array\") String[] array);" }
    1 * mapper.addMethod { Method method -> method.getFormattedContent(0, true) == "int batchUpsertSelective(@Param(\"records\") List<User> list, @Param(\"array\") String[] array);" }
    1 * mapper.addImportedTypes({ it.size() >= 3 })
  }

  def "check generated upsert series xml for mysql"() {
    setup:
    MySqlUpsertPlugin plugin = new MySqlUpsertPlugin();

    when:
    plugin.sqlMapDocumentGenerated(document, introspectedTable)

    then:
    _ * root.addElement(_) >> { Element element -> println element.getFormattedContent(0)}

  }

  def "check generated upsert series xml for oracle"() {
    setup:
    OracleUpsertPlugin plugin = new OracleUpsertPlugin();

    when:
    plugin.sqlMapDocumentGenerated(document, introspectedTable)

    then:
    _ * root.addElement(_) >> { Element element -> println element.getFormattedContent(0) }
  }

  def "check generated upsert series xml for postgresql"() {
    setup:
    PostgreSQLUpsertPlugin plugin = new PostgreSQLUpsertPlugin();

    when:
    plugin.sqlMapDocumentGenerated(document, introspectedTable)

    then:
    _ * root.addElement(_) >> { Element element -> println element.getFormattedContent(0) }
  }

  def "check generated upsert series xml for sqlserver"() {
    setup:
    SQLServerUpsertPlugin plugin = new SQLServerUpsertPlugin();

    when:
    plugin.sqlMapDocumentGenerated(document, introspectedTable)

    then:
    _ * root.addElement(_) >> { Element element -> println element.getFormattedContent(0) }
  }

  def "check generated upsert series xml for hsqldb"() {
    setup:
    HsqldbUpsertPlugin plugin = new HsqldbUpsertPlugin();

    when:
    plugin.sqlMapDocumentGenerated(document, introspectedTable)

    then:
    _ * root.addElement(_) >> { Element element -> println element.getFormattedContent(0) }
  }

  def "check generated upsert series xml for db2"() {
    setup:
    DB2UpsertPlugin plugin = new DB2UpsertPlugin();

    when:
    plugin.sqlMapDocumentGenerated(document, introspectedTable)

    then:
    _ * root.addElement(_) >> { Element element -> println element.getFormattedContent(0) }
  }

}
