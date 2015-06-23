package com.freetmp.mbg.plugin.upsert;

import com.freetmp.mbg.plugin.AbstractXmbgPlugin;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * 存在即更新否则插入方法生成插件
 *
 * @author Pin Liu
 */
public abstract class AbstractUpsertPlugin extends AbstractXmbgPlugin {

  private final Logger log = LoggerFactory.getLogger(AbstractUpsertPlugin.class);

  public static final String UPSERT = "upsert";
  public static final String UPSERT_SELECTIVE = "upsertSelective";

  public static final String IDENTIFIERS_ARRAY_CONDITIONS = "Identifiers_Array_Conditions";

  public static final String PROPERTY_PREFIX = "record.";

  @Override
  public boolean validate(List<String> warnings) {
    return true;
  }

  @Override
  public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

		/*-----------------------------添加单个upsert的接口方法-----------------------------------*/
    Method upsert = new Method(UPSERT);
    upsert.setReturnType(FullyQualifiedJavaType.getIntInstance());

    Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();

    FullyQualifiedJavaType parameterType = introspectedTable.getRules().calculateAllFieldsClass();
    upsert.addParameter(new Parameter(parameterType, "record", "@Param(\"record\")"));
    importedTypes.add(parameterType);

    FullyQualifiedJavaType arrayType = new FullyQualifiedJavaType((new String[]{}).getClass().getCanonicalName());
    upsert.addParameter(new Parameter(arrayType, "array", "@Param(\"array\")"));
    importedTypes.add(arrayType);

    importedTypes.add(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param"));

    interfaze.addMethod(upsert);

    /*-----------------------------添加单个upsertSelective的接口方法-----------------------------------*/
    Method upsertSelective = new Method(UPSERT_SELECTIVE);
    upsertSelective.setReturnType(FullyQualifiedJavaType.getIntInstance());
    upsertSelective.addParameter(new Parameter(parameterType, "record", "@Param(\"record\")"));
    upsertSelective.addParameter(new Parameter(arrayType, "array", "@Param(\"array\")"));

    interfaze.addMethod(upsertSelective);

    interfaze.addImportedTypes(importedTypes);
    return true;
  }

  @Override
  public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

    XmlElement sql = buildSqlClause(introspectedTable);
    document.getRootElement().addElement(sql);
    addSingleUpsertToSqlMap(document, introspectedTable);
    addSingleUpsertSelectiveToSqlMap(document, introspectedTable);
    return true;
  }

  /**
   * add update xml element to mapper.xml for upsert
   *
   * @param document          The generated xml mapper dom
   * @param introspectedTable The metadata for database table
   */
  protected void addSingleUpsertToSqlMap(Document document, IntrospectedTable introspectedTable) {
    XmlElement update = new XmlElement("update");
    update.addAttribute(new Attribute("id", UPSERT));
    update.addAttribute(new Attribute("parameterType", "map"));

    generateSqlMapContent(introspectedTable, update);

    document.getRootElement().addElement(update);
  }

  protected void addSingleUpsertSelectiveToSqlMap(Document document, IntrospectedTable introspectedTable) {
    XmlElement update = new XmlElement("update");
    update.addAttribute(new Attribute("id", UPSERT_SELECTIVE));
    update.addAttribute(new Attribute("parameterType", "map"));

    generateSqlMapContentSelective(introspectedTable, update);

    document.getRootElement().addElement(update);
  }

  /**
   * 生成sqlMap里对应的xml元素
   *
   * @param introspectedTable The metadata for database table
   * @param parent            The parent element for generated xml element
   */
  protected abstract void generateSqlMapContent(IntrospectedTable introspectedTable, XmlElement parent);

  protected abstract void generateSqlMapContentSelective(IntrospectedTable introspectedTable, XmlElement parent);

  /**
   * 生成根据参数array判断where条件的元素
   *
   * @param introspectedTable The metadata for database table
   * @return generated where condition element
   */
  protected XmlElement checkArrayWhere(IntrospectedTable introspectedTable) {
    XmlElement where = new XmlElement("where");

    XmlElement include = new XmlElement("include");
    include.addAttribute(new Attribute("refid", IDENTIFIERS_ARRAY_CONDITIONS));

    where.addElement(include);

    return where;
  }

  /**
   * 创建根据传入的Array数组进行判断的sql语句
   *
   * @param introspectedTable The metadata for database table
   * @return generated xml element for check input array params
   */
  protected XmlElement buildSqlClause(IntrospectedTable introspectedTable) {

    XmlElement sql = new XmlElement("sql");
    sql.addAttribute(new Attribute("id", IDENTIFIERS_ARRAY_CONDITIONS));

    XmlElement foreach = new XmlElement("foreach");
    foreach.addAttribute(new Attribute("collection", "array"));
    foreach.addAttribute(new Attribute("item", "item"));
    foreach.addAttribute(new Attribute("index", "index"));
    foreach.addAttribute(new Attribute("separator", " and "));

    StringBuilder sb = new StringBuilder();
    for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
      XmlElement isEqualElement = new XmlElement("if");
      sb.setLength(0);
      sb.append("item == \'");
      sb.append(introspectedColumn.getJavaProperty());
      sb.append("\'");
      isEqualElement.addAttribute(new Attribute("test", sb.toString()));
      foreach.addElement(isEqualElement);

      sb.setLength(0);
      sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
      sb.append(" = ");
      sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, PROPERTY_PREFIX));

      isEqualElement.addElement(new TextElement(sb.toString()));
    }

    sql.addElement(foreach);
    return sql;
  }

}
