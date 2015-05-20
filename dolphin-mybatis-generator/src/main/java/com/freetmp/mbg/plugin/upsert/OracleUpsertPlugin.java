package com.freetmp.mbg.plugin.upsert;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * Created by LiuPin on 2015/5/19.
 */
public class OracleUpsertPlugin extends AbstractUpsertPlugin {
  @Override protected void generateSqlMapContent(IntrospectedTable introspectedTable, XmlElement parent) {
    generateTextBlockAppendTableName("merge into ", introspectedTable, parent);
    generateTextBlock(" using dual on ( ", parent);

    XmlElement include = new XmlElement("include");
    include.addAttribute(new Attribute("refid", IDENTIFIERS_ARRAY_CONDITIONS));
    parent.addElement(include);

    generateTextBlock(" ) ", parent);
    generateTextBlock(" when matched then update set", parent);
    generateParameterForSet(PROPERTY_PREFIX, introspectedTable.getAllColumns(), parent);

    generateTextBlock(" when not matched then insert ", parent);
    generateActualColumnNamesWithParenthesis(introspectedTable.getAllColumns(), parent);
    generateTextBlock("  values ", parent);
    generateParametersSeparateByCommaWithParenthesis(PROPERTY_PREFIX, introspectedTable.getAllColumns(), parent);

  }

  @Override protected void generateSqlMapContentSelective(IntrospectedTable introspectedTable, XmlElement parent) {
    generateTextBlockAppendTableName("merge into ", introspectedTable, parent);
    generateTextBlock(" using dual on ( ", parent);

    XmlElement include = new XmlElement("include");
    include.addAttribute(new Attribute("refid", IDENTIFIERS_ARRAY_CONDITIONS));
    parent.addElement(include);

    generateTextBlock(" ) ", parent);
    generateTextBlock(" when matched then update set ", parent);
    generateParameterForSet(PROPERTY_PREFIX, true, introspectedTable.getAllColumns(), parent);

    generateTextBlock(" when not matched then insert ", parent);
    generateActualColumnNamesWithParenthesis(PROPERTY_PREFIX, true, introspectedTable.getAllColumns(), parent);
    generateTextBlock("  values ", parent);
    generateParametersSeparateByCommaWithParenthesis(PROPERTY_PREFIX, true, introspectedTable.getAllColumns(), parent);
  }
}
