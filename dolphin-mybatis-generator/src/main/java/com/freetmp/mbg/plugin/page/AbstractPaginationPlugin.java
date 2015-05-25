package com.freetmp.mbg.plugin.page;

import com.freetmp.mbg.plugin.AbstractXmbgPlugin;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;

import java.util.ArrayList;
import java.util.List;

/**
 * MBG分页插件的抽象基类，提供统一的分页属性及分页操作
 * 使用统一的数据模型：offset 当前页离开始记录的偏移，limit 当前页的记录数限制
 * Created by LiuPin on 2015/1/30.
 */
public abstract class AbstractPaginationPlugin extends AbstractXmbgPlugin {

  public static final String LIMIT_NAME = "limit";
  public static final String OFFSET_NAME = "offset";

  public static final String BOUND_BUILDER_NAME = "BoundBuilder";

  @Override
  public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
    List<Field> fields = new ArrayList<>();

    // add field, getter, setter for limit clause
    Field field = generateFieldDeclarationWithGetterSetterFor(LIMIT_NAME, new FullyQualifiedJavaType(Integer.class.getCanonicalName()), topLevelClass);
    fields.add(field);
    field = generateFieldDeclarationWithGetterSetterFor(OFFSET_NAME, new FullyQualifiedJavaType(Integer.class.getCanonicalName()), topLevelClass);
    fields.add(field);

    generateBuilderFor(BOUND_BUILDER_NAME, topLevelClass, fields);
    return true;
  }

  /**
   * This plugin is always valid - no properties are required
   */
  public boolean validate(List<String> warnings) {
    return true;
  }
}
