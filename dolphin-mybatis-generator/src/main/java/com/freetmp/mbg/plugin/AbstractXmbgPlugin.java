package com.freetmp.mbg.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.util.List;

/**
 * Created by LiuPin on 2015/5/19.
 */
public abstract class AbstractXmbgPlugin extends PluginAdapter {

  protected  void generateTextBlock(String text, XmlElement parent){
    parent.addElement(new TextElement(text));
  }

  protected void generateTextBlockAppendTableName(String text,IntrospectedTable introspectedTable, XmlElement parent){
    StringBuilder sb = new StringBuilder();
    sb.append(text);
    sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
    parent.addElement(new TextElement(sb.toString()));
  }

  protected void generateParameterForSetWithIfNullCheck(String fieldPrefix, IntrospectedTable introspectedTable, XmlElement dynamicElement) {
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
      sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
      sb.append(" = ");
      sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, fieldPrefix));
      sb.append(',');

      isNotNullElement.addElement(new TextElement(sb.toString()));
    }

    dynamicElement.addElement(trimElement);
  }

  protected void generateParametersSeparateByComma(String fieldPrefix, List<IntrospectedColumn> columns, XmlElement parent) {
    StringBuilder sb = new StringBuilder();
    for (IntrospectedColumn introspectedColumn : columns) {
      sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, fieldPrefix));
      if (sb.length() > 80) {
        parent.addElement(new TextElement(sb.toString()));
        sb.setLength(0);
        OutputUtilities.xmlIndent(sb, 2);
      }
      sb.append(",");
    }
    sb.setLength(sb.length() - 1);
    parent.addElement(new TextElement(sb.toString()));
  }

  protected void generateParametersSeparateByCommaWithIfNullCheck(String fieldPrefix, List<IntrospectedColumn> columns, XmlElement parent) {
    XmlElement trimElement = new XmlElement("trim");
    trimElement.addAttribute(new Attribute("suffixOverrides",","));

    StringBuilder sb = new StringBuilder();
    for (IntrospectedColumn introspectedColumn : columns) {
      XmlElement isNotNullElement = new XmlElement("if");
      sb.append(introspectedColumn.getJavaProperty(fieldPrefix));
      sb.append(" != null");
      isNotNullElement.addAttribute(new Attribute("test", sb.toString()));
      trimElement.addElement(isNotNullElement);

      sb.setLength(0);
      sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, fieldPrefix));
      sb.append(",");

      isNotNullElement.addElement(new TextElement(sb.toString()));

      sb.setLength(0);
    }
    parent.addElement(trimElement);
  }

  protected void generateParametersSeparateByCommaWithParenthesis(String fieldPrefix, List<IntrospectedColumn> columns, XmlElement parent) {
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    for (IntrospectedColumn introspectedColumn : columns) {
      sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, fieldPrefix));
      if (sb.length() > 80) {
        parent.addElement(new TextElement(sb.toString()));
        sb.setLength(0);
        OutputUtilities.xmlIndent(sb, 2);
      }
      sb.append(",");
    }
    sb.setLength(sb.length() - 1);
    sb.append(")");
    parent.addElement(new TextElement(sb.toString()));
  }

  protected void generateActualColumnNamesWithParenthesis(List<IntrospectedColumn> columns, XmlElement parent) {
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    for (IntrospectedColumn introspectedColumn : columns) {
      sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
      if (sb.length() > 80) {
        parent.addElement(new TextElement(sb.toString()));
        sb.setLength(0);
        OutputUtilities.xmlIndent(sb, 2);
      }
      sb.append(",");
    }
    sb.setLength(sb.length() - 1);
    sb.append(") ");
    parent.addElement(new TextElement(sb.toString()));
  }

  protected void generateActualColumnNamesWithParenthesisAndIfNullCheck(String fieldPrefix,List<IntrospectedColumn> columns, XmlElement parent) {
    generateActualColumnNamesWithParenthesisAndIfNullCheck(fieldPrefix,null,columns,parent);
  }

  protected void generateActualColumnNamesWithParenthesisAndIfNullCheck(String fieldPrefix,String columnPrefix,List<IntrospectedColumn> columns, XmlElement parent) {
    XmlElement trimElement = new XmlElement("trim");
    trimElement.addAttribute(new Attribute("suffixOverrides",","));
    trimElement.addAttribute(new Attribute("prefix","("));
    trimElement.addAttribute(new Attribute("suffix",")"));

    StringBuilder sb = new StringBuilder();
    sb.append("(");
    for (IntrospectedColumn introspectedColumn : columns) {
      XmlElement isNotNullElement = new XmlElement("if");
      sb.append(introspectedColumn.getJavaProperty(fieldPrefix));
      sb.append(" != null");
      isNotNullElement.addAttribute(new Attribute("test", sb.toString()));
      trimElement.addElement(isNotNullElement);

      sb.setLength(0);
      sb.append((columnPrefix == null ? "" : columnPrefix) + MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
      sb.append(",");
      isNotNullElement.addElement(new TextElement(sb.toString()));
      sb.setLength(0);
    }

    parent.addElement(trimElement);
  }

  protected boolean checkIfColumnIsPK(IntrospectedColumn column){
    List<IntrospectedColumn> pks = column.getIntrospectedTable().getPrimaryKeyColumns();

    for(IntrospectedColumn pk : pks){
      if(column.getActualColumnName().equals(pk.getActualColumnName())){
        return true;
      }
    }

    return false;
  }
}
