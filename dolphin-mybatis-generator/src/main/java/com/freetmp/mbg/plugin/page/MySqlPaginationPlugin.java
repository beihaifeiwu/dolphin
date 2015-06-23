package com.freetmp.mbg.plugin.page;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/**
 * MySql数据库查询分页插件
 * @author Pin Liu
 */
public class MySqlPaginationPlugin extends AbstractPaginationPlugin {
	
	public MySqlPaginationPlugin() {
		super();
	}
	
	@Override
	public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(
			XmlElement element, IntrospectedTable introspectedTable) {

		XmlElement isNotNullElement = new XmlElement("if"); 
		isNotNullElement.addAttribute(new Attribute("test", "limit != null and limit>=0 and offset != null"));
		isNotNullElement.addElement(new TextElement("limit #{offset} , #{limit}"));

		element.addElement(isNotNullElement);
		return true;
	}
	
	/*
	 * This plugin is always valid - no properties are required
	 */
	public boolean validate(List<String> warnings) {
		return true;
	}

}