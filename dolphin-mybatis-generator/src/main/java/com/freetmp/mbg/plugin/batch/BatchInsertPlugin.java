package com.freetmp.mbg.plugin.batch;

import com.freetmp.mbg.plugin.AbstractXmbgPlugin;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.GeneratedKey;

import java.util.List;

/**
 * 批量插入生成插件
 *
 * @author Pin Liu
 */
public class BatchInsertPlugin extends AbstractXmbgPlugin {

  public static final String BATCH_INSERT = "batchInsert";

  public static final String PROPERTY_PREFIX = "item.";

  @Override
  public boolean validate(List<String> warnings) {
    return true;
  }

  @Override
  public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
    String objectName = introspectedTable.getTableConfiguration().getDomainObjectName();
    Method method = new Method(BATCH_INSERT);
    FullyQualifiedJavaType type = new FullyQualifiedJavaType("java.util.List<" + objectName + ">");
    method.addParameter(new Parameter(type, "list"));
    method.setReturnType(FullyQualifiedJavaType.getIntInstance());
    interfaze.addMethod(method);
    return true;
  }

  @Override
  public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
    XmlElement answer = new XmlElement("insert"); //$NON-NLS-1$

    answer.addAttribute(new Attribute("id", BATCH_INSERT)); //$NON-NLS-1$
    FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType("java.util.List");
    answer.addAttribute(new Attribute("parameterType", parameterType.getFullyQualifiedName()));

// 批处理中无法主键生成
/**       GeneratedKey gk = introspectedTable.getGeneratedKey();
 if (gk != null) {
 IntrospectedColumn introspectedColumn = introspectedTable.getColumn(gk.getColumn());
 // if the column is null, then it's a configuration error. The
 // warning has already been reported
 if (introspectedColumn != null) {
 if (gk.isJdbcStandard()) {
 answer.addAttribute(new Attribute("useGeneratedKeys", "true"));
 answer.addAttribute(new Attribute("keyProperty", introspectedColumn.getJavaProperty()));
 } else {
 answer.addElement(getSelectKey(introspectedColumn, gk));
 }
 }
 }*/
    generateTextBlockAppendTableName("insert into ", introspectedTable,answer);

    generateActualColumnNamesWithParenthesis(introspectedTable.getNonBLOBColumns(),answer);

    generateTextBlock(" values ", answer);

    XmlElement foreach = new XmlElement("foreach");
    foreach.addAttribute(new Attribute("collection", "list"));
    foreach.addAttribute(new Attribute("item", "item"));
    foreach.addAttribute(new Attribute("index", "index"));
    foreach.addAttribute(new Attribute("separator", ","));

    generateParametersSeparateByCommaWithParenthesis(PROPERTY_PREFIX,introspectedTable.getNonBLOBColumns(),foreach);

    answer.addElement(foreach);

    document.getRootElement().addElement(answer);

    return true;
  }

  protected XmlElement getSelectKey(IntrospectedColumn introspectedColumn, GeneratedKey generatedKey) {
    String identityColumnType = introspectedColumn
        .getFullyQualifiedJavaType().getFullyQualifiedName();

    XmlElement answer = new XmlElement("selectKey"); //$NON-NLS-1$
    answer.addAttribute(new Attribute("resultType", identityColumnType)); //$NON-NLS-1$
    answer.addAttribute(new Attribute("keyProperty", introspectedColumn.getJavaProperty())); //$NON-NLS-1$
    answer.addAttribute(new Attribute("order", //$NON-NLS-1$
        generatedKey.getMyBatis3Order()));

    answer.addElement(new TextElement(generatedKey.getRuntimeSqlStatement()));

    return answer;
  }


}
