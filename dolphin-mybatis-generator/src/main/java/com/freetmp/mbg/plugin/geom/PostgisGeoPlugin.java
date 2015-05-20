package com.freetmp.mbg.plugin.geom;

import com.freetmp.mbg.plugin.upsert.AbstractUpsertPlugin;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.xml.*;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.internal.db.ConnectionFactory;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
 * Postgis的地理信息插件
 * @author Pin Liu
 */
public class PostgisGeoPlugin extends PluginAdapter {
    
    public static final String SRID_NAME = "srid";
	
    public static final String lineSeparator;

    static {
        String ls = System.getProperty("line.separator"); //$NON-NLS-1$
        if (ls == null) {
            ls = "\n"; //$NON-NLS-1$
        }
        lineSeparator = ls;
    }

	Connection connection;
	
	Field elementsList;
	
	String srid;
	
	@Override
	public boolean validate(List<String> warnings) {
		boolean valid = true;
		srid = properties.getProperty(SRID_NAME);
		if(StringUtils.isEmpty(srid)){
			srid = "3857";
		}
		if(connection == null){
	        try {
				connection = ConnectionFactory.getInstance().getConnection(context.getJdbcConnectionConfiguration());
			} catch (SQLException e) {
				e.printStackTrace();
				valid = false;
			}	
		}
		elementsList = FieldUtils.getField(XmlElement.class, "elements", true);
		return valid;
	}
	
	/*
	 * 在初始化阶段，检查所有字段的JdbcType为OTHER的字段，获取其类型名称（TYPE_NAME）
	 * 根据其类型名称确定其实际的地理信息类型
	 */
	@Override
	public void initialized(IntrospectedTable introspectedTable) {
		for(IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()){
			if(introspectedColumn.getJdbcType() == Types.OTHER){
				String typeName = fetchTypeName(introspectedColumn);
				//System.out.println("postgis type : "+typeName);
				switch(typeName.toLowerCase()){
				case "geometry":{
					introspectedColumn.setFullyQualifiedJavaType(new FullyQualifiedJavaType("org.geolatte.geom.Geometry"));
					//introspectedColumn.setJdbcTypeName(typeName.toUpperCase());
					break;
				}
				}
			}
		}
	}
	
	
	@Override
	public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
		
		List<IntrospectedColumn> columns = new ArrayList<IntrospectedColumn>();
		
		for(IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()){
			if(introspectedColumn.getFullyQualifiedJavaType().getPackageName().equalsIgnoreCase("org.geolatte.geom")){
				columns.add(introspectedColumn);
			}
		}
		
		if(columns.isEmpty()) return true;
		
