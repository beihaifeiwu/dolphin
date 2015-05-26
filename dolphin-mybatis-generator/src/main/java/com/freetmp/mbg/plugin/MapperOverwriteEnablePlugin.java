package com.freetmp.mbg.plugin;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 修复MBG的mapper文件不能复写的错误
 *
 * @author Pin Liu
 */
public class MapperOverwriteEnablePlugin extends PluginAdapter {

  private Field isMergeable;

  @Override
  public boolean validate(List<String> warnings) {
    isMergeable = FieldUtils.getField(GeneratedXmlFile.class, "isMergeable", true);
    return true;
  }

  @Override
  public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
    try {
      isMergeable.set(sqlMap, false);
    } catch (IllegalArgumentException | IllegalAccessException e) {
      e.printStackTrace();
    }
    return true;
  }


}
