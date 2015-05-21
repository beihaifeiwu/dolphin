package com.freetmp.mbg.plugin.batch;

import com.freetmp.mbg.plugin.AbstractXmbgPlugin;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * 批量更新生成插件
 * @author Pin Liu
 */
public class BatchUpdatePlugin extends AbstractXmbgPlugin {

  public static final String BATCH_UPDATE = "batchUpdate";

  public static final String PROPERTY_PREFIX = "item.";

  @Override
  public boolean validate(List<String> warnings) {
    return true;
  }

  @Override
  public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
    Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();
    Method method = new Method(BATCH_UPDATE);
    FullyQualifiedJavaType type = new FullyQualifiedJavaType("java.util.List<" + introspectedTable.getTableConfiguration().getDomainObjectName() + ">");
    method.addParameter(new Parameter(type, "list"));
    method.setReturnType(FullyQualifiedJavaType.getIntInstance());
    importedTypes.add(type);
    interfaze.addMethod(method);
    interfaze.addImportedTypes(importedTypes);
    return true;
  }


  @Override
  public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

    XmlElement update = new XmlElement("update");
    update.addAttribute(new Attribute("id", BATCH_UPDATE));

    String parameterType = "java.util.List";

    update.addAttribute(new Attribute("parameterType", parameterType));

    XmlElement foreach = new XmlElement("foreach");
    foreach.addAttribute(new Attribute("collection", "list"));
    foreach.addAttribute(new Attribute("item", "item"));
    foreach.addAttribute(new Attribute("index", "index"));
    foreach.addAttribute(new Attribute("separator", ";"));

    generateTextBlockAppendTableName(" update ",introspectedTable,foreach);

    XmlElement dynamicElement = new XmlElement("set"); //$NON-NLS-1$
    generateParameterForSet(PROPERTY_PREFIX,true,introspectedTable.getNonPrimaryKeyColumns(),dynamicElement);

    foreach.addElement(dynamicElement);
    generateWhereConditions(PROPERTY_PREFIX,introspectedTable.getPrimaryKeyColumns(),foreach);

    update.addElement(foreach);

    document.getRootElement().addElement(update);

    return true;
  }


}