		XmlElement xmlElement = document.getRootElement();
		for(Element element : xmlElement.getElements()){
			if(element instanceof XmlElement){
				XmlElement xe = (XmlElement) element;
				switch (xe.getName().toLowerCase()) {
				case "sql":
					if(containsAttribute(xe, "id", introspectedTable.getBaseColumnListId())){
						checkAndReplaceOutput(columns,xe);						
					}else if(containsAttribute(xe, "id", AbstractUpsertPlugin.IDENTIFIERS_ARRAY_CONDITIONS)){
						checkAndReplaceInput(columns, xe);
					}
					break;
				case "insert":
				case "update":
					checkAndReplaceInput(columns, xe);
					break;
				default:
					break;
				}			
			}
		}
		return true;
	}
	
	/*
	 * 检查并替换输入的地理信息相关参数，使用PostGis提供的ST_GeomFromText函数
	 * @author Pin Liu
	 */
	protected void checkAndReplaceInput(List<IntrospectedColumn> columns, XmlElement xe){
	  for(Element element : xe.getElements()){
	    if(element instanceof XmlElement){
	      checkAndReplaceInput(columns, (XmlElement) element);
	    }
	    if(element instanceof TextElement){
	      TextElement te = (TextElement) element;
    		checkAndReplaceInput(columns, te);
	    }
	  }
	}

  protected void checkAndReplaceInput(List<IntrospectedColumn> columns, TextElement te) {
    String sql = te.getContent();
    for(IntrospectedColumn column : columns){
    	if(column.getFullyQualifiedJavaType().getShortName().equals("Geometry")){
    		String paramStr = MyBatis3FormattingUtilities.getParameterClause(column);
    		sql = StringUtils.replace(sql, paramStr, "ST_GeomFromText(" + paramStr + ","+srid+")"); //replace no prefix geo relate column
    		paramStr = MyBatis3FormattingUtilities.getParameterClause(column, "record.");
    		sql = StringUtils.replace(sql, paramStr, "ST_GeomFromText(" + paramStr + ","+srid+")"); //replace mbg generate prefix geo relate column
    		paramStr = MyBatis3FormattingUtilities.getParameterClause(column, "item.");
    		sql = StringUtils.replace(sql, paramStr, "ST_GeomFromText(" + paramStr + ","+srid+")"); //replace mbg batch plugin generate prefix geo relate column				
  //				System.out.println();
  //				System.out.println(sql);
    	}
    }
    try {
      FieldUtils.writeDeclaredField(te, "content", sql, true);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }
	
	/*
	 * 检查并替换输出的地理信息相关参数，使用PostGis提供的ST_AsText函数
	 * @author Pin Liu
	 */
	protected void checkAndReplaceOutput(List<IntrospectedColumn> columns, XmlElement xe) {
	  for(Element element : xe.getElements()){
	    if(element instanceof XmlElement){
	      checkAndReplaceOutput(columns, (XmlElement) element);
	    }
	    if(element instanceof TextElement){
    	  TextElement te = (TextElement) element;
    		checkAndReplaceOutput(columns, te);
	    }
	  }
	}

  protected void checkAndReplaceOutput(List<IntrospectedColumn> columns, TextElement te) {
    String sql = te.getContent();
		for(IntrospectedColumn column : columns){
			if(column.getFullyQualifiedJavaType().getShortName().equals("Geometry")){
				String columnStr = null;
				if(column.isColumnNameDelimited()){
					columnStr = "\""+column.getActualColumnName()+"\"";
				}else{
					columnStr = column.getActualColumnName();
				}
				sql = StringUtils.replaceOnce(sql, columnStr, "ST_AsText("+columnStr+") as " + columnStr);
				//sql = sql.replace(column.getActualColumnName(), "ST_AsText("+column.getActualColumnName()+")");
//				System.out.println();
//				System.out.println(sql);
			}
		}
    try {
      FieldUtils.writeDeclaredField(te, "content", sql, true);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }		
  }

	/*
	 * 使用新的sql语句替换原来的xml内容
	 * @author Pin Liu
	 */
	protected void replaceOriginChildElements(XmlElement xe, String sql) {
		sql = sql.trim();
		String[] lines = sql.split(lineSeparator);
		List<Element> elements = Lists.newArrayList();
		for(String line : lines){
			elements.add(new TextElement(line));
		}
		try {
			elementsList.set(xe, elements);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 获取xml标签内部的内容
	 * @author Pin Liu
	 */
	protected String getContentWithoutOuterTags(XmlElement xe){
		int indentLevel = 0;
		StringBuilder sb = new StringBuilder();
		Iterator<Element> iter = xe.getElements().iterator();
		while(iter.hasNext()){
			sb.append(iter.next().getFormattedContent(indentLevel));
			if(iter.hasNext()){
	            OutputUtilities.newLine(sb);				
			}
		}
        return sb.toString();
	}

	/*
	 * 检查xml标签元素是否包含指定属性
	 * @author Pin Liu
	 */
	public boolean containsAttribute(XmlElement xe,String key, String value){
		if(xe.getAttributes() != null){
			for(Attribute attribute : xe.getAttributes()){
				if(attribute.getName().equalsIgnoreCase(key) && attribute.getValue().equalsIgnoreCase(value)){
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * 根据MBG探测到的数据库表的元数据获取字段的类别名称
	 * @author Pin Liu
	 */
	public String fetchTypeName(IntrospectedColumn introspectedColumn){
		
		String columnName = introspectedColumn.getActualColumnName();
		String catalog = introspectedColumn.getIntrospectedTable().getTableConfiguration().getCatalog();
		String schema = introspectedColumn.getIntrospectedTable().getTableConfiguration().getSchema();
		String table = introspectedColumn.getIntrospectedTable().getTableConfiguration().getTableName();
		String dataType = null;
		try {
			ResultSet set = connection.getMetaData().getColumns(catalog, schema, table, columnName);
			while(set.next()){
				dataType = set.getString("TYPE_NAME");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return dataType;
	}

}
