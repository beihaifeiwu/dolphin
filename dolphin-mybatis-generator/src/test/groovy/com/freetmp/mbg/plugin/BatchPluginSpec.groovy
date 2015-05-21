package com.freetmp.mbg.plugin

import com.freetmp.mbg.plugin.batch.BatchInsertPlugin
import com.freetmp.mbg.plugin.batch.BatchUpdatePlugin
import groovy.util.logging.Slf4j
import org.mybatis.generator.api.dom.java.Method
import org.mybatis.generator.api.dom.xml.Element

/**
 * Created by LiuPin on 2015/5/21.
 */
@Slf4j
class BatchPluginSpec extends AbstractPluginSpec {

  def "check generated client interface and mapper xml for batch update"(){
    setup:
    BatchUpdatePlugin plugin = new BatchUpdatePlugin()

    when:
    plugin.clientGenerated(interfaze,topLevelClass,introspectedTable)

    then:
    1 * interfaze.addMethod {Method method -> method.getFormattedContent(0,true) == "int batchUpdate(List<User> list);"}
    1 * interfaze.addImportedTypes({it.size() >= 1})

    when:
    plugin.sqlMapDocumentGenerated(document,introspectedTable)

    then:
    1 * root.addElement(_) >> {Element element -> log.info element.getFormattedContent(0)}
  }

  def "check generated client interface for batch insert"(){
    setup:
    BatchInsertPlugin plugin = new BatchInsertPlugin()

    when:
    plugin.clientGenerated(interfaze,topLevelClass,introspectedTable)

    then:
    1 * interfaze.addMethod {Method method -> method.getFormattedContent(0,true) == "int batchInsert(List<User> list);"}

    when:
    plugin.sqlMapDocumentGenerated(document,introspectedTable)

    then:
    1 * root.addElement(_) >> {Element element -> log.info element.getFormattedContent(0)}
  }
}
