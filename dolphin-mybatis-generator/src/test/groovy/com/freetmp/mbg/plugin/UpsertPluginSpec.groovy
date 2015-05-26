package com.freetmp.mbg.plugin
import com.freetmp.mbg.plugin.upsert.*
import groovy.util.logging.Slf4j
import org.mybatis.generator.api.IntrospectedTable
import org.mybatis.generator.api.dom.java.Method
import org.mybatis.generator.api.dom.xml.XmlElement

import static com.freetmp.mbg.plugin.upsert.AbstractUpsertPlugin.*
/**
 * Created by LiuPin on 2015/5/20.
 */
@Slf4j
class UpsertPluginSpec extends AbstractPluginSpec {

  Map<String, XmlElement> generatedXmlElements = [:]

  def setup() {
    1 * root.addElement({ XmlElement element -> element.name == "sql" && isXmlElementWithIdEquals(element, IDENTIFIERS_ARRAY_CONDITIONS) }) >> { generatedXmlElements.put(IDENTIFIERS_ARRAY_CONDITIONS, it) }
    1 * root.addElement({ XmlElement element -> element.name == "update" && isXmlElementWithIdEquals(element, UPSERT) }) >> { generatedXmlElements.put(UPSERT, it) }
    1 * root.addElement({ XmlElement element -> element.name == "update" && isXmlElementWithIdEquals(element, UPSERT_SELECTIVE) }) >> { generatedXmlElements.put(UPSERT_SELECTIVE, it) }
    1 * root.addElement({ XmlElement element -> element.name == "update" && isXmlElementWithIdEquals(element, BATCH_UPSERT) }) >> { generatedXmlElements.put(BATCH_UPSERT, it) }
    1 * root.addElement({ XmlElement element -> element.name == "update" && isXmlElementWithIdEquals(element, BATCH_UPSERT_SELECTIVE) }) >> { generatedXmlElements.put(BATCH_UPSERT_SELECTIVE, it) }
  }

  def buildParameterForSingle() {
    [
        record: [id: 1, name: "Admin", password: "12345678", salt: "123", roles: "admin", registerDate: new Date()] as User,
        array : ["id", "name"]
    ]
  }

  def buildParameterForBatch() {
    [
        records: [
            [id: 1, name: "Admin", password: "12345678", salt: "123", roles: "admin", registerDate: new Date()] as User,
            [id: 2, loginName: "user", name: "User", password: "12345678", salt: "123", registerDate: new Date()] as User
        ],
        array  : ["id", "name"]
    ]
  }

  def parseSqlForUpsert() {
    XmlElement sql = generatedXmlElements.get(IDENTIFIERS_ARRAY_CONDITIONS)
    XmlElement upsert = generatedXmlElements.get(UPSERT)
    Object parameter = buildParameterForSingle()
    parseSql(upsert, parameter, sql)
  }

  def parseSqlForUpsertSelective() {
    XmlElement sql = generatedXmlElements.get(IDENTIFIERS_ARRAY_CONDITIONS)
    XmlElement upsertSelective = generatedXmlElements.get(UPSERT_SELECTIVE)
    Object parameter = buildParameterForSingle()
    parseSql upsertSelective, parameter, sql
  }

  def parseSqlForBatchUpsert() {
    XmlElement sql = generatedXmlElements.get(IDENTIFIERS_ARRAY_CONDITIONS)
    XmlElement batchUpsert = generatedXmlElements.get(BATCH_UPSERT)
    Object parameter = buildParameterForBatch()
    parseSql batchUpsert, parameter, sql
  }

  def parseSqlForBatchUpsertSelective() {
    XmlElement sql = generatedXmlElements.get(IDENTIFIERS_ARRAY_CONDITIONS)
    XmlElement batchUpsertSelective = generatedXmlElements.get(BATCH_UPSERT_SELECTIVE)
    Object parameter = buildParameterForBatch()
    parseSql batchUpsertSelective, parameter, sql
  }

