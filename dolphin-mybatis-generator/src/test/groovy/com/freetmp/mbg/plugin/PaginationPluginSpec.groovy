package com.freetmp.mbg.plugin

import com.freetmp.mbg.plugin.page.AbstractPaginationPlugin
import com.freetmp.mbg.plugin.page.MySqlPaginationPlugin
import groovy.util.logging.Slf4j
import org.mybatis.generator.api.dom.java.Field
import org.mybatis.generator.api.dom.java.InnerClass
import org.mybatis.generator.api.dom.java.Method
import org.mybatis.generator.api.dom.xml.Attribute
import org.mybatis.generator.api.dom.xml.Element
import org.mybatis.generator.api.dom.xml.XmlElement

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
    1 * example.addField { Field field -> log.info field.getFormattedContent(0); field.name == AbstractPaginationPlugin.LIMIT_NAME }
    1 * example.addField { Field field -> log.info field.getFormattedContent(0); field.name == AbstractPaginationPlugin.OFFSET_NAME }
    1 * example.addMethod { Method method -> log.info method.getFormattedContent(0, false); method.name == AbstractXmbgPlugin.uncapitalize(AbstractPaginationPlugin.BOUND_BUILDER_NAME) }
    1 * example.addInnerClass { InnerClass innerClass -> log.info innerClass.getFormattedContent(0); innerClass.type.shortName == AbstractPaginationPlugin.BOUND_BUILDER_NAME }
  }

  def "check generated xml mapper for mysql"() {
    setup:
    MySqlPaginationPlugin plugin = new MySqlPaginationPlugin();

    when:
    plugin.sqlMapSelectByExampleWithoutBLOBsElementGenerated(selectByExample, introspectedTable)

    then:
    1 * selectByExample.addElement {Element element ->log.info element.getFormattedContent(0);element != null}
  }

}
