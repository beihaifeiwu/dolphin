package com.freetmp.mbg.plugin.upsert;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

/**
 * Created by LiuPin on 2015/5/20.
 */
public class DB2UpsertPlugin extends AbstractUpsertPlugin {
  @Override protected void generateSqlMapContent(IntrospectedTable introspectedTable, XmlElement parent) {
    generateTextBlockAppendTableName("merge into ", introspectedTable, parent);
    generateTextBlock(" using (values ", parent);
    generateParametersSeparateByComma(introspectedTable.getAllColumns(), parent);
    generateTextBlock(" ) temp ", parent);
    generateActualColumnNamesWithParenthesis(introspectedTable.getAllColumns(), parent);
    generateTextBlock(" on ", parent);

    XmlElement include = new XmlElement("include");
    include.addAttribute(new Attribute("refid", IDENTIFIERS_ARRAY_CONDITIONS));
    parent.addElement(include);

    generateTextBlock(" when matched then update set ", parent);
    generateCopyForSetByPrefix(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime() + ".", "temp.", introspectedTable, parent);

    generateTextBlock(" when not matched then insert ", parent);
    generateActualColumnNamesWithParenthesis(introspectedTable.getAllColumns(), parent);
    generateTextBlock(" values ", parent);
    generateActualColumnNamesWithParenthesis(PROPERTY_PREFIX, "temp.", false, introspectedTable.getAllColumns(), parent);
  }

  @Override protected void generateSqlMapContentSelective(IntrospectedTable introspectedTable, XmlElement parent) {
    generateTextBlockAppendTableName("merge into ", introspectedTable, parent);
    generateTextBlock(" using (values ", parent);
    generateParametersSeparateByComma(PROPERTY_PREFIX, true, introspectedTable.getAllColumns(), parent);
    generateTextBlock(" ) temp ", parent);
    generateActualColumnNamesWithParenthesis(PROPERTY_PREFIX, true, introspectedTable.getAllColumns(), parent);
    generateTextBlock(" on ", parent);

    XmlElement include = new XmlElement("include");
    include.addAttribute(new Attribute("refid", IDENTIFIERS_ARRAY_CONDITIONS));
    parent.addElement(include);

    generateTextBlock(" when matched then update set ", parent);
    generateCopyForSetByPrefix(PROPERTY_PREFIX,
        introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime() + ".", "temp.", true, introspectedTable, parent);

    generateTextBlock(" when not matched then insert ", parent);
    generateActualColumnNamesWithParenthesis(PROPERTY_PREFIX, true, introspectedTable.getAllColumns(), parent);
    generateTextBlock(" values ", parent);
    generateActualColumnNamesWithParenthesis(PROPERTY_PREFIX, "temp.", true, introspectedTable.getAllColumns(), parent);
  }

  @Override protected XmlElement buildSqlClause(IntrospectedTable introspectedTable) {
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

      String columnName = MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn);

      sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime() + "." + columnName);
      sb.append(" = ");
      sb.append("temp." + columnName);

      isEqualElement.addElement(new TextElement(sb.toString()));
    }

    sql.addElement(foreach);

    return sql;
  }

  protected void generateCopyForSetByPrefix(String fieldPrefix, String leftPrefix, String rightPrefix, boolean ifNullCheck, IntrospectedTable introspectedTable, XmlElement dynamicElement) {
    XmlElement trimElement = new XmlElement("trim");
    trimElement.addAttribute(new Attribute("suffixOverrides", ","));

    StringBuilder sb = new StringBuilder();
    for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {

      sb.setLength(0);
      String columnName = MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn);
      sb.append(leftPrefix + columnName);
      sb.append(" = ");
      sb.append(rightPrefix + columnName);
      sb.append(',');

      doIfNullCheck(fieldPrefix, ifNullCheck, trimElement, sb, introspectedColumn);
    }

    dynamicElement.addElement(trimElement);
  }

  protected void generateCopyForSetByPrefix(String leftPrefix, String rightPrefix, IntrospectedTable introspectedTable, XmlElement dynamicElement) {
    generateCopyForSetByPrefix("", leftPrefix, rightPrefix, false, introspectedTable, dynamicElement);
  }
}