  def "check generated method signature"() {
    setup:
    AbstractUpsertPlugin plugin = Spy()
    XmlElement xe = new XmlElement("placeholder")
    plugin.generateSqlMapContent(_, _) >> { IntrospectedTable table, XmlElement element -> xe }
    plugin.generateSqlMapContentSelective(_, _) >> { IntrospectedTable table, XmlElement element -> xe }

    when:
    plugin.clientGenerated(mapper, mapperImpl, introspectedTable)
    plugin.sqlMapDocumentGenerated(document, introspectedTable)

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
    print parseSqlForUpsert()
    log.info systemOutRule.log

    then:
    systemOutRule.log.trim() == "insert into user ( id, login_name, name, password, salt, roles, register_date ) values ( ?, ?, ?, ?, ?, ?, ? ) " +
        "on duplicate key update id = ?, login_name = ?, name = ?, password = ?, salt = ?, roles = ?, register_date = ?"

    when:
    systemOutRule.clearLog()
    print parseSqlForUpsertSelective()
    log.info systemOutRule.log

    then:
    systemOutRule.log.trim() == "insert into user ( id, name, password, salt, roles, register_date ) values ( ?, ?, ?, ?, ?, ? ) " +
        "on duplicate key update id = ?, name = ?, password = ?, salt = ?, roles = ?, register_date = ?"

    when:
    systemOutRule.clearLog()
    print parseSqlForBatchUpsert()
    log.info systemOutRule.log

    then:
    systemOutRule.log.trim() == "insert into user ( id, login_name, name, password, salt, roles, register_date ) values ( ?, ?, ?, ?, ?, ?, ? ) on duplicate key update id = ?, login_name = ?, name = ?, password = ?, salt = ?, roles = ?, register_date = ? ; " +
        "insert into user ( id, login_name, name, password, salt, roles, register_date ) values ( ?, ?, ?, ?, ?, ?, ? ) on duplicate key update id = ?, login_name = ?, name = ?, password = ?, salt = ?, roles = ?, register_date = ?"

    when:
    systemOutRule.clearLog()
    print parseSqlForBatchUpsertSelective()
    log.info systemOutRule.log

    then:
    systemOutRule.log.trim() == "insert into user ( id, name, password, salt, roles, register_date ) values ( ?, ?, ?, ?, ?, ? ) on duplicate key update id = ?, name = ?, password = ?, salt = ?, roles = ?, register_date = ? ; " +
        "insert into user ( id, login_name, name, password, salt, register_date ) values ( ?, ?, ?, ?, ?, ? ) on duplicate key update id = ?, login_name = ?, name = ?, password = ?, salt = ?, register_date = ?"
  }

