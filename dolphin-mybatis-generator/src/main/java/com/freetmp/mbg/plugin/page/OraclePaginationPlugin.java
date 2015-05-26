package com.freetmp.mbg.plugin.page;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/**
 * Created by LiuPin on 2015/2/3.
 */
public class OraclePaginationPlugin extends AbstractPaginationPlugin {

  public OraclePaginationPlugin() {
    super();
  }

  @Override
  public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
    // 使用select语句包裹住原始的sql语句
    XmlElement checkIfPageable = new XmlElement("if");
    checkIfPageable.addAttribute(new Attribute("test", "limit != null and limit>=0 and offset != null"));
    TextElement prefix = new TextElement("select * from ( select tmp_page.*, rownum row_id from ( ");
    checkIfPageable.addElement(prefix);
    element.addElement(0, checkIfPageable);

    checkIfPageable = new XmlElement("if");
    checkIfPageable.addAttribute(new Attribute("test", "limit != null and limit>=0 and offset != null"));
    TextElement suffix = new TextElement("<![CDATA[ ) tmp_page where rownum <= #{limit} + #{offset} ) where row_id > #{offset} ]]>");
    checkIfPageable.addElement(suffix);
    element.addElement(checkIfPageable);
    return true;
  }

  @Override
  public boolean validate(List<String> warnings) {
    return true;
  }
}
