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

import java.lang.reflect.*;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/*
 * 存在即更新否则插入方法生成插件
 * @author Pin Liu
 */
public abstract class AbstractUpsertPlugin extends AbstractXmbgPlugin {

  public static final String UPSERT = "upsert";
  public static final String UPSERT_SELECTIVE = "upsertSelective";

  public static final String BATCH_UPSERT = "batchUpsert";
  public static final String BATCH_UPSERT_SELECTIVE = "batchUpsertSelective";

  public static final String IDENTIFIERS_ARRAY_WHERE = "Identifiers_Array_Where";

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

		/*-----------------------------添加批量upsert的接口方法--------------------------------------*/
    Method batchUpsert = new Method(BATCH_UPSERT);
    batchUpsert.setReturnType(FullyQualifiedJavaType.getIntInstance());

    FullyQualifiedJavaType list = new FullyQualifiedJavaType("java.util.List<" + introspectedTable.getTableConfiguration().getDomainObjectName() + ">");
    batchUpsert.addParameter(new Parameter(list, "list", "@Param(\"records\")"));
    importedTypes.add(list);

    batchUpsert.addParameter(new Parameter(arrayType, "array", "@Param(\"array\")"));

    interfaze.addMethod(batchUpsert);

    /*-----------------------------添加批量upsertSelective的接口方法--------------------------------------*/
    Method batchUpsertSelective = new Method(BATCH_UPSERT_SELECTIVE);
    batchUpsertSelective.setReturnType(FullyQualifiedJavaType.getIntInstance());
    batchUpsert.addParameter(new Parameter(list, "list", "@Param(\"records\")"));
    batchUpsertSelective.addParameter(new Parameter(arrayType, "array", "@Param(\"array\")"));

    interfaze.addMethod(batchUpsert);

    interfaze.addImportedTypes(importedTypes);
    return true;
  }

  @Override
  public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

    XmlElement sql = buildSqlClause(introspectedTable);
    document.getRootElement().addElement(sql);
    addSingleUpsertToSqlMap(document, introspectedTable);
    addSingleUpsertSelectiveToSqlMap(document,introspectedTable);
    addBatchUpsertToSqlMap(document, introspectedTable);
    addBatchUpsertSelectiveToSqlMap(document,introspectedTable);
    return true;
  }

  /**
   * add update xml element to mapper.xml for upsert
   * @param document
   * @param introspectedTable
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
   * add update xml element to mapper.xml for batch upsert
   * @param document
   * @param introspectedTable
   */
  protected void addBatchUpsertToSqlMap(Document document, IntrospectedTable introspectedTable) {
    XmlElement update = new XmlElement("update");
    update.addAttribute(new Attribute("id", BATCH_UPSERT));
    update.addAttribute(new Attribute("parameterType", "map"));

    XmlElement foreach = new XmlElement("foreach");
    foreach.addAttribute(new Attribute("collection", "records"));
    foreach.addAttribute(new Attribute("item", "record"));
    foreach.addAttribute(new Attribute("index", "index"));
    foreach.addAttribute(new Attribute("separator", " ; "));

    generateSqlMapContent(introspectedTable, foreach);
    update.addElement(foreach);

    document.getRootElement().addElement(update);
  }

  protected void addBatchUpsertSelectiveToSqlMap(Document document, IntrospectedTable introspectedTable) {
    XmlElement update = new XmlElement("update");
    update.addAttribute(new Attribute("id", BATCH_UPSERT_SELECTIVE));
    update.addAttribute(new Attribute("parameterType", "map"));

    XmlElement foreach = new XmlElement("foreach");
    foreach.addAttribute(new Attribute("collection", "records"));
    foreach.addAttribute(new Attribute("item", "record"));
    foreach.addAttribute(new Attribute("index", "index"));
    foreach.addAttribute(new Attribute("separator", " ; "));

    generateSqlMapContent(introspectedTable, foreach);
    update.addElement(foreach);

    document.getRootElement().addElement(update);
  }

  /*
   * 生成sqlMap里对应的xml元素
   * @author Pin Liu
   */
  protected abstract void generateSqlMapContent(IntrospectedTable introspectedTable, XmlElement parent);
  protected abstract void generateSqlMapContentSelective(IntrospectedTable introspectedTable, XmlElement parent);

  /*
   * 生成根据参数array判断where条件的元素
   * @author Pin Liu
   */
  protected XmlElement checkArrayWhere(IntrospectedTable introspectedTable) {
    XmlElement where = new XmlElement("where");

    XmlElement include = new XmlElement("include");
    include.addAttribute(new Attribute("refid", IDENTIFIERS_ARRAY_WHERE));

    where.addElement(include);

    return where;
  }

  /*
   * 创建根据传入的Array数组进行判断的sql语句
   * @author Pin Liu
   */
  protected XmlElement buildSqlClause(IntrospectedTable introspectedTable) {

    XmlElement sql = new XmlElement("sql");
    sql.addAttribute(new Attribute("id", IDENTIFIERS_ARRAY_WHERE));

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