  def "check generated upsert series xml for oracle"() {
    setup:
    OracleUpsertPlugin plugin = new OracleUpsertPlugin();

    when:
    plugin.sqlMapDocumentGenerated(document, introspectedTable)
    print parseSqlForUpsert()
    log.info systemOutRule.log

    then:
    systemOutRule.log.trim() == "merge into user using dual on ( id = ? and name = ? ) " +
        "when matched then update set id = ?, login_name = ?, name = ?, password = ?, salt = ?, roles = ?, register_date = ? " +
        "when not matched then insert ( id, login_name, name, password, salt, roles, register_date ) values ( ?, ?, ?, ?, ?, ?, ? )"

    when:
    systemOutRule.clearLog()
    print parseSqlForUpsertSelective()
    log.info systemOutRule.log

    then:
    systemOutRule.log.trim() == "merge into user using dual on ( id = ? and name = ? ) " +
        "when matched then update set id = ?, name = ?, password = ?, salt = ?, roles = ?, register_date = ? " +
        "when not matched then insert ( id, name, password, salt, roles, register_date ) values ( ?, ?, ?, ?, ?, ? )"

    when:
    systemOutRule.clearLog()
    print parseSqlForBatchUpsert()
    log.info systemOutRule.log

    then:
    systemOutRule.log.trim() == "merge into user using dual on ( id = ? and name = ? ) " +
        "when matched then update set id = ?, login_name = ?, name = ?, password = ?, salt = ?, roles = ?, register_date = ? " +
        "when not matched then insert ( id, login_name, name, password, salt, roles, register_date ) values ( ?, ?, ?, ?, ?, ?, ? ) ; " +
        "merge into user using dual on ( id = ? and name = ? ) " +
        "when matched then update set id = ?, login_name = ?, name = ?, password = ?, salt = ?, roles = ?, register_date = ? " +
        "when not matched then insert ( id, login_name, name, password, salt, roles, register_date ) values ( ?, ?, ?, ?, ?, ?, ? )"

    when:
    systemOutRule.clearLog()
    print parseSqlForBatchUpsertSelective()
    log.info systemOutRule.log

    then:
    systemOutRule.log.trim() == "merge into user using dual on ( id = ? and name = ? ) " +
        "when matched then update set id = ?, name = ?, password = ?, salt = ?, roles = ?, register_date = ? " +
        "when not matched then insert ( id, name, password, salt, roles, register_date ) values ( ?, ?, ?, ?, ?, ? ) ; " +
        "merge into user using dual on ( id = ? and name = ? ) " +
        "when matched then update set id = ?, login_name = ?, name = ?, password = ?, salt = ?, register_date = ? " +
        "when not matched then insert ( id, login_name, name, password, salt, register_date ) values ( ?, ?, ?, ?, ?, ? )"
  }

  def "check generated upsert series xml for postgresql"() {
    setup:
    PostgreSQLUpsertPlugin plugin = new PostgreSQLUpsertPlugin();

    when:
    plugin.sqlMapDocumentGenerated(document, introspectedTable)
    print parseSqlForUpsert()
    log.info systemOutRule.log

    then:
    systemOutRule.log.trim() == "with upsert as ( update user set id = ?, login_name = ?, name = ?, password = ?, salt = ?, roles = ?, register_date = ? where id = ? and name = ? returning * ) " +
        "insert into user ( id, login_name, name, password, salt, roles, register_date ) select ?, ?, ?, ?, ?, ?, ? where not exists ( select * from upsert )"

    when:
    systemOutRule.clearLog()
    print parseSqlForUpsertSelective()
    log.info systemOutRule.log

    then:
    systemOutRule.log.trim() == "with upsert as ( update user set id = ?, name = ?, password = ?, salt = ?, roles = ?, register_date = ? where id = ? and name = ? returning * ) " +
        "insert into user ( id, name, password, salt, roles, register_date ) select ?, ?, ?, ?, ?, ? where not exists ( select * from upsert )"

    when:
    systemOutRule.clearLog()
    print parseSqlForBatchUpsert()
    log.info systemOutRule.log

    then:
    systemOutRule.log.trim() == "with upsert as ( update user set id = ?, login_name = ?, name = ?, password = ?, salt = ?, roles = ?, register_date = ? where id = ? and name = ? returning * ) " +
        "insert into user ( id, login_name, name, password, salt, roles, register_date ) select ?, ?, ?, ?, ?, ?, ? where not exists ( select * from upsert ) ; " +
        "with upsert as ( update user set id = ?, login_name = ?, name = ?, password = ?, salt = ?, roles = ?, register_date = ? where id = ? and name = ? returning * ) " +
        "insert into user ( id, login_name, name, password, salt, roles, register_date ) select ?, ?, ?, ?, ?, ?, ? where not exists ( select * from upsert )"

    when:
    systemOutRule.clearLog()
    print parseSqlForBatchUpsertSelective()
    log.info systemOutRule.log

    then:
    systemOutRule.log.trim() == "with upsert as ( update user set id = ?, name = ?, password = ?, salt = ?, roles = ?, register_date = ? where id = ? and name = ? returning * ) " +
        "insert into user ( id, name, password, salt, roles, register_date ) select ?, ?, ?, ?, ?, ? where not exists ( select * from upsert ) ; " +
        "with upsert as ( update user set id = ?, login_name = ?, name = ?, password = ?, salt = ?, register_date = ? where id = ? and name = ? returning * ) " +
        "insert into user ( id, login_name, name, password, salt, register_date ) select ?, ?, ?, ?, ?, ? where not exists ( select * from upsert )"
  }

