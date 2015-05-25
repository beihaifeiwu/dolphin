package com.freetmp.mbg.plugin.page;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuPin on 2015/2/4.
 */
public class SQLServerPaginationPlugin extends AbstractPaginationPlugin {

  int findElementIndex(List<Element> elements, String name, String key, String value) {
    int index = -1;
    for (int i = 0; i < elements.size(); i++) {
      Element element = elements.get(i);
      if (element instanceof XmlElement) {
        XmlElement xe = (XmlElement) element;
        if (matched(xe, name, key, value)) {
          index = i;
          break;
        }
      }
    }
    return index;
  }

  boolean matched(XmlElement element, String name, String key, String value) {
    if (element == null) return false;
    if (name.trim().equals(element.getName().trim())) {
      List<Attribute> attributes = element.getAttributes();
      if (attributes != null) {
        for (Attribute attribute : attributes) {
          if (StringUtils.equals(attribute.getName().trim(), key.trim())
              && StringUtils.equals(attribute.getValue().trim(), value.trim())) {
            return true;
          }
        }
      }
    }
    return false;
  }

  Element findElement(List<Element> elements, String name, String key, String value) {
    int index = findElementIndex(elements, name, key, value);
    Element element = elements.get(index);
    return element;
  }

  @Override
  public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {

    XmlElement orderBy = (XmlElement) findElement(element.getElements(), "if", "test", "orderByClause != null");
    XmlElement baseColumnList = (XmlElement) findElement(element.getElements(), "include", "refid", introspectedTable.getBaseColumnListId());

    XmlElement prefix = buildPrefix(baseColumnList);
    XmlElement inner = buildInner(orderBy, introspectedTable);
    XmlElement suffix = buildSuffix(orderBy);


    List<Element> elements = element.getElements();
    List<Element> newElements = new ArrayList<>();

    newElements.add(prefix);
    for (Element e : elements) {
      if(e != orderBy) {
        newElements.add(e);
        if( e == baseColumnList){
          newElements.add(inner);
        }
      }
    }
    newElements.add(suffix);

    elements.clear();
    elements.addAll(newElements);
    return true;
  }

  private XmlElement buildInner(XmlElement orderBy, IntrospectedTable introspectedTable) {
    // generate order by clause, first check if user provided order by clause, if provided just use it
    // otherwise use the default primary key for order by clause
    XmlElement newOrderBy = new XmlElement("choose");
    XmlElement when = new XmlElement("when");
    when.addAttribute(new Attribute("test", "orderByClause != null"));
    for (Element e : orderBy.getElements()) {
      when.addElement(e);
    }
    newOrderBy.addElement(when);

    XmlElement otherwise = new XmlElement("otherwise");
    StringBuilder sb = new StringBuilder();
    sb.append(" order by ");
    List<IntrospectedColumn> columns = introspectedTable.getPrimaryKeyColumns();
    for (IntrospectedColumn column : columns) {
      sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(column)).append(", ");
    }
    sb.setLength(sb.length() - 2);
    otherwise.addElement(new TextElement(sb.toString()));
    newOrderBy.addElement(otherwise);

    XmlElement inner = new XmlElement("if");
    inner.addAttribute(new Attribute("test", "limit != null and limit>=0 and offset != null"));
    inner.addElement(new TextElement(" , ROW_NUMBER() over ( "));
    inner.addElement(newOrderBy);
    inner.addElement(new TextElement(" ) as row_num "));
    return inner;
  }

  private XmlElement buildSuffix(XmlElement orderBy) {
    XmlElement suffix = new XmlElement("choose");

    XmlElement when = new XmlElement("when");
    when.addAttribute(new Attribute("test", "limit != null and limit>=0 and offset != null"));
    when.addElement(new TextElement(" ) as tmp where tmp.row_num between #{offset} and #{limit} + #{offset} order by row_num"));
    suffix.addElement(when);

    XmlElement otherwise = new XmlElement("otherwise");
    otherwise.addElement(orderBy);
    suffix.addElement(otherwise);
    return suffix;
  }

  private XmlElement buildPrefix(XmlElement baseColumnList) {
    XmlElement prefix = new XmlElement("if");
    prefix.addAttribute(new Attribute("test", "limit != null and limit>=0 and offset != null"));
    prefix.addElement(new TextElement("select "));
    prefix.addElement(baseColumnList);
    prefix.addElement(new TextElement(" from ( "));
    return prefix;
  }
}
