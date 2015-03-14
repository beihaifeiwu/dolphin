package com.freetmp.mbg.plugin.page;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/*
 * Created by LiuPin on 2015/2/4.
 */
public class SQLServerPaginationPlugin extends AbstractPaginationPlugin {
    
    int findElementIndex(List<Element> elements, String name, String key, String value){
        int index = -1;
        for(int i = 0; i < elements.size(); i++){
            Element element = elements.get(i);
            if(element instanceof  XmlElement){
                XmlElement xe = (XmlElement) element;
                boolean match = false;
                if(name.trim().equals(xe.getName().trim())){
                    List<Attribute> attributes = xe.getAttributes();
                    for(Attribute attribute : attributes){
                        if(StringUtils.equals(attribute.getName().trim(),key.trim())
                                && StringUtils.equals(attribute.getValue().trim(), value.trim())){
                            match = true;
                            break;
                        }
                    }
                    if(match){
                        index = i; break;
                    }
                }
            }
        }
        return index;
    }
    
    Element findElement(List<Element> elements, String name, String key, String value){
        int index = findElementIndex(elements,name,key,value);
        Element element = elements.get(index);
        return element;
    }

    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        XmlElement prefix = new XmlElement("if");
        prefix.addAttribute(new Attribute("test", "limit != null and limit>=0 and offset != null"));
        prefix.addElement(new TextElement("select "));
        XmlElement baseColumn = new XmlElement("include");
        baseColumn.addAttribute(new Attribute("refid","Base_Column_List"));
        prefix.addElement(baseColumn);
        prefix.addElement(new TextElement(" from ( "));
        element.getElements().add(0, prefix);

        XmlElement inner = new XmlElement("if");
        inner.addAttribute(new Attribute("test", "limit != null and limit>=0 and offset != null"));
        
        int index = findElementIndex(element.getElements(),"include", "refid","Base_Column_List");
        
        inner.addElement(new TextElement(" , ROW_NUMBER() over ( "));
        Element orderBy = findElement(element.getElements(),"if","test","orderByClause != null");
        inner.addElement(orderBy);
        inner.addElement(new TextElement(" ) as rank "));
        element.addElement(index + 1, inner);
        
        XmlElement suffix = new XmlElement("if");
        suffix.addAttribute(new Attribute("test", "limit != null and limit>=0 and offset != null"));
        suffix.addElement(new TextElement(" ) as tmp where tmp.rank between #{offset} and #{limit} + #{offset}"));
        element.addElement(suffix);
        return true;
    }
}
