package com.freetmp.mbg.plugin.upsert;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * Created by LiuPin on 2015/5/19.
 */
public class SQLServerUpsertPlugin extends UpsertPlugin {

  @Override
  protected void generateSqlMapContent(IntrospectedTable introspectedTable, XmlElement parent) {
    generateTextBlockAppendTableName("update ", introspectedTable,parent);
    generateTextBlock(" set ",parent);
    generateRecordFieldForSetWithIfNullCheck(PROPERTY_PREFIX,introspectedTable,parent);
    parent.addElement(checkArrayWhere(introspectedTable));
    generateTextBlock(" if @@ROWCOUNT = 0 ",parent);
    generateTextBlockAppendTableName(" insert into ", introspectedTable,parent);
    generateInsertColumnsWithParenthesis(introspectedTable.getAllColumns(),parent);
    generateTextBlock(" values ", parent);
    generateRecordFieldsSeparateByCommaWithParenthesis(PROPERTY_PREFIX,introspectedTable.getAllColumns(),parent);
  }
}
