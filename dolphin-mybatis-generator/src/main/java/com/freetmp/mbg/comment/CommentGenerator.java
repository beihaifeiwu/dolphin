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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

/*
 * Created by LiuPin on 2015/2/14.
 */
public class CommentGenerator extends DefaultCommentGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(CommentGenerator.class);

    public static final String XMBG_CG_I18N_PATH_KEY = "i18n_path_key_for_CG";

    public static final String XMBG_CG_PROJECT_START_YEAR = "project_start_year_for_copyright";

    public static final String XMBG_CG_I18N_LOCALE_KEY = "i18n_locale_key_for_CG";

    public static final String XMBG_CG_I18N_DEFAULT_PATH = "i18n_for_CG";
    public static final String XMBG_CG_PROJECT_START_DEFAULT_YEAR;

    static {
        XMBG_CG_PROJECT_START_DEFAULT_YEAR = "" + Calendar.getInstance().get(Calendar.YEAR);
    }

    protected ThreadLocal<XmlElement> rootElement = new ThreadLocal<>();

    protected boolean suppressAllComments;
    protected boolean suppressDate;

    protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    protected Resources comments;
    protected Resources defaultComments;

    protected Resources copyrights;
    protected Resources defaultCopyrights;

    protected String startYear;
    protected String endYear;

    protected String i18nPath = XMBG_CG_I18N_DEFAULT_PATH;

    public CommentGenerator() {
        super();
    }

    private void initResources(Locale locale) throws MalformedURLException {

        defaultComments = new Resources(XMBG_CG_I18N_DEFAULT_PATH + "/Comments",locale);
        defaultCopyrights = new Resources(XMBG_CG_I18N_DEFAULT_PATH + "/Copyrights",locale);

        ClassLoader loader = getClass().getClassLoader();

        // add user specified i18n sources directory to the classpath
        if(!i18nPath.equals(XMBG_CG_I18N_DEFAULT_PATH)) {
            URL[] urls = {new File(i18nPath).toURI().toURL()};
            loader = new URLClassLoader(urls);
            comments = new Resources("Comments",locale,loader);
            copyrights = new Resources("Copyrights",locale,loader);
        }else {
            comments = defaultComments;
            copyrights = defaultCopyrights;
        }

        endYear = "" + Calendar.getInstance().get(Calendar.YEAR);
    }

    /*
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

        if(suppressAllComments) return;

        // 获取国际化资源的路径
        i18nPath = properties.getProperty(XMBG_CG_I18N_PATH_KEY, XMBG_CG_I18N_DEFAULT_PATH);
        LOG.info("use the i18n resources under {}",i18nPath);

        // 获取项目开始时间，用在版权声明中
        String startYearStr = properties.getProperty(XMBG_CG_PROJECT_START_YEAR);
        if(StringUtils.isNotEmpty(startYearStr)){
            startYear = startYearStr;
        }else{
            startYear = XMBG_CG_PROJECT_START_DEFAULT_YEAR;
        }

        // 初始化资源
        String localeStr = properties.getProperty(XMBG_CG_I18N_LOCALE_KEY);
        Locale locale = Locale.getDefault();
        if(localeStr != null && StringUtils.isNoneEmpty(localeStr)) {
            String[] localeAras = localeStr.trim().split("_");
            locale = new Locale(localeAras[0], localeAras[1]);
        }
        LOG.info("use the locale {}",locale);
        try {
            initResources(locale);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addJavaFileComment(CompilationUnit compilationUnit) {

        // if user doesn't supplied the java source copyright then use the default
        String copyright = copyrights.getFormatted("JavaSource", startYear, endYear);
        if(StringUtils.isEmpty(copyright)){
            copyright = defaultCopyrights.getFormatted("JavaSource",startYear,endYear);
            if(StringUtils.isEmpty(copyright)) return;
        }

        String[] array = copyright.split("\\|");

        for(String str : array){
            if(str.startsWith("*")){
                str = " " + str;
            }
            compilationUnit.addFileCommentLine(str);
        }
    }

    /*
     * 添加新元素到子元素的最前面
     */
    public void addToFirstChildren(XmlElement parent,Element child){
        List<Element> elements = parent.getElements();
        elements.add(0,child);
    }

    /*
     * add the sql map file comment
     * @param document mbg dom
     */
    public void addSqlMapFileComment(Document document){

        if(suppressAllComments) return;

        ExtendedDocument ed = null;
        if(document instanceof ExtendedDocument) {
            ed = (ExtendedDocument) document;
        } else return;

        // if user doesn't supplied the xml source copyright then use the default
        String copyright = copyrights.getFormatted("XmlSource", startYear, endYear);
        if(StringUtils.isEmpty(copyright)){
            copyright = defaultCopyrights.getFormatted("XmlSource",startYear,endYear);
        }
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

    /*
     * 初始化XML文件的根节点
     */
    public void initRootElement(XmlElement rootElement){
        // just init the root element
        this.rootElement.set(rootElement);
    }

    /*
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
            // use block comments and limit the length of each line
            elements.add(selfIndex++, new TextElement(""));
            elements.add(selfIndex++, new TextElement("<!-- "));

            if(comment.length() < 80){
                elements.add(selfIndex++, new TextElement("    " + comment));
            }else {
                do {
                    String current = comment.substring(0, 80);
                    elements.add(selfIndex++, new TextElement("    " + current));
                    comment = comment.substring(80);
                } while (comment.length() > 80);
            }
            elements.add(selfIndex++, new TextElement("-->"));

        }
    }

    /*
     * Adds a suitable comment to warn users that the element was generated, and
     * when it was generated.
     */
    @Override
    public void addComment(XmlElement xmlElement) {

        if(suppressAllComments) return;

        String id = getID(xmlElement);

        // if user doesn't supply the specified comment the use the default
        String comment = comments.getString(id);
        if(StringUtils.isEmpty(comment)){
            comment = defaultComments.getString(id);
        }
        if(StringUtils.isNotEmpty(comment)) {
            addBeforeSelfInParent(xmlElement, comment);
        }
    }

    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        if(suppressAllComments) return;

        // it's not beauty to add some no use comment to the method
        if(false) {
            StringBuilder sb = new StringBuilder();

            method.addJavaDocLine("/*"); //$NON-NLS-1$
            sb.append(" * created by XMBG"); //$NON-NLS-1$
            if (!suppressDate) {
                sb.append(" on " + getDateString());
            } else {
                sb.append(".");
            }
            method.addJavaDocLine(sb.toString());
            method.addJavaDocLine(" */"); //$NON-NLS-1$
        }
    }

    /*
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
