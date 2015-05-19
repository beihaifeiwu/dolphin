package com.freetmp.mbg.plugin.upsert;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * Created by LiuPin on 2015/5/19.
 */
public class PostgreSQLUpsertPlugin extends UpsertPlugin {

  @Override
  protected void generateSqlMapContent(IntrospectedTable introspectedTable, XmlElement parent) {
    generateTextBlock("with upsert as ( ",parent);
    generateTextBlockAppendTableName("update ",introspectedTable,parent);
    generateTextBlock(" set ", parent);
    generateRecordFieldForSetWithIfNullCheck(PROPERTY_PREFIX,introspectedTable,parent);

    XmlElement where = checkArrayWhere(introspectedTable);
    parent.addElement(where);

    generateTextBlock(" returning * )",parent);
    generateTextBlockAppendTableName("insert into ",introspectedTable,parent);
    generateInsertColumnsWithParenthesis(introspectedTable.getAllColumns(),parent);
    generateTextBlock(" select ",parent);
    generateRecordFieldsSeparateByComma(PROPERTY_PREFIX,introspectedTable.getAllColumns(),parent);
    generateTextBlock(" where not exists ( select * from upsert )",parent);
  }
}
