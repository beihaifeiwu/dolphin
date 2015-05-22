package com.freetmp.mbg.plugin

import org.apache.ibatis.builder.xml.XMLMapperBuilder
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver
import org.apache.ibatis.parsing.XNode
import org.apache.ibatis.parsing.XPathParser
import org.apache.ibatis.session.Configuration
import org.apache.log4j.ConsoleAppender
import org.apache.log4j.Logger
import org.apache.log4j.PatternLayout
import org.junit.Rule
import org.junit.contrib.java.lang.system.SystemOutRule
import org.mybatis.generator.api.CommentGenerator
import org.mybatis.generator.api.IntrospectedColumn
import org.mybatis.generator.api.IntrospectedTable
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType
import org.mybatis.generator.api.dom.java.Interface
import org.mybatis.generator.api.dom.java.TopLevelClass
import org.mybatis.generator.api.dom.xml.Attribute
import org.mybatis.generator.api.dom.xml.Document
import org.mybatis.generator.api.dom.xml.Element
import org.mybatis.generator.api.dom.xml.XmlElement
import org.mybatis.generator.codegen.XmlConstants
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.BaseColumnListElementGenerator
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.ExampleWhereClauseElementGenerator
import org.mybatis.generator.codegen.mybatis3.xmlmapper.elements.ResultMapWithoutBLOBsElementGenerator
import org.mybatis.generator.config.Context
import org.mybatis.generator.config.TableConfiguration
import org.mybatis.generator.internal.PluginAggregator
import org.mybatis.generator.internal.rules.Rules
import spock.lang.Specification

/**
 * Created by LiuPin on 2015/5/20.
 */
abstract class AbstractPluginSpec extends Specification {

  @Rule
  SystemOutRule systemOutRule = new SystemOutRule().enableLog().muteForSuccessfulTests()

  // JavaSource generate related
  Interface mapper = Spy(Interface, constructorArgs: [User.class.canonicalName + "Mapper"])
  TopLevelClass mapperImpl = Spy(TopLevelClass, constructorArgs: [User.class.canonicalName + "MapperImpl"])
  TopLevelClass example = Spy(TopLevelClass, constructorArgs: [User.class.canonicalName + "Example"])
  TopLevelClass entity = Spy(TopLevelClass, constructorArgs: [User.class.canonicalName])

  // Database table metadata related
  Rules rules = Stub()
  TableConfiguration tableConfiguration = Stub()
  IntrospectedTable introspectedTable = Spy(IntrospectedTable, constructorArgs: [IntrospectedTable.TargetRuntime.MYBATIS3])
  List<IntrospectedColumn> introspectedColumns = []
  List<IntrospectedColumn> introspectedNpkColumns = []
  List<IntrospectedColumn> introspectedPkColumns = []
  List<IntrospectedColumn> introspectedBaseColumns = []

  // Mybatis generator config context related
  Context mbgContext = Stub()
  CommentGenerator commentGenerator = Mock()

  // Xml mapper generate related
  XmlElement root = Spy(XmlElement, constructorArgs: ["mapper"])
  Document document = Stub()

  // Xml mapper parser related
  Map<String, XNode> sqlFragments = [:]
  Configuration configuration = new Configuration()

