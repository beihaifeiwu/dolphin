package com.freetmp.mbg.comment;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.DefaultCommentGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

/**
 * Created by LiuPin on 2015/2/14.
 */
public class CommentGenerator extends DefaultCommentGenerator {

    protected XmlElement rootElement;

    protected boolean suppressAllComments;
    protected boolean suppressDate;

    public CommentGenerator() {
        super();
    }

    @Override
    public void addConfigurationProperties(Properties properties) {
        super.addConfigurationProperties(properties);

        // stolen from the parent
        suppressDate = isTrue(properties
                .getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_DATE));

        suppressAllComments = isTrue(properties
                .getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_ALL_COMMENTS));
    }

    @Override
    public void addJavaFileComment(CompilationUnit compilationUnit) {

        compilationUnit.addFileCommentLine(" Copyright 2014-2015 the original author or authors.");
        compilationUnit.addFileCommentLine("");
        compilationUnit.addFileCommentLine(" Licensed under the Apache License, Version 2.0 (the \"License\");");
        compilationUnit.addFileCommentLine(" you may not use this file except in compliance with the License.");
        compilationUnit.addFileCommentLine(" You may obtain a copy of the License at");
        compilationUnit.addFileCommentLine("");
        compilationUnit.addFileCommentLine("   http://www.apache.org/licenses/LICENSE-2.0");
        compilationUnit.addFileCommentLine("");
        compilationUnit.addFileCommentLine(" Unless required by applicable law or agreed to in writing, software");
        compilationUnit.addFileCommentLine(" distributed under the License is distributed on an \"AS IS\" BASIS,");
        compilationUnit.addFileCommentLine(" WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.");
        compilationUnit.addFileCommentLine(" See the License for the specific language governing permissions and");
        compilationUnit.addFileCommentLine(" limitations under the License.");
    }

    /**
     * 添加新元素到子元素的最前面
     * @param parent
     * @param child
     */
    public void addToFirstChildren(XmlElement parent,Element child){
        List<Element> elements = parent.getElements();
        elements.add(0,child);
    }

    @Override
    public void addRootComment(XmlElement rootElement) {

        if(suppressAllComments) return;

        List<Element> comments = new ArrayList<>();
        
        comments.add(new TextElement("<!--"));
        comments.add(new TextElement(" Copyright 2014-2015 the original author or authors."));
        comments.add(new TextElement(""));
        comments.add(new TextElement(" Licensed under the Apache License, Version 2.0 (the \"License\");"));
        comments.add(new TextElement(" you may not use this file except in compliance with the License."));
        comments.add(new TextElement(" You may obtain a copy of the License at"));
        comments.add(new TextElement(""));
        comments.add(new TextElement("   http://www.apache.org/licenses/LICENSE-2.0"));
        comments.add(new TextElement(""));
        comments.add(new TextElement(" Unless required by applicable law or agreed to in writing, software"));
        comments.add(new TextElement(" distributed under the License is distributed on an \"AS IS\" BASIS,"));
        comments.add(new TextElement(" WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied."));
        comments.add(new TextElement(" See the License for the specific language governing permissions and"));
        comments.add(new TextElement("-->"));

        // 倒序添加以保证注释顺序正确
        for(int index = comments.size() - 1; index >= 0; index--){
            addToFirstChildren(rootElement,comments.get(index));
        }

        this.rootElement = rootElement;
    }

    public String getID(XmlElement xmlElement){
        List<Attribute> attributes = xmlElement.getAttributes();
        for(Attribute attribute : attributes){
            if(attribute.getName().equalsIgnoreCase("id")){
                return attribute.getValue();
            }
        }
        return "";
    }

    public void addBeforeSelfInParent(XmlElement self,String comment){
        if(this.rootElement == null) return;
        int selfIndex = this.rootElement.getElements().indexOf(self);
        if(selfIndex != -1){
            this.rootElement.getElements().add(selfIndex,new TextElement(comment));
        }
    }

    /**
     * Adds a suitable comment to warn users that the element was generated, and
     * when it was generated.
     *
     * @param xmlElement
     */
    @Override
    public void addComment(XmlElement xmlElement) {

        if(suppressAllComments) return;

        String id = getID(xmlElement);
        switch (id){
            case "BaseResultMap":
                addBeforeSelfInParent(xmlElement,"the basic mapping of pojo fields and db table's columns");
                break;
            case "ResultMapWithBLOBs":
                addBeforeSelfInParent(xmlElement,"the mapping of pojo fields and db table's columns with type BLOB in it");
                break;
            case "Example_Where_Clause":
                addBeforeSelfInParent(xmlElement,"the where condition clause of the helper class example");
                break;
            case "Update_By_Example_Where_Clause":
                addBeforeSelfInParent(xmlElement, "the where condition for updating the db data using the example helper class");
                break;
            case "Base_Column_List":
                addBeforeSelfInParent(xmlElement,"the basic columns of db table used by select");
                break;
            case "Blob_Column_List":
                addBeforeSelfInParent(xmlElement,"the columns of db table used by select with type BLOB in it");
                break;
            case "selectAll":
                addBeforeSelfInParent(xmlElement,"select all the db data of the specific table");
                break;
            case "selectByExample":
                addBeforeSelfInParent(xmlElement,"select the db data of the specific table by the example condition");
                break;
            case "selectByExampleWithBLOBs":
                addBeforeSelfInParent(xmlElement,"select the db data of the specific table by the example condition with type BLOB in it");
                break;
            case "selectByPrimaryKey":
                addBeforeSelfInParent(xmlElement,"select the db data of the specific table by primary key");
                break;
            case "updateByExample":
                addBeforeSelfInParent(xmlElement,"update the db data with all fields by example condition");
                break;
            case "updateByExampleSelective":
                addBeforeSelfInParent(xmlElement,"update the db data with legal fields selected by example condition");
                break;
            case "updateByExampleWithBLOBs":
                addBeforeSelfInParent(xmlElement,"update the db data by example condition with Type BLOB in it");
                break;
            case "updateByPrimaryKey":
                addBeforeSelfInParent(xmlElement,"update the db data by table primary key");
                break;
            case "updateByPrimaryKeySelective":
                addBeforeSelfInParent(xmlElement,"update the db data with legal fields selected by table primary key");
                break;
            case "updateByPrimaryKeyWithBLOBs":
                addBeforeSelfInParent(xmlElement,"update the db data by table primary key with Type BLOB in it");
                break;
            case "insert":
                addBeforeSelfInParent(xmlElement,"insert the db data for all fields");
                break;
            case "insertSelective":
                addBeforeSelfInParent(xmlElement,"insert the db data with legal fields");
                break;
            case "deleteByExample":
                addBeforeSelfInParent(xmlElement,"delete the db data by example condition");
                break;
            case "deleteByPrimaryKey":
                addBeforeSelfInParent(xmlElement,"delete the db data by table primary key");
                break;
            case "countByExample":
                addBeforeSelfInParent(xmlElement,"count the db table rows by example condition");
                break;
            default:break;
        }
    }

    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        if(suppressAllComments) return;
        StringBuilder sb = new StringBuilder();

        method.addJavaDocLine("/**"); //$NON-NLS-1$
        sb.append(" * created by X MyBatis Generator"); //$NON-NLS-1$
        if(!suppressDate){
            sb.append(" on " + getDateString());
        }else {
            sb.append(".");
        }
        method.addJavaDocLine(sb.toString());
        method.addJavaDocLine(" */"); //$NON-NLS-1$
    }

    /**
     * This method adds the custom javadoc tag for. You may do nothing if you do
     * not wish to include the Javadoc tag - however, if you do not include the
     * Javadoc tag then the Java merge capability of the eclipse plugin will
     * break.
     *
     * @param javaElement       the java element
     * @param markAsDoNotDelete
     */
    @Override
    protected void addJavadocTag(JavaElement javaElement, boolean markAsDoNotDelete) {
        //TODO just leave it for now
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
        // no idea what to do
    }

    @Override
    public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {
        // no idea what to do
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        // no idea what to do
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
        // no idea what to do
    }

    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        //there is no need to add comments
    }

    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        //there is no need to add comments
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
        //TODO I have no idea what to say ?
    }
}
