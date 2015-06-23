package com.freetmp.mbg.plugin;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.exception.ShellException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * Created by pin on 2015/2/10.
 */
public class XMLMergePlugin extends PluginAdapter {

  static final Logger log = LoggerFactory.getLogger(XMLMergePlugin.class);

  public static final String ROOTDIR_NAME = "rootDir";

  private String rootDir = ".";

  public boolean validate(List<String> warnings) {
    String prop = properties.getProperty(ROOTDIR_NAME);

    boolean valid = stringHasValue(prop);
    if (valid) {
      rootDir = prop;
    } else {
      warnings.add(getString("ValidationError.18", "XMLMergePlugin", "rootDir"));
    }

    return true;
  }

  /**
   * 从DefaultShellCallback中借用的解析文件夹的函数
   *
   * @param targetProject target project
   * @param targetPackage target package
   * @return file instance
   * @throws ShellException Cannot get infos form environment
   */
  public File getDirectory(String targetProject, String targetPackage)
      throws ShellException {
    // targetProject is interpreted as a directory that must exist
    //
    // targetPackage is interpreted as a sub directory, but in package
    // format (with dots instead of slashes). The sub directory will be
    // created
    // if it does not already exist

    File project = new File(targetProject);
    if (!project.isDirectory()) {
      throw new ShellException(getString("Warning.9", //$NON-NLS-1$
          targetProject));
    }

    StringBuilder sb = new StringBuilder();
    StringTokenizer st = new StringTokenizer(targetPackage, "."); //$NON-NLS-1$
    while (st.hasMoreTokens()) {
      sb.append(st.nextToken());
      sb.append(File.separatorChar);
    }

    File directory = new File(project, sb.toString());
    if (!directory.isDirectory()) {
      boolean rc = directory.mkdirs();
      if (!rc) {
        throw new ShellException(getString("Warning.10", //$NON-NLS-1$
            directory.getAbsolutePath()));
      }
    }

    return directory;
  }

  /**
   * 根据xml文件所属的包名获取相应的文件
   *
   * @param packiage package declaration
   * @param fileName target file name
   * @throws ShellException Cannot get infos form environment
   */
  private File getTargetFile(String packiage, String fileName) throws ShellException {
    File root = new File(this.rootDir);
    if (!root.exists()) {
      root.mkdirs();
    }
    File directory = getDirectory(rootDir, packiage);
    File targetFile = new File(directory, fileName);
    return targetFile;
  }

