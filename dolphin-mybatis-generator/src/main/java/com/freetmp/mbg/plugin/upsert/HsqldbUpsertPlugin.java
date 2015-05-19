package com.freetmp.mbg.plugin.upsert;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

/**
 * Created by LiuPin on 2015/5/19.
 */
public class HsqldbUpsertPlugin extends UpsertPlugin {
  @Override protected void generateSqlMapContent(IntrospectedTable introspectedTable, XmlElement parent) {
    generateTextBlockAppendTableName("merge into ", introspectedTable,parent);
    generateTextBlock(" using (values ",parent);
    generateParametersSeparateByCommaWithIfNullCheck(PROPERTY_PREFIX, introspectedTable.getAllColumns(), parent);
    generateTextBlock(" ) temp ",parent);
    generateActualColumnNamesWithParenthesisAndIfNullCheck(PROPERTY_PREFIX, introspectedTable.getAllColumns(), parent);
    generateTextBlock(" on ( ",parent);

    XmlElement include = new XmlElement("include");
    include.addAttribute(new Attribute("refid", IDENTIFIERS_ARRAY_WHERE));
    parent.addElement(include);

    generateTextBlock(" ) ",parent);

    generateTextBlock(" when matched then update set ", parent);
    generateCopyForSetByPrefixWithIfNullCheck(PROPERTY_PREFIX,
        introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime(),"temp.",introspectedTable,parent);

    generateTextBlock(" when not matched then insert ", parent);
    generateActualColumnNamesWithParenthesisAndIfNullCheck(PROPERTY_PREFIX, introspectedTable.getAllColumns(), parent);
    generateTextBlock(" values ", parent);
    generateActualColumnNamesWithParenthesisAndIfNullCheck(PROPERTY_PREFIX,"temp.",introspectedTable.getAllColumns(),parent);
  }

  @Override protected XmlElement buildSqlClause(IntrospectedTable introspectedTable) {
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

      String columnName = MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn);

      sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime() + "." + columnName);
      sb.append(" = ");
      sb.append("temp." + columnName);

      isEqualElement.addElement(new TextElement(sb.toString()));
    }

    sql.addElement(foreach);

    return sql;
  }

  protected void generateCopyForSetByPrefixWithIfNullCheck(String fieldPrefix, String leftPrefix, String rightPrefix, IntrospectedTable introspectedTable, XmlElement dynamicElement) {
    XmlElement trimElement = new XmlElement("trim");
    trimElement.addAttribute(new Attribute("suffixOverrides",","));

    StringBuilder sb = new StringBuilder();
    for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
      XmlElement isNotNullElement = new XmlElement("if");
      sb.setLength(0);
      sb.append(introspectedColumn.getJavaProperty(fieldPrefix));
      sb.append(" != null");
      isNotNullElement.addAttribute(new Attribute("test", sb.toString()));
      trimElement.addElement(isNotNullElement);

      sb.setLength(0);
      String columnName = MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn);
      sb.append(leftPrefix + columnName);
      sb.append(" = ");
      sb.append(rightPrefix + columnName);
      sb.append(',');

      isNotNullElement.addElement(new TextElement(sb.toString()));
    }

    dynamicElement.addElement(trimElement);
  }
}