  def setup() {
    document.rootElement >> root
    rules.calculateAllFieldsClass() >> new FullyQualifiedJavaType(User.class.canonicalName)
    tableConfiguration.getDomainObjectName() >> User.class.simpleName

    introspectedColumns << Mock(IntrospectedColumn) { getJavaProperty(_) >> { String prefix -> prefix + "id" }; getJavaProperty() >> "id"; getJdbcTypeName() >> "BIGINT"; getActualColumnName() >> "id"; isColumnNameDelimited() >> false }
    introspectedColumns << Mock(IntrospectedColumn) { getJavaProperty(_) >> { String prefix -> prefix + "loginName" }; getJavaProperty() >> "loginName"; getJdbcTypeName() >> "VARCHAR"; getActualColumnName() >> "login_Name"; isColumnNameDelimited() >> false }
    introspectedColumns << Mock(IntrospectedColumn) { getJavaProperty(_) >> { String prefix -> prefix + "name" }; getJavaProperty() >> "name"; getJdbcTypeName() >> "VARCHAR"; getActualColumnName() >> "name"; isColumnNameDelimited() >> false }
    introspectedColumns << Mock(IntrospectedColumn) { getJavaProperty(_) >> { String prefix -> prefix + "password" }; getJavaProperty() >> "password"; getJdbcTypeName() >> "VARCHAR"; getActualColumnName() >> "password"; isColumnNameDelimited() >> false }
    introspectedColumns << Mock(IntrospectedColumn) { getJavaProperty(_) >> { String prefix -> prefix + "salt" }; getJavaProperty() >> "salt"; getJdbcTypeName() >> "VARCHAR"; getActualColumnName() >> "salt"; isColumnNameDelimited() >> false }
    introspectedColumns << Mock(IntrospectedColumn) { getJavaProperty(_) >> { String prefix -> prefix + "roles" }; getJavaProperty() >> "roles"; getJdbcTypeName() >> "VARCHAR"; getActualColumnName() >> "roles"; isColumnNameDelimited() >> false }
    introspectedColumns << Mock(IntrospectedColumn) { getJavaProperty(_) >> { String prefix -> prefix + "registerDate" }; getJavaProperty() >> "registerDate"; getJdbcTypeName() >> "TIMESTAMP"; getActualColumnName() >> "register_date"; isColumnNameDelimited() >> false }

    introspectedNpkColumns.addAll introspectedColumns[1..-1]
    introspectedPkColumns << introspectedColumns[0]
    introspectedBaseColumns.addAll introspectedColumns[1..-1]

    introspectedTable.getRules() >> rules
    introspectedTable.getTableConfiguration() >> tableConfiguration
    introspectedTable.getAllColumns() >> introspectedColumns
    introspectedTable.getPrimaryKeyColumns() >> introspectedPkColumns
    introspectedTable.getNonPrimaryKeyColumns() >> introspectedNpkColumns
    introspectedTable.getBaseColumns() >> introspectedBaseColumns
    introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime() >> "user"
    introspectedTable.constructorBased >> false
    introspectedTable.baseRecordType >> User.class.canonicalName

    mbgContext.commentGenerator >> commentGenerator
    def plugins = new PluginAggregator();
    mbgContext.getPlugins() >> plugins
  }

  def setupSpec() {
    Logger.rootLogger.addAppender new ConsoleAppender(new PatternLayout("%-4r [%t] %-5p %c %x - %m%n"))
  }

  /**
   * determine if element is XmlElement which has a id attribute with specific value
   */
  def isXmlElementWithIdEquals(Element element, String idStr) {
    XmlElement xml = element instanceof XmlElement ? element : null
    xml != null && xml.attributes.find({it.name == "id" && it.value == idStr}) != null
  }

  /**
   * xml mapper parser helper method
   */
  def addBaseElement() {
    XmlElement root = new XmlElement("mapper")
    root.attributes << new Attribute("namespace", User.class.canonicalName + "Mapper")
    Document document = new Document(XmlConstants.MYBATIS3_MAPPER_PUBLIC_ID, XmlConstants.MYBATIS3_MAPPER_SYSTEM_ID)
    document.rootElement = root

    def baseColumnList = new BaseColumnListElementGenerator(introspectedTable: introspectedTable, context: mbgContext)
    def exampleWhere = new ExampleWhereClauseElementGenerator(false)
    exampleWhere.introspectedTable = introspectedTable
    exampleWhere.context = mbgContext
    def resultMapWithoutBlobs = new ResultMapWithoutBLOBsElementGenerator(true)
    resultMapWithoutBlobs.introspectedTable = introspectedTable
    resultMapWithoutBlobs.context = mbgContext

    resultMapWithoutBlobs.addElements(root)
    exampleWhere.addElements(root)
    baseColumnList.addElements(root)
    return document
  }

  def parseXml(XmlElement element) {
    Document document = addBaseElement()
    document.rootElement.addElement element
    def parser = new XPathParser(document.formattedContent, false, Stub(Properties), new XMLMapperEntityResolver())
    def mapperBuilder = new XMLMapperBuilder(parser, configuration, "", sqlFragments)
    mapperBuilder.parse()
    configuration.getMappedStatement element.getAttributes().find({ it.name == "id" }).value
  }

}