  def "check generated upsert series xml for sqlserver"() {
    setup:
    SQLServerUpsertPlugin plugin = new SQLServerUpsertPlugin();

    when:
    plugin.sqlMapDocumentGenerated(document, introspectedTable)
    print parseSqlForUpsert()
    log.info systemOutRule.log

    then:
    systemOutRule.log.trim() == "update user set id = ?, login_name = ?, name = ?, password = ?, salt = ?, roles = ?, register_date = ? where id = ? and name = ? " +
        "if @@rowcount = 0 insert into user ( id, login_name, name, password, salt, roles, register_date ) values ( ?, ?, ?, ?, ?, ?, ? )"

    when:
    systemOutRule.clearLog()
    print parseSqlForUpsertSelective()
    log.info systemOutRule.log

    then:
    systemOutRule.log.trim() == "update user set id = ?, name = ?, password = ?, salt = ?, roles = ?, register_date = ? where id = ? and name = ? " +
        "if @@rowcount = 0 insert into user ( id, name, password, salt, roles, register_date ) values ( ?, ?, ?, ?, ?, ? )"

    when:
    systemOutRule.clearLog()
    print parseSqlForBatchUpsert()
    log.info systemOutRule.log

    then:
    systemOutRule.log.trim() == "update user set id = ?, login_name = ?, name = ?, password = ?, salt = ?, roles = ?, register_date = ? where id = ? and name = ? " +
        "if @@rowcount = 0 insert into user ( id, login_name, name, password, salt, roles, register_date ) values ( ?, ?, ?, ?, ?, ?, ? ) ; " +
        "update user set id = ?, login_name = ?, name = ?, password = ?, salt = ?, roles = ?, register_date = ? where id = ? and name = ? " +
        "if @@rowcount = 0 insert into user ( id, login_name, name, password, salt, roles, register_date ) values ( ?, ?, ?, ?, ?, ?, ? )"

    when:
    systemOutRule.clearLog()
    print parseSqlForBatchUpsertSelective()
    log.info systemOutRule.log

    then:
    systemOutRule.log.trim() == "update user set id = ?, name = ?, password = ?, salt = ?, roles = ?, register_date = ? where id = ? and name = ? " +
        "if @@rowcount = 0 insert into user ( id, name, password, salt, roles, register_date ) values ( ?, ?, ?, ?, ?, ? ) ; " +
        "update user set id = ?, login_name = ?, name = ?, password = ?, salt = ?, register_date = ? where id = ? and name = ? " +
        "if @@rowcount = 0 insert into user ( id, login_name, name, password, salt, register_date ) values ( ?, ?, ?, ?, ?, ? )"
  }

