package com.freetmp.mbg.plugin.upsert;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.XmlElement;


/**
 * Created by LiuPin on 2015/5/19.
 */
public class MySqlUpsertPlugin extends AbstractUpsertPlugin {

  @Override
  protected void generateSqlMapContent(IntrospectedTable introspectedTable, XmlElement parent) {

    generateTextBlockAppendTableName("insert into ",introspectedTable,parent);
    generateActualColumnNamesWithParenthesis(introspectedTable.getAllColumns(), parent);
    generateTextBlock("values ",parent);
    generateParametersSeparateByCommaWithParenthesis(PROPERTY_PREFIX, introspectedTable.getAllColumns(), parent);
    generateTextBlock("on duplicate key update ",parent);
    generateParameterForSet(PROPERTY_PREFIX, introspectedTable.getAllColumns(), parent);
  }

  @Override protected void generateSqlMapContentSelective(IntrospectedTable introspectedTable, XmlElement parent) {
    generateTextBlockAppendTableName("insert into ",introspectedTable,parent);
    generateActualColumnNamesWithParenthesis(PROPERTY_PREFIX,true,introspectedTable.getAllColumns(),parent);
    generateTextBlock("values ",parent);
    generateParametersSeparateByCommaWithParenthesis(PROPERTY_PREFIX,true,introspectedTable.getAllColumns(),parent);
    generateTextBlock("on duplicate key update ",parent);
    generateParameterForSet(PROPERTY_PREFIX,true, introspectedTable.getAllColumns(), parent);
  }
}
