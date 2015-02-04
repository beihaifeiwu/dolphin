package com.freetmp.mbg.plugin.page;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * Created by LiuPin on 2015/2/4.
 */
public class DB2PaginationPlugin extends AbstractPaginationPlugin {

    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        XmlElement prefix = new XmlElement("if");
        prefix.addAttribute(new Attribute("test", "limit != null and limit>=0 and offset != null"));
        prefix.addElement(new TextElement("select * from (select tmp_page.*, rownumber() over() as row_id from ( "));
        element.addElement(0, prefix);
        
        XmlElement suffix = new XmlElement("if");
        suffix.addAttribute(new Attribute("test", "limit != null and limit>=0 and offset != null"));
        suffix.addElement(new TextElement(" ) as tmp_page ) where row_id between #{offset} and #{offset} + #{limit}"));
        element.addElement(suffix);
        return true;
    }
}
