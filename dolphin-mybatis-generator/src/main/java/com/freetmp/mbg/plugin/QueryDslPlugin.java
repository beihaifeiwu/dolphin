package com.freetmp.mbg.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

/**
 * 对QueryDsl提供支持的插件
 *
 * @author Pin Liu
 */
public class QueryDslPlugin extends PluginAdapter {

  public static final String QUERY_ENTITY_ANNOTATION = "@QueryEntity";

  public static final FullyQualifiedJavaType QUERY_ENTITY_ANNOTATION_TYPE = new FullyQualifiedJavaType("com.mysema.query.annotations.QueryEntity");

  @Override
  public boolean validate(List<String> warnings) {
    return true;
  }

  @Override
  public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
    addQueryEntityAnnotation(topLevelClass);
    return true;
  }

  @Override
  public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
    addQueryEntityAnnotation(topLevelClass);
    return true;
  }

  @Override
  public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
    addQueryEntityAnnotation(topLevelClass);
    return true;
  }

  public void addQueryEntityAnnotation(TopLevelClass topLevelClass) {
    topLevelClass.addAnnotation(QUERY_ENTITY_ANNOTATION);
    topLevelClass.addImportedType(QUERY_ENTITY_ANNOTATION_TYPE);
  }
}