  def "check generated upsert series xml for hsqldb"() {
    setup:
    HsqldbUpsertPlugin plugin = new HsqldbUpsertPlugin();

    when:
    plugin.sqlMapDocumentGenerated(document, introspectedTable)
    print parseSqlForUpsert()
    log.info systemOutRule.log

    then:
    systemOutRule.log.trim() == "merge into user using (values ?, ?, ?, ?, ?, ?, ? ) temp ( id, login_name, name, password, salt, roles, register_date ) on ( user.id = temp.id and user.name = temp.name ) " +
        "when matched then update set userid = temp.id, userlogin_name = temp.login_name, username = temp.name, userpassword = temp.password, usersalt = temp.salt, userroles = temp.roles, userregister_date = temp.register_date " +
        "when not matched then insert ( id, login_name, name, password, salt, roles, register_date ) values ( temp.id, temp.login_name, temp.name, temp.password, temp.salt, temp.roles, temp.register_date )"

    when:
    systemOutRule.clearLog()
    print parseSqlForUpsertSelective()
    log.info systemOutRule.log

    then:
    systemOutRule.log.trim() == "merge into user using (values ?, ?, ?, ?, ?, ? ) temp ( id, name, password, salt, roles, register_date ) on ( user.id = temp.id and user.name = temp.name ) " +
        "when matched then update set userid = temp.id, username = temp.name, userpassword = temp.password, usersalt = temp.salt, userroles = temp.roles, userregister_date = temp.register_date " +
        "when not matched then insert ( id, name, password, salt, roles, register_date ) values ( temp.id, temp.name, temp.password, temp.salt, temp.roles, temp.register_date )"

    when:
    systemOutRule.clearLog()
    print parseSqlForBatchUpsert()
    log.info systemOutRule.log

    then:
    systemOutRule.log.trim() == "merge into user using (values ?, ?, ?, ?, ?, ?, ? ) temp ( id, login_name, name, password, salt, roles, register_date ) on ( user.id = temp.id and user.name = temp.name ) " +
        "when matched then update set userid = temp.id, userlogin_name = temp.login_name, username = temp.name, userpassword = temp.password, usersalt = temp.salt, userroles = temp.roles, userregister_date = temp.register_date " +
        "when not matched then insert ( id, login_name, name, password, salt, roles, register_date ) values ( temp.id, temp.login_name, temp.name, temp.password, temp.salt, temp.roles, temp.register_date ) ; " +
        "merge into user using (values ?, ?, ?, ?, ?, ?, ? ) temp ( id, login_name, name, password, salt, roles, register_date ) on ( user.id = temp.id and user.name = temp.name ) " +
        "when matched then update set userid = temp.id, userlogin_name = temp.login_name, username = temp.name, userpassword = temp.password, usersalt = temp.salt, userroles = temp.roles, userregister_date = temp.register_date " +
        "when not matched then insert ( id, login_name, name, password, salt, roles, register_date ) values ( temp.id, temp.login_name, temp.name, temp.password, temp.salt, temp.roles, temp.register_date )"

    when:
    systemOutRule.clearLog()
    print parseSqlForBatchUpsertSelective()
    log.info systemOutRule.log

    then:
    systemOutRule.log.trim() == "merge into user using (values ?, ?, ?, ?, ?, ? ) temp ( id, name, password, salt, roles, register_date ) on ( user.id = temp.id and user.name = temp.name ) " +
        "when matched then update set userid = temp.id, username = temp.name, userpassword = temp.password, usersalt = temp.salt, userroles = temp.roles, userregister_date = temp.register_date " +
        "when not matched then insert ( id, name, password, salt, roles, register_date ) values ( temp.id, temp.name, temp.password, temp.salt, temp.roles, temp.register_date ) ; " +
        "merge into user using (values ?, ?, ?, ?, ?, ? ) temp ( id, login_name, name, password, salt, register_date ) on ( user.id = temp.id and user.name = temp.name ) " +
        "when matched then update set userid = temp.id, userlogin_name = temp.login_name, username = temp.name, userpassword = temp.password, usersalt = temp.salt, userregister_date = temp.register_date " +
        "when not matched then insert ( id, login_name, name, password, salt, register_date ) values ( temp.id, temp.login_name, temp.name, temp.password, temp.salt, temp.register_date )"
  }

