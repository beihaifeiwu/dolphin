package com.freetmp.mbg.plugin.page;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * Created by LiuPin on 2015/2/4.
 */
public class HsqldbPaginationPlugin extends AbstractPaginationPlugin {

  @Override
  public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
    XmlElement isNotNullElement = new XmlElement("if");
    isNotNullElement.addAttribute(new Attribute("test", "limit != null and limit>=0 and offset != null"));
    isNotNullElement.addElement(new TextElement("limit #{limit} offset #{offset}"));
    element.addElement(isNotNullElement);
    return true;
  }
}