  @Override
  public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
    SqlMapGeneratorConfiguration smgc = context.getSqlMapGeneratorConfiguration();
    try {
      Document document = (Document) FieldUtils.readDeclaredField(sqlMap, "document", true);
      File targetFile = getTargetFile(smgc.getTargetPackage(), sqlMap.getFileName());
      if (!targetFile.exists()) { // 第一次生成直接使用当前生成的文件
        return true;
      }
      visitAndMerge(document, targetFile);
    } catch (ShellException | IOException | IllegalAccessException | DocumentException e) {
      e.printStackTrace();
    }
    return true;
  }

  /**
   * 访问并合并mapper的xml文件
   *
   * @param document   generate xml dom tree
   * @param targetFile the file for writing xml content
   * @throws java.io.IOException
   * @throws org.dom4j.DocumentException
   */
  private void visitAndMerge(final Document document, File targetFile) throws IOException, DocumentException {
    SAXReader reader = new SAXReader();
    reader.setValidation(false);
    org.dom4j.Document doc = reader.read(targetFile);
    final org.dom4j.Element rootElement = doc.getRootElement();
    rootElement.accept(new VisitorSupport() {

      @Override
      public void visit(org.dom4j.Element node) {
        //根节点的直属子节点
        if (node.getParent() == rootElement) {
          XmlElement xe = findMatchedElementIn(document, node);
          if (xe == null) { // 新增节点 添加到document中
            int index = node.getParent().elements().indexOf(node);
            xe = transformElement(node);
            log.info("XmlElement Add ---> " + xe.getName() + " id=" + idValue(node));
            document.getRootElement().getElements().add(index, xe);
          } else { // 合并已经存在的节点
            XmlElement nxe = transformElement(node);
            mergeExistedElement(nxe, xe);
            log.info("XmlElement Merge ---> " + xe.getName() + " id=" + idValue(node));
          }
        }
      }

      @Override
      public void visit(Comment node) {
        // mbg 暂不支持xml doc注释节点添加
      }

    });
  }

  /**
   * 合并已经存在的Xml元素
   *
   * @param src  The source xml element for merging
   * @param dest The dest xml element for merging
   */
  protected void mergeExistedElement(XmlElement src, XmlElement dest) {

    // 合并属性
    List<Attribute> srcAttributes = src.getAttributes();
    List<Attribute> destAttributes = dest.getAttributes();
    for (Attribute srcAttr : srcAttributes) {
      Attribute matched = null;
      for (Attribute destAttr : destAttributes) {
        if (StringUtils.equals(srcAttr.getName(), destAttr.getName()) &&
            StringUtils.equals(srcAttr.getValue(), destAttr.getValue())) {
          matched = destAttr;
        }
      }
      // 不存在则添加到目标元素的属性列表中
      if (matched == null) {
        destAttributes.add(srcAttributes.indexOf(srcAttr), srcAttr);
      }
    }

    // 重组子节点
    // reformationTheElementChilds(src);
    // reformationTheElementChilds(dest);

    // 暂时不做处理 ---留待后续添加
  }

  /**
   * 重组XML元素的亲子节点,合并相邻的文本节点
   *
   * @param xe The element whose content will be reformatted
   */
  protected void reformationTheElementChilds(XmlElement xe) {
    List<Element> reformationList = new ArrayList<>();
    for (Element element : xe.getElements()) {
      // 如果是XML元素节点，则直接添加
      if (element instanceof XmlElement) {
        reformationList.add(element);
      }
      if (element instanceof TextElement) {
        int lastIndex = reformationList.size() - 1;
        TextElement te = (TextElement) element;

        // 如果当前文本节点之前的一个节点也是文本节点时，将两个文本节点合并成一个替换之前的节点
        // 否则直接添加
        if (!reformationList.isEmpty() && reformationList.get(lastIndex) instanceof TextElement) {
          te = (TextElement) reformationList.get(lastIndex);
          StringBuilder sb = new StringBuilder();
          sb.append(te.getContent()).append(((TextElement) element).getContent());
          te = new TextElement(sb.toString());
          reformationList.remove(lastIndex);
        }

        reformationList.add(te);
      }
    }
    // 清空原有的子元素列表，并用重组后的子节点列表填充
    xe.getElements().clear();
    xe.getElements().addAll(reformationList);
  }

  /**
   * 转换dom4j的element元素，生成mbg的XmlElement元素
   *
   * @param node The dom4j node to be transform
   * @return The transform result
   */
  protected XmlElement transformElement(org.dom4j.Element node) {
    XmlElement xe = new XmlElement(node.getName());

    // 设置元素的属性
    @SuppressWarnings("unchecked")
    Iterator<org.dom4j.Attribute> iterator = node.attributeIterator();
    while (iterator.hasNext()) {
      org.dom4j.Attribute ab = iterator.next();
      xe.addAttribute(new Attribute(ab.getName(), ab.getValue()));
    }
    // 深度优先遍历子节点
    @SuppressWarnings("unchecked")
    Iterator<org.dom4j.Node> niter = node.nodeIterator();
    while (niter.hasNext()) {
      org.dom4j.Node n = niter.next();
      // 文本节点
      if (n.getNodeType() == org.dom4j.Node.TEXT_NODE) {
        Text text = (Text) n;
        TextElement te = new TextElement(text.getText().trim());
        xe.addElement(te);
      }
      // 元素节点
      if (n.getNodeType() == org.dom4j.Node.ELEMENT_NODE) {
        xe.addElement(transformElement((org.dom4j.Element) n));
      }
      // 注释节点
      if (n.getNodeType() == org.dom4j.Node.COMMENT_NODE) {
        TextElement te = new TextElement(n.asXML().trim());
        xe.addElement(te);
      }
      // CDATA 节点
      if (n.getNodeType() == org.dom4j.Node.CDATA_SECTION_NODE) {
        TextElement te = new TextElement(n.asXML().trim());
        xe.addElement(te);
      }
    }
    return xe;
  }

  /**
   * 从MBG生成的DOM文档结构中找到与element代表同一节点的元素对象
   *
   * @param document generate xml dom tree
   * @param element  The dom4j element
   * @return The xml element correspond to dom4j element
   */
  protected XmlElement findMatchedElementIn(Document document, org.dom4j.Element element) {
    org.dom4j.Attribute id = element.attribute("id");
    String idName = id.getName();
    String idValue = id.getValue();
    for (Element me : document.getRootElement().getElements()) {
      if (me instanceof XmlElement) {
        XmlElement xe = (XmlElement) me;
        for (Attribute ab : xe.getAttributes()) {
          if (StringUtils.equals(idName, ab.getName()) && StringUtils.equals(idValue, ab.getValue())) {
            return xe;
          }
        }
      }
    }
    return null;
  }

  protected String idValue(org.dom4j.Element element) {
    org.dom4j.Attribute id = element.attribute("id");
    if (id != null) {
      return id.getValue();
    } else {
      return "";
    }
  }
}