  def "check generated upsert series xml for db2"() {
    setup:
    DB2UpsertPlugin plugin = new DB2UpsertPlugin();

    when:
    plugin.sqlMapDocumentGenerated(document, introspectedTable)
    print parseSqlForUpsert()
    log.info systemOutRule.log

    then:
    systemOutRule.log.trim() == "merge into user using (values ?, ?, ?, ?, ?, ?, ? ) temp ( id, login_name, name, password, salt, roles, register_date ) on user.id = temp.id and user.name = temp.name " +
        "when matched then update set userid = temp.id, userlogin_name = temp.login_name, username = temp.name, userpassword = temp.password, usersalt = temp.salt, userroles = temp.roles, userregister_date = temp.register_date " +
        "when not matched then insert ( id, login_name, name, password, salt, roles, register_date ) values ( temp.id, temp.login_name, temp.name, temp.password, temp.salt, temp.roles, temp.register_date )"

    when:
    systemOutRule.clearLog()
    print parseSqlForUpsertSelective()
    log.info systemOutRule.log

    then:
    systemOutRule.log.trim() == "merge into user using (values ?, ?, ?, ?, ?, ? ) temp ( id, name, password, salt, roles, register_date ) on user.id = temp.id and user.name = temp.name " +
        "when matched then update set userid = temp.id, username = temp.name, userpassword = temp.password, usersalt = temp.salt, userroles = temp.roles, userregister_date = temp.register_date " +
        "when not matched then insert ( id, name, password, salt, roles, register_date ) values ( temp.id, temp.name, temp.password, temp.salt, temp.roles, temp.register_date )"

    when:
    systemOutRule.clearLog()
    print parseSqlForBatchUpsert()
    log.info systemOutRule.log

    then:
    systemOutRule.log.trim() == "merge into user using (values ?, ?, ?, ?, ?, ?, ? ) temp ( id, login_name, name, password, salt, roles, register_date ) on user.id = temp.id and user.name = temp.name " +
        "when matched then update set userid = temp.id, userlogin_name = temp.login_name, username = temp.name, userpassword = temp.password, usersalt = temp.salt, userroles = temp.roles, userregister_date = temp.register_date " +
        "when not matched then insert ( id, login_name, name, password, salt, roles, register_date ) values ( temp.id, temp.login_name, temp.name, temp.password, temp.salt, temp.roles, temp.register_date ) ; " +
        "merge into user using (values ?, ?, ?, ?, ?, ?, ? ) temp ( id, login_name, name, password, salt, roles, register_date ) on user.id = temp.id and user.name = temp.name " +
        "when matched then update set userid = temp.id, userlogin_name = temp.login_name, username = temp.name, userpassword = temp.password, usersalt = temp.salt, userroles = temp.roles, userregister_date = temp.register_date " +
        "when not matched then insert ( id, login_name, name, password, salt, roles, register_date ) values ( temp.id, temp.login_name, temp.name, temp.password, temp.salt, temp.roles, temp.register_date )"

    when:
    systemOutRule.clearLog()
    print parseSqlForBatchUpsertSelective()
    log.info systemOutRule.log

    then:
    systemOutRule.log.trim() == "merge into user using (values ?, ?, ?, ?, ?, ? ) temp ( id, name, password, salt, roles, register_date ) on user.id = temp.id and user.name = temp.name " +
        "when matched then update set userid = temp.id, username = temp.name, userpassword = temp.password, usersalt = temp.salt, userroles = temp.roles, userregister_date = temp.register_date " +
        "when not matched then insert ( id, name, password, salt, roles, register_date ) values ( temp.id, temp.name, temp.password, temp.salt, temp.roles, temp.register_date ) ; " +
        "merge into user using (values ?, ?, ?, ?, ?, ? ) temp ( id, login_name, name, password, salt, register_date ) on user.id = temp.id and user.name = temp.name " +
        "when matched then update set userid = temp.id, userlogin_name = temp.login_name, username = temp.name, userpassword = temp.password, usersalt = temp.salt, userregister_date = temp.register_date " +
        "when not matched then insert ( id, login_name, name, password, salt, register_date ) values ( temp.id, temp.login_name, temp.name, temp.password, temp.salt, temp.register_date )"
  }

}
