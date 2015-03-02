package com.freetmp.mbg.comment;

import com.freetmp.mbg.dom.ExtendedDocument;
import com.freetmp.mbg.i18n.Resources;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.*;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.DefaultCommentGenerator;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

/**
 * Created by LiuPin on 2015/2/14.
 */
public class CommentGenerator extends DefaultCommentGenerator {

    protected ThreadLocal<XmlElement> rootElement = new ThreadLocal<>();

    protected boolean suppressAllComments;
    protected boolean suppressDate;

    protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    protected Resources resources;

    public CommentGenerator() {
        super();
        resources = new Resources("i18n/Comments",Locale.getDefault());
    }

    /**
     * This method returns a formated date string to include in the Javadoc tag
     * and XML comments. You may return null if you do not want the date in
     * these documentation elements.
     *
     * @return a string representing the current timestamp, or null
     */
    @Override
    protected String getDateString() {
        return sdf.format(new Date());
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

        compilationUnit.addFileCommentLine("/*");
        compilationUnit.addFileCommentLine(" * Copyright 2014-2015 the original author or authors.");
        compilationUnit.addFileCommentLine(" *");
        compilationUnit.addFileCommentLine(" * Licensed under the Apache License, Version 2.0 (the \"License\");");
        compilationUnit.addFileCommentLine(" * you may not use this file except in compliance with the License.");
        compilationUnit.addFileCommentLine(" * You may obtain a copy of the License at");
        compilationUnit.addFileCommentLine(" *");
        compilationUnit.addFileCommentLine(" *   http://www.apache.org/licenses/LICENSE-2.0");
        compilationUnit.addFileCommentLine(" *");
        compilationUnit.addFileCommentLine(" * Unless required by applicable law or agreed to in writing, software");
        compilationUnit.addFileCommentLine(" * distributed under the License is distributed on an \"AS IS\" BASIS,");
        compilationUnit.addFileCommentLine(" * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.");
        compilationUnit.addFileCommentLine(" * See the License for the specific language governing permissions and");
        compilationUnit.addFileCommentLine(" * limitations under the License.");
        compilationUnit.addFileCommentLine(" */");
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

    /**
     * add the sql map file comment
     * @param document
     */
    public void addSqlMapFileComment(Document document){

        if(suppressAllComments) return;

        ExtendedDocument ed = null;
        if(document instanceof ExtendedDocument) {
            ed = (ExtendedDocument) document;
        } else return;

        StringBuilder sb = new StringBuilder();
        OutputUtilities.newLine(sb);
        sb.append("<!--");
        OutputUtilities.newLine(sb);
        sb.append(" Copyright 2014-2015 the original author or authors.");
        OutputUtilities.newLine(sb);
        sb.append("");
        OutputUtilities.newLine(sb);
        sb.append(" Licensed under the Apache License, Version 2.0 (the \"License\");");
        OutputUtilities.newLine(sb);
        sb.append(" you may not use this file except in compliance with the License.");
        OutputUtilities.newLine(sb);
        sb.append(" You may obtain a copy of the License at");
        OutputUtilities.newLine(sb);
        OutputUtilities.newLine(sb);
        sb.append("   http://www.apache.org/licenses/LICENSE-2.0");
        OutputUtilities.newLine(sb);
        OutputUtilities.newLine(sb);
        sb.append(" Unless required by applicable law or agreed to in writing, software");
        OutputUtilities.newLine(sb);
        sb.append(" distributed under the License is distributed on an \"AS IS\" BASIS,");
        OutputUtilities.newLine(sb);
        sb.append(" WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.");
        OutputUtilities.newLine(sb);
        sb.append(" See the License for the specific language governing permissions and");
        OutputUtilities.newLine(sb);
        sb.append("-->");
        OutputUtilities.newLine(sb);

        ed.setFileComments(sb.toString());
    }

    /**
     * 初始化XML文件的根节点
     */
    public void initRootElement(XmlElement rootElement){
        // just init the root element
        this.rootElement.set(rootElement);
    }

    /**
     * 清除XML文件的根节点
     */
    public void clearRootElement(){
        this.rootElement.remove();
    }

    @Override
    public void addRootComment(XmlElement rootElement) {
        // nothing have to do
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
        if(this.rootElement.get() == null) return;
        int selfIndex = this.rootElement.get().getElements().indexOf(self);
        if(selfIndex != -1){
            this.rootElement.get().getElements().add(selfIndex, new TextElement(""));
            this.rootElement.get().getElements().add(selfIndex + 1,new TextElement("<!-- " + comment + " -->"));
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

        String comment = resources.getString(id);
        if(StringUtils.isNotEmpty(comment));
        addBeforeSelfInParent(xmlElement,comment);
    }

    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        if(suppressAllComments) return;
        StringBuilder sb = new StringBuilder();

        method.addJavaDocLine("/**"); //$NON-NLS-1$
        sb.append(" * created by XMBG"); //$NON-NLS-1$
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
