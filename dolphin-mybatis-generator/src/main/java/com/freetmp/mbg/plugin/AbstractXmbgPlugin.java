package com.freetmp.mbg.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Element;
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

  protected void generateParameterForSet(IntrospectedTable introspectedTable, XmlElement parent){
    generateParameterForSet("",false,introspectedTable,parent);
  }

  protected void generateParameterForSet(String fieldPrefix,IntrospectedTable introspectedTable, XmlElement parent){
    generateParameterForSet(fieldPrefix,false,introspectedTable,parent);
  }

  protected void generateParameterForSet(String fieldPrefix,boolean ifNullCheck, IntrospectedTable introspectedTable, XmlElement dynamicElement) {
    XmlElement trimElement = new XmlElement("trim");
    trimElement.addAttribute(new Attribute("suffixOverrides",","));

    StringBuilder sb = new StringBuilder();
    for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
      sb.setLength(0);
      sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
      sb.append(" = ");
      sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, fieldPrefix));
      sb.append(',');

      doIfNullCheck(fieldPrefix, ifNullCheck, trimElement, sb, introspectedColumn);
    }

    dynamicElement.addElement(trimElement);
  }

  protected void doIfNullCheck(String fieldPrefix, boolean ifNullCheck, XmlElement trimElement, StringBuilder sb, IntrospectedColumn introspectedColumn) {
    Element content;
    if(ifNullCheck) {
      content = wrapIfNullCheckForJavaProperty(fieldPrefix, new TextElement(sb.toString()), introspectedColumn);
    }else {
      content = new TextElement(sb.toString());
    }
    trimElement.addElement(content);
  }

  protected XmlElement wrapIfNullCheckForJavaProperty(String fieldPrefix, Element content, IntrospectedColumn introspectedColumn) {
    StringBuilder sb = new StringBuilder();
    XmlElement isNotNullElement = new XmlElement("if");
    sb.setLength(0);
    sb.append(introspectedColumn.getJavaProperty(fieldPrefix));
    sb.append(" != null");
    isNotNullElement.addAttribute(new Attribute("test", sb.toString()));
    isNotNullElement.addElement(content);
    return isNotNullElement;
  }

  protected void generateParametersSeparateByComma(List<IntrospectedColumn> columns, XmlElement parent){
    generateParametersSeparateByComma("",false,columns,parent);
  }

  protected void generateParametersSeparateByComma(String fieldPrefix,List<IntrospectedColumn> columns, XmlElement parent){
    generateParametersSeparateByComma(fieldPrefix,false,columns,parent);
  }

  protected void generateParametersSeparateByComma(String fieldPrefix,boolean ifNullCheck, List<IntrospectedColumn> columns, XmlElement parent) {
    generateParametersSeparateByComma(fieldPrefix,ifNullCheck,false,columns,parent);
  }

  protected void generateParametersSeparateByComma(String fieldPrefix,boolean ifNullCheck,boolean withParenthesis, List<IntrospectedColumn> columns, XmlElement parent) {
    XmlElement trimElement = new XmlElement("trim");
    trimElement.addAttribute(new Attribute("suffixOverrides",","));
    if(withParenthesis){
      trimElement.addAttribute(new Attribute("prefix","("));
      trimElement.addAttribute(new Attribute("suffix",")"));
    }

    StringBuilder sb = new StringBuilder();
    for (IntrospectedColumn introspectedColumn : columns) {
      sb.setLength(0);
      sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, fieldPrefix));
      sb.append(",");

      doIfNullCheck(fieldPrefix, ifNullCheck, trimElement, sb, introspectedColumn);
    }
    parent.addElement(trimElement);
  }

  protected void generateParametersSeparateByCommaWithParenthesis(List<IntrospectedColumn> columns, XmlElement parent){
    generateParametersSeparateByCommaWithParenthesis("",false,columns,parent);
  }

  protected void generateParametersSeparateByCommaWithParenthesis(String fieldPrefix,List<IntrospectedColumn> columns, XmlElement parent){
    generateParametersSeparateByCommaWithParenthesis(fieldPrefix,false,columns,parent);
  }

  protected void generateParametersSeparateByCommaWithParenthesis(String fieldPrefix,boolean ifNullCheck, List<IntrospectedColumn> columns, XmlElement parent) {
    generateParametersSeparateByComma(fieldPrefix,ifNullCheck,true,columns,parent);
  }

  protected void generateActualColumnNamesWithParenthesis(List<IntrospectedColumn> columns, XmlElement parent){
    generateActualColumnNamesWithParenthesis("",false,columns,parent);
  }

  protected void generateActualColumnNamesWithParenthesis(String fieldPrefix,boolean ifNullCheck,List<IntrospectedColumn> columns, XmlElement parent) {
    generateActualColumnNamesWithParenthesis(fieldPrefix, null,ifNullCheck, columns, parent);
  }

  protected void generateActualColumnNamesWithParenthesis(String fieldPrefix,String columnPrefix,boolean ifNullCheck,List<IntrospectedColumn> columns, XmlElement parent) {
    XmlElement trimElement = new XmlElement("trim");
    trimElement.addAttribute(new Attribute("suffixOverrides",","));
    trimElement.addAttribute(new Attribute("prefix","("));
    trimElement.addAttribute(new Attribute("suffix",")"));

    StringBuilder sb = new StringBuilder();
    for (IntrospectedColumn introspectedColumn : columns) {
      sb.setLength(0);
      sb.append((columnPrefix == null ? "" : columnPrefix) + MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
      sb.append(",");

      doIfNullCheck(fieldPrefix, ifNullCheck, trimElement, sb, introspectedColumn);
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
