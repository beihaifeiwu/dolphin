package com.freetmp.mbg.plugin;

import com.freetmp.mbg.comment.CommentGenerator;
import com.freetmp.mbg.dom.ExtendedDocument;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.PropertyRegistry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by LiuPin on 2015/3/2.
 */
public class CommentsWavePlugin extends PluginAdapter {

  /**
   * This method is called after all the setXXX methods are called, but before
   * any other method is called. This allows the plugin to determine whether
   * it can run or not. For example, if the plugin requires certain properties
   * to be set, and the properties are not set, then the plugin is invalid and
   * will not run.
   *
   * @param warnings add strings to this list to specify warnings. For example, if
   *                 the plugin is invalid, you should specify why. Warnings are
   *                 reported to users after the completion of the run.
   * @return true if the plugin is in a valid state. Invalid plugins will not
   * be called
   */
  @Override
  public boolean validate(List<String> warnings) {
    return true;
  }

  @Override
  public void initialized(IntrospectedTable introspectedTable) {

  }

  @Override
  public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
    // set up root context for the comment generator
    CommentGenerator cg = null;
    if (context.getCommentGenerator() instanceof CommentGenerator) {
      cg = (CommentGenerator) context.getCommentGenerator();
      cg.initRootElement(document.getRootElement());
    }

    // add all the xml comments
    try {
      List<Element> elements = new ArrayList<>();
      for (Element element : document.getRootElement().getElements()) {
        elements.add(element);
      }
      for (Element element : elements) {
        if (element instanceof XmlElement)
          context.getCommentGenerator().addComment((XmlElement) element);
      }
    } finally {
      // clear the context for this generate
      if (cg != null) {
        cg.clearRootElement();
      }
    }

    return true;
  }

  @Override
  public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
    try {
      // use reflect to fix the root comment
      Document document = (Document) FieldUtils.readDeclaredField(sqlMap, "document", true);
      ExtendedDocument extendedDocument = new ExtendedDocument(document);
      FieldUtils.writeDeclaredField(sqlMap, "document", extendedDocument, true);
      if (context.getCommentGenerator() instanceof CommentGenerator) {
        CommentGenerator cg = (CommentGenerator) context.getCommentGenerator();
        cg.addSqlMapFileComment(extendedDocument);
      }
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return true;
  }

  @Override
  public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

    //System.out.println(topLevelClass.getFormattedContent());
    //System.out.println(context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING));
    return true;
  }


}
