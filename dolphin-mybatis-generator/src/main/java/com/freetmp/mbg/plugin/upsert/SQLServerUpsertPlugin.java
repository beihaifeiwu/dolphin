package com.freetmp.mbg.plugin.upsert;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * Created by LiuPin on 2015/5/19.
 */
public class SQLServerUpsertPlugin extends AbstractUpsertPlugin {

  @Override
  protected void generateSqlMapContent(IntrospectedTable introspectedTable, XmlElement parent) {
    generateTextBlockAppendTableName("update ", introspectedTable,parent);
    generateTextBlock(" set ",parent);
    generateParameterForSet(PROPERTY_PREFIX, introspectedTable.getAllColumns(), parent);
    parent.addElement(checkArrayWhere(introspectedTable));
    generateTextBlock(" if @@ROWCOUNT = 0 ",parent);
    generateTextBlockAppendTableName(" insert into ", introspectedTable,parent);
    generateActualColumnNamesWithParenthesis(introspectedTable.getAllColumns(), parent);
    generateTextBlock(" values ", parent);
    generateParametersSeparateByCommaWithParenthesis(PROPERTY_PREFIX, introspectedTable.getAllColumns(), parent);
  }

  @Override protected void generateSqlMapContentSelective(IntrospectedTable introspectedTable, XmlElement parent) {
    generateTextBlockAppendTableName("update ", introspectedTable,parent);
    generateTextBlock(" set ",parent);
    generateParameterForSet(PROPERTY_PREFIX,true, introspectedTable.getAllColumns(), parent);
    parent.addElement(checkArrayWhere(introspectedTable));
    generateTextBlock(" if @@ROWCOUNT = 0 ",parent);
    generateTextBlockAppendTableName(" insert into ", introspectedTable,parent);
    generateActualColumnNamesWithParenthesis(PROPERTY_PREFIX,true,introspectedTable.getAllColumns(), parent);
    generateTextBlock(" values ", parent);
    generateParametersSeparateByCommaWithParenthesis(PROPERTY_PREFIX,true, introspectedTable.getAllColumns(), parent);
  }
}
