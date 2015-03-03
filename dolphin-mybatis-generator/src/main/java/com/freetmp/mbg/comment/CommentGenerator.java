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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

/**
 * Created by LiuPin on 2015/2/14.
 */
public class CommentGenerator extends DefaultCommentGenerator {

    public static final String I18N_PATH_KEY = "i18n_path_key_for_CG";

    public static final String PROJECT_START_YEAR = "project_start_year_for_copyright";

    public static final String I18N_DEFAULT_PATH = "i18n_for_CG";
    public static final String PROJECT_START_DEFAULT_YEAR;

    static {
        PROJECT_START_DEFAULT_YEAR = "" + Calendar.getInstance().get(Calendar.YEAR);
    }

    protected ThreadLocal<XmlElement> rootElement = new ThreadLocal<>();

    protected boolean suppressAllComments;
    protected boolean suppressDate;

    protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    protected Resources comments;

    protected Resources copyrights;

    protected String startYear;
    protected String endYear;

    protected String i18nPath = I18N_DEFAULT_PATH;

    public CommentGenerator() {
        super();
    }

    private void initResources(){
        comments = new Resources(i18nPath + "/Comments",Locale.getDefault());
        copyrights = new Resources(i18nPath + "/Copyright",Locale.getDefault());
        endYear = "" + Calendar.getInstance().get(Calendar.YEAR);
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

        // 获取国际化资源的路径
        i18nPath = properties.getProperty(I18N_PATH_KEY, I18N_DEFAULT_PATH);

        // 获取项目开始时间，用在版权声明中
        String startYearStr = properties.getProperty(PROJECT_START_YEAR);
        if(StringUtils.isNotEmpty(startYearStr)){
            startYear = startYearStr;
        }else{
            startYear = PROJECT_START_DEFAULT_YEAR;
        }

        // 初始化资源
        initResources();
    }

    @Override
    public void addJavaFileComment(CompilationUnit compilationUnit) {

        String copyright = copyrights.getFormatted("JavaSource", startYear, endYear);
        if(StringUtils.isEmpty(copyright)) return;

        String[] array = copyright.split("\\|");

        for(String str : array){
            if(str.startsWith("*")){
                str = " " + str;
            }
            compilationUnit.addFileCommentLine(str);
        }
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

        String copyright = copyrights.getFormatted("XmlSource", startYear, endYear);
        if(StringUtils.isNotEmpty(copyright)) {
            String[] array = copyright.split("\\|");
            StringBuilder sb = new StringBuilder();
            for(String str : array){
                if(!str.startsWith("<!--") && !str.startsWith("-->")){
                    sb.append("    ");
                }
                sb.append(str);
                OutputUtilities.newLine(sb);
            }
            ed.setFileComments(sb.toString());
        }
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
        List<Element> elements = this.rootElement.get().getElements();
        int selfIndex = elements.indexOf(self);
        if(selfIndex != -1){
            // 使用块儿状注释，并限制每行的长度
            elements.add(selfIndex++, new TextElement(""));
            elements.add(selfIndex++, new TextElement("<!-- "));
            while (comment.length() > 80){
                String current = comment.substring(0,80);
                elements.add(selfIndex++, new TextElement("    " + current));
                comment = comment.substring(80);
            }
            elements.add(selfIndex++, new TextElement("-->"));

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

        String comment = comments.getString(id);
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
