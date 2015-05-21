package com.freetmp.mbg.plugin

import com.freetmp.mbg.plugin.batch.BatchUpdatePlugin
import org.mybatis.generator.api.dom.java.Method

/**
 * Created by LiuPin on 2015/5/21.
 */
class BatchPluginSpec extends AbstractPluginSpec {

  def "check generated client interface for batch update"(){
    setup:
    BatchUpdatePlugin plugin = new BatchUpdatePlugin()

    when:
    plugin.clientGenerated(interfaze,topLevelClass,introspectedTable)

    then:
    1 * interfaze.addMethod {Method method -> method.getFormattedContent(0,true) == "int batchUpdate(List<User> list);"}
    1 * interfaze.addImportedTypes({it.size() >= 1})
  }
}
