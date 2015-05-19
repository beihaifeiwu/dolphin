package com.freetmp.mbg.plugin.upsert;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.XmlElement;


/**
 * Created by LiuPin on 2015/5/19.
 */
public class MySqlUpsertPlugin extends UpsertPlugin {

  @Override
  protected void generateSqlMapContent(IntrospectedTable introspectedTable, XmlElement parent) {

    generateTextBlockAppendTableName("insert into ",introspectedTable,parent);

    generateInsertColumnsWithParenthesis(introspectedTable.getAllColumns(),parent);

    generateTextBlock("values ",parent);

    generateRecordFieldsSeparateByCommaWithParenthesis(PROPERTY_PREFIX,introspectedTable.getAllColumns(),parent);

    generateTextBlock("on duplicate key update ",parent);

    generateRecordFieldForSetWithIfNullCheck(PROPERTY_PREFIX, introspectedTable, parent);
  }
}
