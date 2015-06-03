package com.freetmp.mbg.plugin;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;

import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.*;
import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * 数据库表列名转换插件,根据指定的正则表达式从列名中提取单词元组
 * 生成符合Java命名规范的驼峰命名格式
 *
 * @author Pin Liu
 */
public class ColumnNameConversionPlugin extends PluginAdapter {

  public static final String COLUMN_PATTERN_NAME = "columnPattern";

  private Pattern columnPattern; //匹配数据库列名的模式

  private Field baseColumnsField;

  private Field blobColumnsField;

  @Override
  public boolean validate(List<String> warnings) {

    String column = properties.getProperty(COLUMN_PATTERN_NAME);

    boolean valid = stringHasValue(column);
    //System.out.println(column);
    if (valid) {
      columnPattern = Pattern.compile(column);
    } else {
      warnings.add(getString("ValidationError.18", "ColumnNameConversionPlugin", "columnPattern"));
    }

    return valid;
  }

  @Override
  public void initialized(IntrospectedTable introspectedTable) {
    //print("before calculate",introspectedTable.getAllColumns());
    convertUseReflect(introspectedTable);
    //print("after calculate",introspectedTable.getAllColumns());
  }

  @SuppressWarnings("unchecked")
  protected void convertUseReflect(IntrospectedTable introspectedTable) {
    if (baseColumnsField == null || blobColumnsField == null) {
      baseColumnsField = FieldUtils.getField(IntrospectedTable.class, "baseColumns", true);
      blobColumnsField = FieldUtils.getField(IntrospectedTable.class, "blobColumns", true);
    }
    List<IntrospectedColumn> introspectedColumns = null;
    try {
      introspectedColumns = (List<IntrospectedColumn>) baseColumnsField.get(introspectedTable);
      convertForAll(introspectedColumns);
      //print("after calculate for base", introspectedColumns);
      baseColumnsField.set(introspectedTable, introspectedColumns);
      introspectedColumns = (List<IntrospectedColumn>) blobColumnsField.get(introspectedTable);
      convertForAll(introspectedColumns);
      //print("after calculate for blob", introspectedColumns);
      blobColumnsField.set(introspectedTable, introspectedColumns);

    } catch (IllegalArgumentException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  protected void convertUseApi(IntrospectedTable introspectedTable) {
    convertForAll(introspectedTable.getAllColumns());
  }

  protected void convertForAll(List<IntrospectedColumn> introspectedColumns) {
    if (introspectedColumns != null) {
      for (IntrospectedColumn introspectedColumn : introspectedColumns) {
        //System.out.println("convert for column " + introspectedColumn + ", JDBC Type Name:"+introspectedColumn.getJdbcTypeName());
        introspectedColumn.setJavaProperty(convert(introspectedColumn.getActualColumnName()));
      }
    }
  }


  public void print(String title, List<IntrospectedColumn> introspectedColumns) {
    System.out.println("*******" + title + "********");
    for (IntrospectedColumn column : introspectedColumns) {
      System.out.println("ColumnName:" + column.getActualColumnName() + ", PropertyName:" + column.getJavaProperty());
    }
  }


  public String convert(String actualColumnName) {
    //System.out.println(actualColumnName);
    Matcher matcher = columnPattern.matcher(actualColumnName);
    StringBuilder sb = new StringBuilder();
    while (matcher.find()) {
      String word = matcher.group();
      word = removePattern(word, "[^a-zA-Z]"); //去除word中所有非字母字符
      sb.append(capitalize(word.toLowerCase()));
    }
    String result = sb.toString();
    //System.out.println(result);
    if (isAllUpperCase(result)) { //实际列名全为大写时则全部改为小写
      result = result.toLowerCase();
    } else {
      result = uncapitalize(result);
    }
    //System.out.println(result);
    return result;
  }

  public Pattern getColumnPattern() {
    return columnPattern;
  }

  public void setColumnPattern(Pattern columnPattern) {
    this.columnPattern = columnPattern;
  }

}
