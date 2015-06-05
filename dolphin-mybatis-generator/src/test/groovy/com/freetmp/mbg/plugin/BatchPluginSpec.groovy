package com.freetmp.mbg.plugin

import com.alibaba.druid.sql.SQLUtils
import com.freetmp.mbg.formatter.BasicFormatterImpl
import com.freetmp.mbg.plugin.batch.BatchInsertPlugin
import com.freetmp.mbg.plugin.batch.BatchUpdatePlugin
import groovy.util.logging.Slf4j
import org.mybatis.generator.api.dom.java.Method
import org.mybatis.generator.api.dom.xml.XmlElement

/**
 * Created by LiuPin on 2015/5/21.
 */
@Slf4j
class BatchPluginSpec extends AbstractPluginSpec {

  def buildParameter() {
    [list: [
        [id: 1, loginName: "admin", name: "Admin", password: "12345678", salt: "123", roles: "admin", registerDate: new Date()] as User,
        [id: 2, loginName: "user", name: "User", password: "12345678", salt: "123", roles: "user", registerDate: new Date()] as User
    ]]
  }

  def "check generated client interface and mapper xml for batch update"() {
    setup:
    BatchUpdatePlugin plugin = new BatchUpdatePlugin()
    XmlElement element

    when:
    plugin.clientGenerated(mapper, mapperImpl, introspectedTable)

    then:
    1 * mapper.addMethod { Method method -> method.getFormattedContent(0, true) == "int batchUpdate(List<User> list);" }
    1 * mapper.addImportedTypes({ it.size() >= 1 })

    when:
    plugin.sqlMapDocumentGenerated(document, introspectedTable)

    then:
    1 * root.addElement({ isXmlElementWithIdEquals(it, BatchUpdatePlugin.BATCH_UPDATE) }) >> { element = it }

    when:
    print parseSql(element, buildParameter())
    log.info systemOutRule.log
    then:
    systemOutRule.log ==
        """
update user
set login_name = ?, name = ?, password = ?, salt = ?, roles = ?, register_date = ?
where id = ?;
update user
set login_name = ?, name = ?, password = ?, salt = ?, roles = ?, register_date = ?
where id = ?
"""
  }

  def "check generated client interface and mapper xml for batch insert"() {
    setup:
    BatchInsertPlugin plugin = new BatchInsertPlugin()
    XmlElement element
    formatter = new BasicFormatterImpl()

    when:
    plugin.clientGenerated(mapper, mapperImpl, introspectedTable)

    then:
    1 * mapper.addMethod { Method method -> method.getFormattedContent(0, true) == "int batchInsert(List<User> list);" }

    when:
    plugin.sqlMapDocumentGenerated(document, introspectedTable)

    then:
    1 * root.addElement({ isXmlElementWithIdEquals(it, BatchInsertPlugin.BATCH_INSERT) }) >> { element = it }

    when:
    print parseSql(element, buildParameter())
    log.info systemOutRule.log
    then:
    systemOutRule.log ==
"""
    insert
    into
        user
        ( id, login_name, name, password, salt, roles, register_date )
    values
        ( ?, ?, ?, ?, ?, ?, ? ) , (
            ?, ?, ?, ?, ?, ?, ?
        )
"""
  }
}
