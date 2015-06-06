package com.freetmp.mbg.plugin.upsert;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * Created by LiuPin on 2015/5/19.
 */
public class PostgreSQLUpsertPlugin extends AbstractUpsertPlugin {

  @Override
  protected void generateSqlMapContent(IntrospectedTable introspectedTable, XmlElement parent) {
    generateTextBlock("with upsert as ( ", parent);
    generateTextBlockAppendTableName("update ", introspectedTable, parent);
    generateTextBlock(" set ", parent);
    generateParameterForSet(PROPERTY_PREFIX, introspectedTable.getAllColumns(), parent);

    XmlElement where = checkArrayWhere(introspectedTable);
    parent.addElement(where);

    generateTextBlock(" returning * )", parent);
    generateTextBlockAppendTableName("insert into ", introspectedTable, parent);
    generateActualColumnNamesWithParenthesis(introspectedTable.getAllColumns(), parent);
    generateTextBlock(" select ", parent);
    generateParametersSeparateByComma(PROPERTY_PREFIX, introspectedTable.getAllColumns(), parent);
    generateTextBlock(" where not exists ( select * from upsert )", parent);
  }

  @Override
  protected void generateSqlMapContentSelective(IntrospectedTable introspectedTable, XmlElement parent) {
    generateTextBlock("with upsert as ( ", parent);
    generateTextBlockAppendTableName("update ", introspectedTable, parent);
    generateTextBlock(" set ", parent);
    generateParameterForSet(PROPERTY_PREFIX, true, introspectedTable.getAllColumns(), parent);

    XmlElement where = checkArrayWhere(introspectedTable);
    parent.addElement(where);

    generateTextBlock(" returning * )", parent);
    generateTextBlockAppendTableName("insert into ", introspectedTable, parent);
    generateActualColumnNamesWithParenthesis(PROPERTY_PREFIX, true, introspectedTable.getAllColumns(), parent);
    generateTextBlock(" select ", parent);
    generateParametersSeparateByComma(PROPERTY_PREFIX, true, introspectedTable.getAllColumns(), parent);
    generateTextBlock(" where not exists ( select * from upsert )", parent);

  }
}
