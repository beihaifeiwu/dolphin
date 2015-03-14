package com.freetmp.mbg.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/*
 * 存在即更新否则插入方法生成插件
 * @author Pin Liu
 */
public class UpsertPlugin extends PluginAdapter {
	
	public static final String UPSERT = "upsert";
	
	public static final String BATCH_UPSERT = "batchUpsert";
	
	public static final String IDENTIFIERS_ARRAY_WHERE = "Identifiers_Array_Where";
	
	public static final String PROPERTY_PREFIX = "record.";

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}

	@Override
	public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

		/*-----------------------------添加单个upsert的接口方法-----------------------------------*/
		Method upsert = new Method(UPSERT);
		upsert.setReturnType(FullyQualifiedJavaType.getIntInstance());
		
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();

        FullyQualifiedJavaType parameterType = introspectedTable.getRules().calculateAllFieldsClass();
        upsert.addParameter(new Parameter(parameterType, "record", "@Param(\"record\")"));
        importedTypes.add(parameterType);

        FullyQualifiedJavaType arrayType = new FullyQualifiedJavaType((new String[] {}).getClass().getCanonicalName());
        upsert.addParameter(new Parameter(arrayType, "array", "@Param(\"array\")"));
        importedTypes.add(arrayType);

        importedTypes.add(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param"));

        interfaze.addMethod(upsert);
		
		/*-----------------------------添加批量upsert的接口方法--------------------------------------*/
		Method batchUpsert = new Method(BATCH_UPSERT);
		batchUpsert.setReturnType(FullyQualifiedJavaType.getIntInstance());
		
		FullyQualifiedJavaType list = new FullyQualifiedJavaType("java.util.List<" + introspectedTable.getTableConfiguration().getDomainObjectName() + ">");
		batchUpsert.addParameter(new Parameter(list, "list","@Param(\"records\")"));
		importedTypes.add(list);
		
		batchUpsert.addParameter(new Parameter(arrayType, "array", "@Param(\"array\")"));
		
		interfaze.addMethod(batchUpsert);
		
        interfaze.addImportedTypes(importedTypes);
		return true;
	}

	@Override
	public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
		
		XmlElement sql = buildSqlClause(introspectedTable);
		document.getRootElement().addElement(sql);
		addSingleUpsertToSqlMap(document, introspectedTable);
		addBatchUpsertToSqlMap(document, introspectedTable);
		return true;
	}

	protected void addSingleUpsertToSqlMap(Document document, IntrospectedTable introspectedTable) {
		XmlElement update = new XmlElement("update");
		update.addAttribute(new Attribute("id", UPSERT));
		update.addAttribute(new Attribute("parameterType", "map"));
		
		generateSqlMapContent(introspectedTable, update);
        
		document.getRootElement().addElement(update);
	}
	
	protected void addBatchUpsertToSqlMap(Document document, IntrospectedTable introspectedTable){
		XmlElement update = new XmlElement("update");
		update.addAttribute(new Attribute("id", BATCH_UPSERT));
		update.addAttribute(new Attribute("parameterType", "map"));
		
		XmlElement foreach = new XmlElement("foreach");
        foreach.addAttribute(new Attribute("collection", "records"));
        foreach.addAttribute(new Attribute("item", "record"));
        foreach.addAttribute(new Attribute("index", "index"));
        foreach.addAttribute(new Attribute("separator", " ; "));
        
        generateSqlMapContent(introspectedTable, foreach);
        update.addElement(foreach);
        
		document.getRootElement().addElement(update);
	}

	/*
	 * 生成sqlMap里对应的xml元素
	 * @author Pin Liu
	 */
	protected void generateSqlMapContent(IntrospectedTable introspectedTable, XmlElement parent) {
		/*----------------------------update语句--------------------------------*/
        StringBuilder sb = new StringBuilder();
        sb.append("update ");
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        
        parent.addElement(new TextElement(sb.toString()));
        sb.setLength(0);
        
        XmlElement dynamicElement = new XmlElement("set");
        parent.addElement(dynamicElement);
        
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            XmlElement isNotNullElement = new XmlElement("if");
            sb.setLength(0);
            sb.append(introspectedColumn.getJavaProperty(PROPERTY_PREFIX));
            sb.append(" != null");
            isNotNullElement.addAttribute(new Attribute("test", sb.toString()));
            dynamicElement.addElement(isNotNullElement);

            sb.setLength(0);
            sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, PROPERTY_PREFIX));
            sb.append(',');

            isNotNullElement.addElement(new TextElement(sb.toString()));
        }
        
        XmlElement where = checkArrayWhere(introspectedTable);
        
        parent.addElement(where);
        
        /*--------------------------带exists判断的insert into语句-----------------------------------*/
        sb.setLength(0);
        sb.append("; insert into ");
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        sb.append("(");
        List<IntrospectedColumn> nonPkColumn = introspectedTable.getNonPrimaryKeyColumns();
        for(IntrospectedColumn introspectedColumn : nonPkColumn){
        	sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
            if(sb.length() > 80){
                parent.addElement(new TextElement(sb.toString()));
                sb.setLength(0);
                OutputUtilities.xmlIndent(sb,2);
            }
        	sb.append(",");
        }
        sb.setLength(sb.length()-1);
        sb.append(") ");
        
        parent.addElement(new TextElement(sb.toString()));
        sb.setLength(0);
        sb.append(" select ");
        for(IntrospectedColumn introspectedColumn : nonPkColumn){
        	sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, PROPERTY_PREFIX));
        	if(sb.length() > 80){
        		parent.addElement(new TextElement(sb.toString()));
        		sb.setLength(0);
        		OutputUtilities.xmlIndent(sb, 2);
        	}
        	sb.append(",");
        }
        sb.setLength(sb.length()-1);
        if(sb.length() > 0){
    		parent.addElement(new TextElement(sb.toString()));
    		sb.setLength(0);        	
        }
        
        sb.append(" where not exists (select 1 from ");
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        
        parent.addElement(new TextElement(sb.toString()));
        sb.setLength(0);
        
        where = checkArrayWhere(introspectedTable);
        parent.addElement(where);
        sb.append(" )");
        
        parent.addElement(new TextElement(sb.toString()));
		
	}

	/*
	 * 生成根据参数array判断where条件的元素
	 * @author Pin Liu
	 */
	protected XmlElement checkArrayWhere(IntrospectedTable introspectedTable) {
		XmlElement where = new XmlElement("where");
        
        XmlElement include = new XmlElement("include");
        include.addAttribute(new Attribute("refid",IDENTIFIERS_ARRAY_WHERE));
        
        where.addElement(include);
        
		return where;
	}

	/*
	 * 创建根据传入的Array数组进行判断的sql语句
	 * @author Pin Liu
	 */
	protected XmlElement buildSqlClause(IntrospectedTable introspectedTable) {
		
		XmlElement sql = new XmlElement("sql");
		sql.addAttribute(new Attribute("id", IDENTIFIERS_ARRAY_WHERE));
		
		XmlElement foreach = new XmlElement("foreach");
        foreach.addAttribute(new Attribute("collection", "array"));
        foreach.addAttribute(new Attribute("item", "item"));
        foreach.addAttribute(new Attribute("index", "index"));
        foreach.addAttribute(new Attribute("separator", " and "));
        
        StringBuilder sb = new StringBuilder();
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            XmlElement isEqualElement = new XmlElement("if");
            sb.setLength(0);
            sb.append("item == \'");
            sb.append(introspectedColumn.getJavaProperty());
            sb.append("\'");
            isEqualElement.addAttribute(new Attribute("test", sb.toString()));
            foreach.addElement(isEqualElement);

            sb.setLength(0);
            sb.append(MyBatis3FormattingUtilities.getAliasedEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, "record."));

            isEqualElement.addElement(new TextElement(sb.toString()));
        }
        
        sql.addElement(foreach);
        
        return sql;
	}

}
