package com.freetmp.mbg.plugin.batch;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.util.List;

/**
 * 批量更新生成插件
 * @author Pin Liu
 */
public class BatchUpdatePlugin extends PluginAdapter {
	
	public static final String BATCH_UPDATE = "batchUpdate";
	
	public static final String PROPERTY_PREFIX = "item.";

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}

	@Override
	public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		String objectName = introspectedTable.getTableConfiguration().getDomainObjectName();
		Method method = new Method(BATCH_UPDATE);
		FullyQualifiedJavaType type = new FullyQualifiedJavaType("java.util.List<"+objectName+">");
		method.addParameter(new Parameter(type, "list"));
		method.setReturnType(FullyQualifiedJavaType.getIntInstance());
		interfaze.addMethod(method);		
		return true;
	}


	@Override
	public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
		
        XmlElement update = new XmlElement("update"); 
        update.addAttribute(new Attribute("id", BATCH_UPDATE)); 

        String parameterType = "java.util.List";

        update.addAttribute(new Attribute("parameterType",parameterType));

        context.getCommentGenerator().addComment(update);
        
        XmlElement foreach = new XmlElement("foreach");
        foreach.addAttribute(new Attribute("collection", "list"));
        foreach.addAttribute(new Attribute("item", "item"));
        foreach.addAttribute(new Attribute("index", "index"));
        foreach.addAttribute(new Attribute("separator", ";"));

        StringBuilder sb = new StringBuilder();

        sb.append("update "); //$NON-NLS-1$
        sb.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
        foreach.addElement(new TextElement(sb.toString()));

        XmlElement dynamicElement = new XmlElement("set"); //$NON-NLS-1$
        foreach.addElement(dynamicElement);

        for (IntrospectedColumn introspectedColumn : introspectedTable.getNonPrimaryKeyColumns()) {
            XmlElement isNotNullElement = new XmlElement("if"); 
            sb.setLength(0);
            sb.append(introspectedColumn.getJavaProperty(PROPERTY_PREFIX));
            sb.append(" != null");
            isNotNullElement.addAttribute(new Attribute("test", sb.toString())); //$NON-NLS-1$
            dynamicElement.addElement(isNotNullElement);

            sb.setLength(0);
            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn,PROPERTY_PREFIX));
            sb.append(',');

            isNotNullElement.addElement(new TextElement(sb.toString()));
        }

        boolean and = false;
        for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
            sb.setLength(0);
            if (and) {
                sb.append("  and ");
            } else {
                sb.append("where "); 
                and = true;
            }

            sb.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn,PROPERTY_PREFIX));
            foreach.addElement(new TextElement(sb.toString()));
        }
        
        update.addElement(foreach);

        document.getRootElement().addElement(update);
		
        return true;
	}
	
	

}
