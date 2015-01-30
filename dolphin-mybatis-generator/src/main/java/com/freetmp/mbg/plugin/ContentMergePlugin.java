package com.freetmp.mbg.plugin;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.dom4j.Comment;
import org.dom4j.DocumentException;
import org.dom4j.Text;
import org.dom4j.VisitorSupport;
import org.dom4j.io.SAXReader;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.PrimitiveType.Code;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.*;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * 内容合并插件，合并以前和当前生成的文件，只增加和更新，不删除
 * @author Pin Liu
 * @编写日期 2014年12月15日下午3:59:04
 */
public class ContentMergePlugin extends PluginAdapter {
  
  static Logger log = LoggerFactory.getLogger(ContentMergePlugin.class);
  
  private DefaultShellCallback callback = new DefaultShellCallback(true);
  
  private String rootDir = ".";

  @Override
  public boolean validate(List<String> warnings) {
    String prop = properties.getProperty("rootDir");
    
    boolean valid = stringHasValue(prop);
    if(valid){
      rootDir = prop;
    }else{
      warnings.add(getString("ValidationError.18", "ContentMergePlugin", "rootDir"));
    }
    
    return true;
  }

  /**
   * 解析已有的java文件代码
   * @author Pin Liu
   * @编写日期: 2014年12月15日下午4:40:59
   * @param targetFile
   * @return
   * @throws java.io.IOException
   */
  public CompilationUnit generateASTfromTheFile(File targetFile) throws IOException{
    String content = FileUtils.readFileToString(targetFile);
    ASTParser parser = ASTParser.newParser(AST.JLS8);
    parser.setKind(ASTParser.K_COMPILATION_UNIT);
    parser.setSource(content.toCharArray());
    CompilationUnit node = (CompilationUnit)parser.createAST(null);
    return node;
  }

  /**
   * 两个不同体系的类型是否相等
   * @author Pin Liu
   * @编写日期: 2014年12月15日下午5:44:52
   * @param type
   * @param fType
   * @return
   * @throws IllegalAccessException
   */
  public boolean isTypeEquals(CompilationUnit unit,Type type, FullyQualifiedJavaType fType) throws IllegalAccessException{

    if(unit == null || type == null || fType == null) return false;

    String fullQualifiedType = generateFullyQualifiedString(type, unit);

    String ffullQualifiedType = fType.getFullyQualifiedName();

    // 修正引用java.lang包中的类时AST与MBG的不一致
    if(StringUtils.contains(ffullQualifiedType, "java.lang.")){
      fullQualifiedType = StringUtils.replace(fullQualifiedType, "java.lang.", "");
      ffullQualifiedType = StringUtils.replace(ffullQualifiedType, "java.lang.", "");
    }

    // 修正对MBG的mapper系列接口的引用
    JavaClientGeneratorConfiguration jcgc = context.getJavaClientGeneratorConfiguration();
    if(isXorContains(fullQualifiedType, ffullQualifiedType, jcgc.getTargetPackage())){
      fullQualifiedType = StringUtils.replace(fullQualifiedType, jcgc.getTargetPackage() + ".", "");
      ffullQualifiedType = StringUtils.replace(ffullQualifiedType, jcgc.getTargetPackage() + ".", "");
    }

    // 修正对MBG的model系列类的引用
    JavaModelGeneratorConfiguration jmgc = context.getJavaModelGeneratorConfiguration();
    if(isXorContains(fullQualifiedType, ffullQualifiedType, jmgc.getTargetPackage())){
      fullQualifiedType = StringUtils.replace(fullQualifiedType, jmgc.getTargetPackage() + ".", "");
      ffullQualifiedType = StringUtils.replace(ffullQualifiedType, jmgc.getTargetPackage() + ".", "");
    }

    return StringUtils.equals(fullQualifiedType, ffullQualifiedType);
  }

  protected boolean isXorContains(String first, String second, String searchStr){
    if(StringUtils.contains(first, searchStr) && !StringUtils.contains(second, searchStr)){
      return true;
    }
    if(!StringUtils.contains(first, searchStr) && StringUtils.contains(second, searchStr)){
      return true;
    }
    return false;
  }

  /**
   * 从AST编译单元中检索出简单类型的权限定类型的字符串表示
   * @author Pin Liu
   * @编写日期: 2014年12月16日上午11:29:21
   * @param unit
   * @param st
   * @return
   */
  protected String findTheAstFullyQualifiedName(CompilationUnit unit, SimpleType st) {
    String sname = st.getName().getFullyQualifiedName();
    @SuppressWarnings("unchecked")
    List<ImportDeclaration> ids = unit.imports();
    for(ImportDeclaration id : ids){
      String ifqn = id.getName().getFullyQualifiedName();
      // 获取不带限定符的类名
      int start = ifqn.lastIndexOf('.');
      start = start == -1 ? 0 : start + 1;
      String subStr = ifqn.substring(start, ifqn.length());
      // 判断是否相同（大小写敏感）
      if(StringUtils.equals(subStr, sname)){
        sname = ifqn;
        break;
      }
    }
    return sname;
  }

  protected String findTheAstFullyQualifiedName(CompilationUnit unit, Name sn) {
    String sname = sn.getFullyQualifiedName();
    @SuppressWarnings("unchecked")
    List<ImportDeclaration> ids = unit.imports();
    for(ImportDeclaration id : ids){
      String ifqn = id.getName().getFullyQualifiedName();
      if(StringUtils.endsWith(ifqn, sname)){
        sname = ifqn;
        break;
      }
    }
    return sname;
  }

  /**
   * 根据方法声明节点的描述从接口描述中找到相对应的Method对象
   * @author Pin Liu
   * @编写日期: 2014年12月15日下午5:19:36
   * @param node
   * @return
   * @throws IllegalAccessException
   */
  public Method findTheMatchedMethod(CompilationUnit unit,List<Method> methods,MethodDeclaration node) throws IllegalAccessException{
    String name = node.getName().getIdentifier();
    for(Method method : methods){
      if(name.equals(method.getName())){ //匹配函数名,由于重载函数的存在需要遍历完所有的同名函数才能知道函数是否匹配，不能使用短路的做法

        boolean match = false; // 用于表示整个方法是否匹配

        // 对参数列表进行匹配
        @SuppressWarnings("unchecked")
        List<SingleVariableDeclaration> parameters = node.parameters();
        List<Parameter> params = method.getParameters();

        // 参数列表不存在
        if(params == null && parameters == null){
          match = true;
          //参数列表均存在
        }else if(params != null && parameters != null && params.size() == parameters.size()){
          boolean isParamsMatch = true;

          // 遍历比较每一位参数的类型及参数名
          for(int i = 0; i < params.size(); i++){
            SingleVariableDeclaration svd = parameters.get(i);
            Parameter p = params.get(i);

            // 如果参数列表中有一个参数类型不同，则整个参数列表不匹配
            if(isTypeEquals(unit, svd.getType(), p.getType())){
              String astName = svd.getName().getFullyQualifiedName();
              String mbgName = p.getName();

              // 如果参数类型相同，但是参数名不同，则整个参数列表仍不匹配
              if(!StringUtils.equals(astName, mbgName)){
                isParamsMatch = false; break;
              }
            }else{
              isParamsMatch = false; break;
            }
          }

          if(isParamsMatch) match = true;
        }

        if(match == true) return method;
      }
    }
    return null;
  }

  /**
   * 添加方法到方法列表中
   * @author Pin Liu
   * @编写日期: 2014年12月16日下午3:51:21
   * @param method
   * @param unit
   */
  public void addNewMethod(final List<Method> methods, MethodDeclaration method, CompilationUnit unit){
    if(methods == null || method == null || unit == null) return;

    Method nm = generateMethodFromMD(method, unit);
    methods.add(nm);
  }

  /**
   * 根据AST中方法声明的描述节点创建MBG的方法对象
   * @author Pin Liu
   * @编写日期: 2014年12月17日下午5:38:28
   * @param method
   * @param unit
   * @return
   */
  protected Method generateMethodFromMD(MethodDeclaration method, CompilationUnit unit) {
    //初始化函数的创建
    Method gMethod = generateMethodDefAndReturnType(method, unit);

    if(gMethod == null) return null;

    //设置方法参数
    generateMethodParams(method, unit, gMethod);

    //设置方法的修饰符及注解,注释
    generateMethodModifier(method, unit, gMethod);
    return gMethod;
  }

  /**
   * 生成方法的定义及返回类型
   * @author Pin Liu
   * @编写日期: 2014年12月17日下午4:55:07
   * @param method
   * @param unit
   * @return
   */
  public Method generateMethodDefAndReturnType(MethodDeclaration method, CompilationUnit unit){

    if(method == null || unit == null) return null;

    Method gMethod = new Method();
    //设置函数名
    gMethod.setName(method.getName().getIdentifier());

    //设置返回类型
    Type type = method.getReturnType2();
    FullyQualifiedJavaType returnType = transformType(type, unit);
    gMethod.setReturnType(returnType);

    //检查函数是否为构造函数
    if(method.isConstructor()){
      gMethod.setConstructor(true);
    }
    return gMethod;
  }

  /**
   * 生成MBG方法的修饰符及注解
   * @author Pin Liu
   * @编写日期: 2014年12月17日下午4:35:19
   * @param method
   * @param unit
   * @param gMethod
   */
  @SuppressWarnings("unchecked")
  public void generateMethodModifier(MethodDeclaration method, CompilationUnit unit, Method gMethod){
    if(method == null || unit == null || gMethod == null) return;

    //设置函数的注解
    Javadoc javadoc = method.getJavadoc();
    transformJavadoc(gMethod, javadoc);

    List<IExtendedModifier> modifiers = method.modifiers();
    transformModifier(gMethod, modifiers);
  }

  /**
   * 为MBG中的Java元素转换Javadoc
   * @author Pin Liu
   * @编写日期: 2014年12月18日下午6:10:42
   * @param element
   * @param javadoc
   */
  protected void transformJavadoc(JavaElement element, Javadoc javadoc) {
    if(element != null){
      element.getJavaDocLines().addAll(transformJavadoc(javadoc));
    }
  }

  protected List<String> transformJavadoc(Javadoc javadoc){
    List<String> results = new ArrayList<>();
    if(javadoc != null){
      String docStr = javadoc.toString();
      String[] docs = StringUtils.split(docStr, (char) Character.LINE_SEPARATOR);
      for(int i = 0; i < docs.length; i++){
        String doc = docs[i];
        if(doc.trim().startsWith("/**")){
          // nothing
        }else if(doc.trim().startsWith("*/")){
          // nothing
        }else{
          results.add(doc.trim().substring(1));
        }
      }
    }
    return results;
  }

  /**
   * 为MBG中的Java类元素添加或者合并超类接口
   * @author Pin Liu
   * @编写日期: 2014年12月23日下午5:43:08
   * @param innerClass
   * @param superInterfaces
   */
  protected void transformSuperInterfaces(InnerClass innerClass, List<Type> superInterfaces, CompilationUnit unit){
    Set<FullyQualifiedJavaType> fqs = innerClass.getSuperInterfaceTypes();
    transformSuperInterfaces(superInterfaces, unit, fqs);
  }

  protected void transformSuperInterfaces(List<Type> superInterfaces, CompilationUnit unit, Set<FullyQualifiedJavaType> fqs) {
    if(superInterfaces != null){
      for(Type type : superInterfaces){
        FullyQualifiedJavaType fq = transformType(type, unit);
        fqs.add(fq);
      }
    }
  }

  protected void transformSuperInterfaces(InnerEnum innerEnum, List<Type> superInterfaces, CompilationUnit unit){
    Set<FullyQualifiedJavaType> fqs = innerEnum.getSuperInterfaceTypes();
    transformSuperInterfaces(superInterfaces, unit, fqs);
  }

  /**
   * 为MBG中的Java类元素添加或者合并初始化代码块
   * @author Pin Liu
   * @编写日期: 2014年12月23日下午5:55:51
   * @param type
   */
  protected void transformInitializer(InnerClass innerClass, TypeDeclaration type) {
    if(innerClass == null || type == null ) return;
    final List<InitializationBlock> initializationBlocks = innerClass.getInitializationBlocks();
    transformInitializer(type, initializationBlocks);
  }

  @SuppressWarnings("unchecked")
  protected void transformInitializer(TypeDeclaration type, final List<InitializationBlock> initializationBlocks) {
    List<BodyDeclaration> bds = type.bodyDeclarations();
    for(BodyDeclaration bd : bds){
      if(bd instanceof Initializer){
        InitializationBlock block = transformInitializer((Initializer) bd);
        boolean found = false;
        for(InitializationBlock ib : initializationBlocks){
          // 寻找相同的初始代码块儿，如果发现则用原有的注释替换掉现在的·
          if(ib.isStatic() == block.isStatic() && isBlocEquals(ib.getBodyLines(), block.getBodyLines())){
            ib.getJavaDocLines().clear();
            ib.getJavaDocLines().addAll(block.getJavaDocLines());
            found = true;
          }
        }
        //未发现则直接添加
        if(!found){
          initializationBlocks.add(block);
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  protected InitializationBlock transformInitializer(Initializer initializer){
    if(initializer == null) return null;

    InitializationBlock ib = new InitializationBlock();

    // 判断初始化代码块儿是否是静态代码块儿
    boolean isStatic = false;
    List<IExtendedModifier> modifiers = initializer.modifiers();
    for(IExtendedModifier modifier : modifiers){
      if(modifier.isModifier()){
        Modifier md = (Modifier) modifier;
        isStatic = md.isStatic();
        if(isStatic) break; // 如果已经找到Static修饰符则直接跳出循环
      }
    }
    ib.setStatic(isStatic);

    // 填充代码块儿的内容
    Block block = initializer.getBody();
    List<Statement> statements = block.statements();
    for(Statement statement : statements){
      ib.addBodyLine(statement.toString());
    }

    // 填充javadoc的内容
    List<String> javadocs = transformJavadoc(initializer.getJavadoc());
    ib.getJavaDocLines().addAll(javadocs);

    return ib;
  }

  /**
   * 映射AST和MBG间的修饰符对应关系
   * @author Pin Liu
   * @编写日期: 2014年12月18日下午6:07:03
   * @param element
   * @param modifiers
   */
  protected void transformModifier(JavaElement element, List<IExtendedModifier> modifiers) {
    for(IExtendedModifier modifier : modifiers){
      //设置元素的注解
      if(modifier.isAnnotation()){
        Annotation annotation = (Annotation) modifier;
        String annotationDefStr = annotationToString(annotation);
        int index  = findMatchedAnnotation(element, annotationDefStr);
        if( index == -1){
          element.addAnnotation(annotationDefStr);
        }else{
          element.getAnnotations().remove(index);
          element.getAnnotations().add(index, annotationDefStr);
        };
      }
      //设置元素的修饰符
      if(modifier.isModifier()){
        Modifier md = (Modifier) modifier;
        ModifierKeyword mk = md.getKeyword();
        switch (mk.toFlagValue()) {
        case Modifier.ABSTRACT: // 只有类的声明才有abstract修饰符
          if(element instanceof InnerClass){
            ((InnerClass)element).setAbstract(true);
          }
          break;
        case Modifier.DEFAULT:
          element.setVisibility(JavaVisibility.DEFAULT);
          break;
        case Modifier.FINAL:
          element.setFinal(true);
          break;
        case Modifier.NATIVE: //只有方法才有native修饰符
          ((Method)element).setNative(true);
          break;
        case Modifier.SYNCHRONIZED: //MBG中只有方法才有synchronized修饰符
          ((Method)element).setSynchronized(true);
          break;
        case Modifier.NONE:
          break;
        case Modifier.PRIVATE:
          element.setVisibility(JavaVisibility.PRIVATE);
          break;
        case Modifier.PROTECTED:
          element.setVisibility(JavaVisibility.PROTECTED);
          break;
        case Modifier.PUBLIC:
          element.setVisibility(JavaVisibility.PUBLIC);
          break;
        case Modifier.STATIC:
          element.setStatic(true);
          break;
        case Modifier.STRICTFP:
          break;
        case Modifier.TRANSIENT:
          break;
        case Modifier.VOLATILE:
          break;
        default:
          break;
        }

      }
    }
  }

  /**
   * 找到匹配的注解定义
   * @author Pin Liu
   * @编写日期: 2014年12月25日下午1:58:42
   * @param element
   * @param annotationDefStr
   * @return
   */
  private int findMatchedAnnotation(JavaElement element, String annotationDefStr) {
    int index = -1;
    List<String> annotations = element.getAnnotations();
    String ads = StringUtils.replacePattern(annotationDefStr, "\\s+", " ");
    for(int i = 0; i < annotations.size(); i++){
      String annotation = annotations.get(i);
      annotation = StringUtils.replacePattern(annotation, "\\s+", " ");
      if(StringUtils.equals(annotation, ads)){
        index = i;
      }
    }
    return index;
  }

  /**
   * 生成MBG的方法参数
   * @author Pin Liu
   * @编写日期: 2014年12月16日下午6:20:41
   * @param method
   * @param unit
   * @param gMethod
   */
  @SuppressWarnings("unchecked")
  protected void generateMethodParams(MethodDeclaration method, CompilationUnit unit, Method gMethod) {
    if(method == null || unit == null || gMethod == null) return;

    Type type;
    List<SingleVariableDeclaration> parameters = method.parameters();
    for(SingleVariableDeclaration svd : parameters){
      type = svd.getType();
      FullyQualifiedJavaType fp = transformType(type, unit);
      Parameter pm = new Parameter(fp, svd.getName().getIdentifier());
      List<IExtendedModifier> modifiers = svd.modifiers();
      for(IExtendedModifier modifier : modifiers){
        //设置参数的注解
        if(modifier.isAnnotation()){
          Annotation annotation = (Annotation) modifier;
          String annotationDefStr = annotationToString(annotation);
          pm.addAnnotation(annotationDefStr);
        }
        //设置参数的修饰符
        if(modifier.isModifier()){
          // mbg 不支持参数级别的修饰符
        }
      }
      gMethod.addParameter(pm);
    }
  }

  /**
   * 转换jdt的Annotation声明为String
   * @author Pin Liu
   * @编写日期: 2014年12月16日下午6:15:05
   * @param annotation
   * @return
   */
  @SuppressWarnings("unchecked")
  protected String annotationToString(Annotation annotation) {
    String typeName = annotation.getTypeName().getFullyQualifiedName();
    StringBuilder sb = new StringBuilder("@");
    sb.append(typeName);
    if(annotation.isMarkerAnnotation()){
      // nothing
    }
    if(annotation.isNormalAnnotation()){
      NormalAnnotation na = (NormalAnnotation) annotation;
      List<MemberValuePair> values = na.values();
      List<String> aps = new ArrayList<>();
      for(MemberValuePair pair : values){
        String name = pair.getName().getFullyQualifiedName();
        Expression expression = pair.getValue();
        String value = expression.toString();
        aps.add(name + "=" + value);
      }
      sb.append("(").append(StringUtils.join(aps, ",")).append(")");
    }
    if(annotation.isSingleMemberAnnotation()){
      SingleMemberAnnotation sma = (SingleMemberAnnotation) annotation;
      sb.append("(").append(sma.getValue().toString()).append(")");
    }
    String annotationDefStr = sb.toString();
    return annotationDefStr;
  }

  /**
   * 转换jdt的类型为mbg的类型
   * @author Pin Liu
   * @编写日期: 2014年12月16日下午4:15:41
   * @param type
   * @param unit
   * @return
   */
  protected FullyQualifiedJavaType transformType(Type type,CompilationUnit unit){
    if(type == null || unit == null) return null;

    String fullQualified = generateFullyQualifiedString(type, unit);
    if(!StringUtils.isEmpty(fullQualified)){
      FullyQualifiedJavaType fq = new FullyQualifiedJavaType(fullQualified);
      if(type.isAnnotatable()){
         // mbg不支持类型上的注解
      }
      return fq;
    }

    return null;
  }

  /**
   * 根据jdt的节点描述生成类型的全限定字符串
   * @author Pin Liu
   * @编写日期: 2014年12月16日下午4:31:59
   * @param type
   * @param unit
   * @return
   */
  private String generateFullyQualifiedString(Type type, CompilationUnit unit) {
    return generateFullyQualifiedString(type, unit, false);
  }

  @SuppressWarnings("unchecked")
  protected String generateFullyQualifiedString(Type type,CompilationUnit unit, boolean isGenericShort){
    if(type == null || unit == null) return null;

    if(type.isPrimitiveType()){
      PrimitiveType pt = (PrimitiveType) type;
      Code code = pt.getPrimitiveTypeCode();
      return code.toString().toLowerCase();
    }

    if(type.isArrayType()){
      ArrayType at = (ArrayType) type;
      String element = generateFullyQualifiedString(at.getElementType(), unit, false);
      int dimensions = at.getDimensions();
      StringBuilder sb = new StringBuilder(element);
      for(int i = 0; i < dimensions; i++){
        sb.append("[]");
      }
      return sb.toString();
    }

    if(type.isWildcardType()){
      WildcardType wt = (WildcardType) type;
      Type bound = wt.getBound();
      if(bound != null){
        StringBuilder sb = new StringBuilder("? ");
        if(wt.isUpperBound()){
          sb.append("extends ");
        }else{
          sb.append("super ");
        }
        sb.append(generateFullyQualifiedString(bound, unit, isGenericShort));
      }else{
        return "?";
      }
    }

    if(type.isNameQualifiedType()){
      NameQualifiedType nqt = (NameQualifiedType) type;
      Name q = nqt.getQualifier();
      String qFull = findTheAstFullyQualifiedName(unit, q);
      return qFull + "." + nqt.getName().getIdentifier();
    }

    if(type.isQualifiedType()){
      QualifiedType qt = (QualifiedType) type;
      Type qualifier = qt.getQualifier();
      String qFull = generateFullyQualifiedString(qualifier, unit, false);
      return qFull + "." + qt.getName().getIdentifier();
    }

    if(type.isParameterizedType()){
      ParameterizedType pt = (ParameterizedType) type;
      List<Type> argumens = pt.typeArguments();
      StringBuilder sb = new StringBuilder();
      sb.append(generateFullyQualifiedString(pt.getType(),unit, isGenericShort));
      List<String> args = new ArrayList<>();
      for(Type arg : argumens){
        args.add(generateFullyQualifiedString(arg, unit, isGenericShort));
      }
      sb.append("<").append(StringUtils.join(args, ",")).append(">");
      return sb.toString();
    }

    if(type.isSimpleType()){
      if(isGenericShort) return type.toString();
      return findTheAstFullyQualifiedName(unit, (SimpleType) type);
    }

    return null;
  }

  /**
   * 将所有的普通引用转换为MBG接受的形式
   * @author Pin Liu
   * @编写日期: 2014年12月16日下午4:09:27
   * @param unit
   * @return
   */
  protected Set<FullyQualifiedJavaType> transformAllImports(CompilationUnit unit) {
    Set<FullyQualifiedJavaType> importedTypes = new HashSet<>();
    //引入所有的引用声明
    @SuppressWarnings("unchecked")
    List<ImportDeclaration> ids = unit.imports();
    for(ImportDeclaration id : ids){
      if(id.isStatic()) continue; //跳过静态引用
      String fullName = id.getName().getFullyQualifiedName();
      importedTypes.add(new FullyQualifiedJavaType(fullName));
    }
    return importedTypes;
  }

  /**
   * 将所有的静态引用转换为MBG接受的形式
   * @author Pin Liu
   * @编写日期: 2014年12月16日下午4:09:27
   * @param unit
   * @return
   */
  protected Set<String> transformAllStaticImports(CompilationUnit unit) {
    Set<String> importedTypes = new HashSet<>();
    //引入所有的引用声明
    @SuppressWarnings("unchecked")
    List<ImportDeclaration> ids = unit.imports();
    for(ImportDeclaration id : ids){
      if(id.isStatic()){
        String staticImport = id.getName().getFullyQualifiedName();
        importedTypes.add(staticImport);
      }
    }
    return importedTypes;
  }

  /**
   * 合并方法（只增加不删除）,不包括方法内容，只合并方法的修饰符，注解，注释以及方法参数的注解
   * @author Pin Liu
   * @编写日期: 2014年12月17日下午5:32:28
   * @param md
   * @param method
   */
  public void mergeMethod(CompilationUnit unit, MethodDeclaration md, Method method){
    if(unit == null || md == null || method == null) return;

    Method gMethod = generateMethodFromMD(md, unit);

    // 合并方法的修饰符
    if(gMethod.isFinal()){
      method.setFinal(true);
    }
    if(gMethod.isNative()){
      method.setNative(true);
    }
    if(gMethod.isStatic()){
      method.setStatic(true);
    }
    if(gMethod.isSynchronized()){
      method.setSynchronized(true);
    }
    method.setVisibility(gMethod.getVisibility());

    // 合并方法的注解
    List<String> added = findNewAddedElement(gMethod.getAnnotations(), method.getAnnotations());
    method.getAnnotations().addAll(added);

    // 合并方法的注释,修改注释可以从很多方面，因此直接使用原有的替换掉现存的
    method.getJavaDocLines().clear();
    method.getJavaDocLines().addAll(gMethod.getJavaDocLines());

    // 合并方法参数的注解
    mergeParamsAnnotations(gMethod, method);
  }

  /**
   * 合并方法参数的注解
   * @author Pin Liu
   * @编写日期: 2014年12月17日下午6:16:06
   * @param gMethod
   * @param method
   */
  public void mergeParamsAnnotations(Method gMethod, Method method){
    List<Parameter> gps = gMethod.getParameters();
    List<Parameter> ps = method.getParameters();
    if(gps.size() == ps.size()){
      for(int i = 0; i < gps.size(); i++){
        Parameter gp = gps.get(i);
        Parameter p = ps.get(i);
        List<String> added = findNewAddedElement(gp.getAnnotations(), p.getAnnotations());
        p.getAnnotations().addAll(added);
      }
    }
  }

  /**
   * 比较并找出新增的元素
   * @author Pin Liu
   * @编写日期: 2014年12月17日下午6:03:36
   * @param origins 解析之前文件的元素集合
   * @param as 当前MBG生成的元素集合
   * @return
   */
  protected List<String> findNewAddedElement(List<String> origins, List<String> currents) {
    List<String> added = new ArrayList<>();
    List<String> asc = new ArrayList<>();
    asc.addAll(currents);
    for(String ga : origins){
      boolean existed = false;
      String nga = StringUtils.replacePattern(ga, "\\s+", " ");
      for(String a : asc){
        String na = StringUtils.replacePattern(a, "\\s+", " ");
        if(StringUtils.equals(nga, na)){
          existed = true;
        }
      }
      if(!existed){
        added.add(ga);
      }
    }
    return added;
  }

  /**
   * 比较两个块儿级元素是否相等
   * @author Pin Liu
   * @编写日期: 2014年12月23日下午6:17:02
   * @param origins
   * @param currents
   * @return
   */
  protected boolean isBlocEquals(List<String> origins, List<String> currents){
    String origin = StringUtils.join(origins, Character.LINE_SEPARATOR);
    origin = StringUtils.replacePattern(origin, "\\s+", " ");
    String current = StringUtils.join(currents, Character.LINE_SEPARATOR);
    current = StringUtils.replacePattern(current, "\\s+", " ");
    return StringUtils.equals(origin, current);
  }

  /**
   * 获取目标源文件的文件对象
   * @author Pin Liu
   * @编写日期: 2014年12月18日下午4:46:38
   * @param jcgc
   * @param type
   * @return
   * @throws org.mybatis.generator.exception.ShellException
   */
  public File getTargetFile(JavaClientGeneratorConfiguration jcgc,FullyQualifiedJavaType type) throws ShellException{
    return getTargetFile(jcgc.getTargetPackage(), type.getShortName()+".java");
  }

  private File getTargetFile(JavaModelGeneratorConfiguration jmgc, FullyQualifiedJavaType type) throws ShellException {
    return getTargetFile(jmgc.getTargetPackage(), type.getShortName()+".java");
  }

  private File getTargetFile(String packiage, String fileName) throws ShellException {
    File root = new File(this.rootDir);
    if(!root.exists()){
      root.mkdirs();
    }
    File directory = callback.getDirectory(rootDir, packiage);
    File targetFile = new File(directory,fileName);
    return targetFile;
  }

  /**
   * 找到与AST的Field节点匹配的MBG的节点对象
   * @author Pin Liu
   * @编写日期: 2014年12月18日下午5:34:23
   * @param node
   * @param fields
   * @return
   */
  @SuppressWarnings("unchecked")
  protected Pair<List<Pair<Field,VariableDeclarationFragment>>,List<VariableDeclarationFragment>>
                                      findTheMatchedField(FieldDeclaration node, List<Field> fields) {

    List<Pair<Field,VariableDeclarationFragment>> found = new ArrayList<>();
    List<VariableDeclarationFragment> notFound = new ArrayList<>();

    List<VariableDeclarationFragment> vdfs = node.fragments();
    for(VariableDeclarationFragment vdf : vdfs){
      boolean f = false;
      for(Field field : fields){
        if(StringUtils.equals(vdf.getName().getIdentifier(), field.getName())){
          found.add(Pair.of(field, vdf)); f = true; break;
        }
      }
      if(!f){
        notFound.add(vdf);
      }
    }

    return Pair.of(found, notFound);
  }

  /**
   * 合并节点对象
   * @author Pin Liu
   * @编写日期: 2014年12月18日下午5:57:29
   * @param pairs
   */
  protected void mergeField(List<Pair<Field,VariableDeclarationFragment>> pairs, FieldDeclaration fd, CompilationUnit unit){
    for(Pair<Field,VariableDeclarationFragment> pair : pairs){
      Field nf = transformVDFtoField(pair.getRight(), fd, unit);
      mergeField(nf, pair.getLeft());
      log.info("Field Merge ---> " + nf.getName());
    }
  }

  /**
   * 合并节点对象
   * @author Pin Liu
   * @编写日期: 2014年12月19日下午3:40:47
   * @param src 源节点
   * @param dest 目标节点
   */
  protected void mergeField(Field src, Field dest){
    //增加注解
    List<String> added = findNewAddedElement(src.getAnnotations(), dest.getAnnotations());
    dest.getAnnotations().addAll(added);
    //增加注释
    dest.getJavaDocLines().clear();
    dest.getJavaDocLines().addAll(src.getJavaDocLines());
    //增加初始化表达式
    dest.setInitializationString(src.getInitializationString());
    //合并修饰符
    if(src.isFinal()){
      dest.setFinal(true);
    }
    if(src.isStatic()){
      dest.setStatic(true);
    }
    if(src.isTransient()){
      dest.setTransient(true);
    }
    if(src.isVolatile()){
      dest.setVolatile(true);
    }
    dest.setVisibility(src.getVisibility());
  }

  /**
   * 转换VariableDeclarationFragment为Field
   * @author Pin Liu
   * @编写日期: 2014年12月18日下午5:59:52
   * @param vdf
   * @return
   */
  @SuppressWarnings("unchecked")
  protected Field transformVDFtoField(VariableDeclarationFragment vdf,FieldDeclaration fd,CompilationUnit unit){
    Field field = new Field();

    //设置字段Javadoc
    transformJavadoc(field, fd.getJavadoc());

    //设置字段修饰符及注解
    transformModifier(field, fd.modifiers());

    //设置字段名称
    String name = vdf.getName().getIdentifier();
    int dimensions = vdf.getExtraDimensions();
    StringBuilder sb = new StringBuilder();
    sb.append(name);
    for(int i = 0; i < dimensions; i++){
      sb.append("[]");
    }
    field.setName(sb.toString());

    //设置字段的初始值
    if(vdf.getInitializer() != null){
      String expression = vdf.getInitializer().toString();
      field.setInitializationString(expression);
    }

    //设置字段类型
    FullyQualifiedJavaType fqjt = transformType(fd.getType(), unit);
    field.setType(fqjt);

    return field;
  }

  /**
   * 添加新的字段节点到字段列表中
   * @author Pin Liu
   * @编写日期: 2014年12月19日下午3:52:45
   * @param fields
   * @param vdfs
   * @param fd
   * @param unit
   */
  protected void addNewFields(final List<Field> fields, List<VariableDeclarationFragment> vdfs, FieldDeclaration fd, CompilationUnit unit){
    if(fields == null || vdfs == null || fd == null || unit == null ) return;
    for(VariableDeclarationFragment vdf : vdfs){
      Field field = transformVDFtoField(vdf, fd, unit);
      if(field != null){
        fields.add(field);
        log.info("Field Add ---> " + field.getName());
      }
    }
  }

  /**
   * 以语法树遍历的形式访问AST并与MBG的Interface合并
   * @author Pin Liu
   * @编写日期: 2014年12月23日下午4:27:30
   * @param interfaze
   * @param targetFile
   * @throws java.io.IOException
   * @throws IllegalAccessException
   */
  public void visitAndMerge(final Interface interfaze, File targetFile) throws IOException, IllegalAccessException{
    if(interfaze == null || targetFile == null) return;

    final List<Method> methods = interfaze.getMethods();
    final CompilationUnit unit = generateASTfromTheFile(targetFile);

    @SuppressWarnings("unchecked")
    Set<FullyQualifiedJavaType> importeds = (Set<FullyQualifiedJavaType>) FieldUtils.readDeclaredField(interfaze, "importedTypes", true);
    // 合并普通引入列表
    Set<FullyQualifiedJavaType> importedTypes = transformAllImports(unit);
    importeds.addAll(importedTypes);

    unit.accept(new ASTVisitor(true) {

      @Override
      public boolean visit(AnonymousClassDeclaration node) {
        //匿名内部类，MBG暂不支持
        return super.visit(node);
      }

      @Override
      @SuppressWarnings("unchecked")
      public boolean visit(TypeDeclaration node) {
        if(node.isInterface()){
          String shortName = interfaze.getType().getShortName();
          String identifier = node.getName().getIdentifier();
          if(!StringUtils.equals(shortName, identifier)){
            return true;
          }
        }
        log.info("Type Merge ---> " + interfaze.getType());
        // 合并类级别的注解以及修饰符
        transformModifier(interfaze, node.modifiers());
        // 设置类级别的注释
        transformJavadoc(interfaze, node.getJavadoc());

        return super.visit(node);
      }
      @Override
      public boolean visit(MethodDeclaration node) {
        try {
          Method method = null;
          if(methods != null){
            method = findTheMatchedMethod(unit, methods, node);
            if(method == null){ // 如果方法不存在则此方法节点为用户自己添加的，需要追加到生成的文件中
              addNewMethod(methods, node, unit);
              log.info("Method Add ---> " + node.getName());
            }else{ // 如果方法已经存在则需要查看用户自己添加的内容，包括注释
              mergeMethod(unit, node, method);
              log.info("Method Merge ---> " + method.getName());
            }
          }
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
        return super.visit(node);
      }

    });
  }

  /**
   * 合并或者添加字段声明到字段列表中
   * @author Pin Liu
   * @编写日期: 2014年12月23日下午5:25:19
   * @param fds
   * @param fields
   */
  protected void mergeOrAddFields(List<FieldDeclaration> fds, List<Field> fields, CompilationUnit unit){
    if(fds == null || fields == null || unit == null) return;
    for(FieldDeclaration fd : fds){
      Pair<List<Pair<Field,VariableDeclarationFragment>>,List<VariableDeclarationFragment>> pair = findTheMatchedField(fd,fields);
      // 合并字段
      if(pair.getLeft() != null && pair.getLeft().size() != 0){
        mergeField(pair.getLeft(),fd,unit);
      }
      // 添加字段
      if(pair.getRight() != null && pair.getRight().size() != 0){
        addNewFields(fields, pair.getRight(), fd, unit);
      }
    }
  }

  /**
   * 合并或者添加方法声明到字段列表中
   * @author Pin Liu
   * @编写日期: 2014年12月23日下午5:31:10
   * @param mds
   * @param methods
   * @param unit
   */
  protected void mergeOrAddMethods(List<MethodDeclaration> mds, List<Method> methods, CompilationUnit unit){
    if(mds == null || methods == null || unit == null) return;
    for(MethodDeclaration md : mds){
      try {
        Method method = null;
        if(methods != null){
          method = findTheMatchedMethod(unit, methods, md);
          if(method == null){ // 如果方法不存在则此方法节点为用户自己添加的，需要追加到生成的文件中
            addNewMethod(methods, md, unit);
            log.info("Method Add ---> " + md.getName());
          }else{ // 如果方法已经存在则需要查看用户自己添加的内容，包括注释
            mergeMethod(unit, md, method);
            log.info("Method Merge ---> " + method.getName());
          }
        }
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 因类中可能会有内部类的存在，在遍历时需要注意方法的所属的集合
   * @author Pin Liu
   * @编写日期: 2014年12月23日下午4:33:05
   * @param topLevelClass
   * @param targetFile
   * @throws java.io.IOException
   * @throws IllegalAccessException
   */
  @SuppressWarnings("unchecked")
  public void visitAndMerge(final TopLevelClass topLevelClass, File targetFile) throws IOException, IllegalAccessException{
    if(topLevelClass == null || targetFile == null) return;

    final CompilationUnit unit = generateASTfromTheFile(targetFile);

    Set<FullyQualifiedJavaType> importeds = (Set<FullyQualifiedJavaType>) FieldUtils.readDeclaredField(topLevelClass, "importedTypes", true);
    // 合并普通引入列表
    Set<FullyQualifiedJavaType> importedTypes = transformAllImports(unit);
    importeds.addAll(importedTypes);
    // 合并静态引入列表
    Set<String> staticImports = transformAllStaticImports(unit);
    topLevelClass.getStaticImports().addAll(staticImports);

    final List<Method> methods = topLevelClass.getMethods();
    final List<Field> fields = topLevelClass.getFields();
    final List<InnerClass> innerClasses = topLevelClass.getInnerClasses();
    final List<InnerEnum> innerEnums = topLevelClass.getInnerEnums();
    final Set<FullyQualifiedJavaType> superInterfaces = topLevelClass.getSuperInterfaceTypes();

    // 遍历访问所有的top-level级别的类型声明
    List<TypeDeclaration> types = unit.types();
    for(TypeDeclaration type : types){
      if(type.getName().getIdentifier().equals(topLevelClass.getType().getShortName())){

        log.info("Type Merge ---> " + topLevelClass.getType());

        // 合并类的初始化代码块儿
        transformInitializer(topLevelClass,type);

        visitAndMerge(topLevelClass, unit, methods, fields, innerClasses, innerEnums,superInterfaces, type);
      }
    }
  }

  @SuppressWarnings("unchecked")
  protected void visitAndMerge(final JavaElement element, final CompilationUnit unit,
      final List<Method> methods, final List<Field> fields, final List<InnerClass> innerClasses,
      final List<InnerEnum> innerEnums,final Set<FullyQualifiedJavaType> superInterfaces,TypeDeclaration type) {

    // 合并类级别的注解以及修饰符
    transformModifier(element, type.modifiers());
    // 设置类级别的注释
    transformJavadoc(element, type.getJavadoc());

    // 合并类的超类接口
    transformSuperInterfaces(type.superInterfaceTypes(), unit, superInterfaces);

    // 字段
    FieldDeclaration[] fds =  type.getFields();
    mergeOrAddFields(Arrays.asList(fds), fields, unit);

    // 方法
    MethodDeclaration[] mds = type.getMethods();
    mergeOrAddMethods(Arrays.asList(mds), methods, unit);

    // 内部类 ,内部枚举
    mergeInnerEnumAndInnerClass(unit, innerEnums, innerClasses, type);

  }

  @SuppressWarnings("unchecked")
  protected void visitAndMerge(final JavaElement element, final CompilationUnit unit,
      final List<Method> methods, final List<Field> fields, final List<InnerClass> innerClasses,
      final List<InnerEnum> innerEnums,final Set<FullyQualifiedJavaType> superInterfaces,EnumDeclaration type) {

    // 合并类级别的注解以及修饰符
    transformModifier(element, type.modifiers());
    // 设置类级别的注释
    transformJavadoc(element, type.getJavadoc());

    // 合并类的超类接口
    transformSuperInterfaces(type.superInterfaceTypes(), unit, superInterfaces);

    // 遍历AST树找出字段声明及方法声明
    List<FieldDeclaration> fds = new ArrayList<>();
    List<MethodDeclaration> mds = new ArrayList<>();

    List<BodyDeclaration> bds = type.bodyDeclarations();
    for(BodyDeclaration bd : bds){
      if(bd instanceof FieldDeclaration){
        fds.add((FieldDeclaration) bd);
      }
      if(bd instanceof MethodDeclaration){
        mds.add((MethodDeclaration) bd);
      }
    }
    // 字段
    mergeOrAddFields(fds, fields, unit);

    // 方法
    mergeOrAddMethods(mds, methods, unit);

    // 内部类 ,内部枚举
    mergeInnerEnumAndInnerClass(unit, innerEnums, innerClasses, type);

  }

  /**
   * 合并内部枚举及内部类，暂不支持新增内部类及枚举
   * @author Pin Liu
   * @编写日期: 2014年12月24日下午1:34:05
   * @param unit
   * @param innerEnums
   * @param innerClasses
   * @param type
   */
  @SuppressWarnings("unchecked")
  protected void mergeInnerEnumAndInnerClass(final CompilationUnit unit, final List<InnerEnum> innerEnums,
      final List<InnerClass> innerClasses, AbstractTypeDeclaration type) {

    List<EnumDeclaration> eds = new ArrayList<EnumDeclaration>();
    List<TypeDeclaration> tds = new ArrayList<>();

    // 从AST语法树中检索出枚举声明以及类声明
    List<BodyDeclaration> bds = type.bodyDeclarations();
    for(BodyDeclaration bd : bds){
      if(bd instanceof EnumDeclaration){
        eds.add((EnumDeclaration) bd);
      }
      if(bd instanceof TypeDeclaration){
        tds.add((TypeDeclaration) bd);
      }
    }

    // 寻找匹配的类型声明，存在则合并,暂不支持直接添加
    for(TypeDeclaration td : tds){
      InnerClass inc = findMatchedInnerClass(td, innerClasses);
      if(inc != null){
        visitAndMerge(inc, td, unit);
      }
    }

    // 寻找匹配的枚举声明，存在则合并，暂不支持直接添加
    for(EnumDeclaration ed : eds){
      InnerEnum ine = findMatchedInnerEnum(ed, innerEnums);
      if(ine != null){
        visitAndMerge(ine, ed, unit);
      }
    }
  }

  /**
   * 找到与类型声明匹配的内部类对象
   * @author Pin Liu
   * @编写日期: 2014年12月24日下午12:00:39
   * @param td
   * @param innerClasses
   * @return
   */
  protected InnerClass findMatchedInnerClass(TypeDeclaration td, List<InnerClass> innerClasses){
    InnerClass innerClass = null;
    for(InnerClass in : innerClasses){
      if(StringUtils.equals(td.getName().getIdentifier(),in.getType().getShortName())){
        innerClass = in;break;
      }
    }
    return innerClass;
  }

  protected InnerEnum findMatchedInnerEnum(EnumDeclaration ed, List<InnerEnum> innerEnums){
    InnerEnum innerEnum = null;
    for(InnerEnum in : innerEnums){
      if(StringUtils.equals(ed.getName().getIdentifier(),in.getType().getShortName())){
        innerEnum = in;break;
      }
    }
    return innerEnum;
  }

  /**
   * 遍历并合并内部类
   * @author Pin Liu
   * @编写日期: 2014年12月24日上午11:54:07
   * @param innerClass
   * @param td
   */
  public void visitAndMerge(final InnerClass innerClass, TypeDeclaration td, CompilationUnit unit){

    final List<Method> methods = innerClass.getMethods();
    final List<Field> fields = innerClass.getFields();
    final List<InnerClass> innerClasses = innerClass.getInnerClasses();
    final List<InnerEnum> innerEnums = innerClass.getInnerEnums();
    final Set<FullyQualifiedJavaType> superInterfaces = innerClass.getSuperInterfaceTypes();

    log.info("InnerClass Type Merge ---> " + innerClass.getType());

    // 合并类的初始化代码块儿
    transformInitializer(innerClass,td);

    visitAndMerge(innerClass, unit, methods, fields, innerClasses, innerEnums,superInterfaces, td);
  }

  public void visitAndMerge(final InnerEnum innerEnum, EnumDeclaration ed, CompilationUnit unit){
    final List<Method> methods = innerEnum.getMethods();
    final List<Field> fields = innerEnum.getFields();
    final List<InnerClass> innerClasses = innerEnum.getInnerClasses();
    final List<InnerEnum> innerEnums = innerEnum.getInnerEnums();
    final Set<FullyQualifiedJavaType> superInterfaces = innerEnum.getSuperInterfaceTypes();

    log.info("InnerEnum Type Merge ---> " + innerEnum.getType());

    // 合并枚举的枚举常量
    transformEnumConstant(innerEnum, ed);

    visitAndMerge(innerEnum, unit, methods, fields, innerClasses, innerEnums,superInterfaces, ed);
  }

  /**
   * 合并或者添加枚举常量
   * @author Pin Liu
   * @编写日期: 2014年12月24日下午1:52:42
   * @param innerEnum
   * @param td
   */
  @SuppressWarnings("unchecked")
  private void transformEnumConstant(InnerEnum innerEnum, EnumDeclaration td) {
    if(innerEnum == null || td == null) return;
    List<String> enumConstants = innerEnum.getEnumConstants();
    List<EnumConstantDeclaration> ecds = td.enumConstants();
    for(EnumConstantDeclaration ecd : ecds){
      int index = findMatchedEnumConstantsIndex(ecd, enumConstants);
      if(index == -1){
        enumConstants.add(ecd.toString());
      }
    }
  }

  private int findMatchedEnumConstantsIndex(EnumConstantDeclaration ecd, List<String> enumConstants) {
    int index = -1;
    for(int i = 0; i < enumConstants.size(); i++){
      String enumConstant = enumConstants.get(i);
      if(ecd.getName().getIdentifier().equals(enumConstant)){
        index = i; break;
      }
    }
    return index;
  }

  @Override
  public boolean clientGenerated(final Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
    JavaClientGeneratorConfiguration jcgc = context.getJavaClientGeneratorConfiguration();
    try {
      File targetFile = getTargetFile(jcgc, interfaze.getType());
      if(!targetFile.exists()){ // 第一次生成直接使用当前生成的文件
        return true;
      }
      visitAndMerge(interfaze, targetFile);
    } catch (ShellException | IOException | IllegalAccessException e) {
      e.printStackTrace();
    }
    return true;
  }

  @Override
  public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
    JavaModelGeneratorConfiguration jmgc = context.getJavaModelGeneratorConfiguration();
    try {
      File targetFile = getTargetFile(jmgc, topLevelClass.getType());
      if(!targetFile.exists()){ // 第一次生成直接使用当前生成的文件
        return true;
      }
      visitAndMerge(topLevelClass, targetFile);
    } catch (ShellException | IOException | IllegalAccessException e) {
      e.printStackTrace();
    }
    return true;
  }


  @Override
  public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
    JavaModelGeneratorConfiguration jmgc = context.getJavaModelGeneratorConfiguration();
    try {
      File targetFile = getTargetFile(jmgc, topLevelClass.getType());
      if(!targetFile.exists()){ // 第一次生成直接使用当前生成的文件
        return true;
      }
      visitAndMerge(topLevelClass, targetFile);
    } catch (ShellException | IOException | IllegalAccessException e) {
      e.printStackTrace();
    }
    return true;
  }

  @Override
  public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
    SqlMapGeneratorConfiguration smgc = context.getSqlMapGeneratorConfiguration();
    try {
      Document document = (Document) FieldUtils.readDeclaredField(sqlMap, "document", true);
      File targetFile = getTargetFile(smgc.getTargetPackage(), sqlMap.getFileName());
      if(!targetFile.exists()){ // 第一次生成直接使用当前生成的文件
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
   * @author Pin Liu
   * @编写日期: 2014年12月22日下午4:49:19
   * @param document
   * @param targetFile
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
         if(node.getParent() == rootElement){
           XmlElement xe = findMatchedElementIn(document, node);
           if(xe == null){ // 新增节点 添加到document中
             int index = node.getParent().elements().indexOf(node);
             xe = transformElement(node);
             log.info("XmlElement Add ---> " + xe.getName() + " id=" + idValue(node));
             document.getRootElement().getElements().add(index, xe);
           }else{ // 合并已经存在的节点
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
   * @author Pin Liu
   * @编写日期: 2014年12月23日上午11:20:14
   * @param src
   * @param dest
   */
  protected void mergeExistedElement(XmlElement src, XmlElement dest){
    
    // 合并属性
    List<Attribute> srcAttributes = src.getAttributes();
    List<Attribute> destAttributes = dest.getAttributes();
    for(Attribute srcAttr : srcAttributes){
      Attribute matched = null;
      for(Attribute destAttr : destAttributes){
        if(StringUtils.equals(srcAttr.getName(), destAttr.getName()) &&
            StringUtils.equals(srcAttr.getValue(), destAttr.getValue())){
          matched = destAttr;
        }
      }
      // 不存在则添加到目标元素的属性列表中
      if(matched == null){
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
   * @author Pin Liu
   * @编写日期: 2014年12月23日下午12:03:46
   */
  protected void reformationTheElementChilds(XmlElement xe){
    List<Element> reformationList = new ArrayList<>();
    for(Element element : xe.getElements()){
      // 如果是XML元素节点，则直接添加
      if(element instanceof XmlElement){
        reformationList.add(element);
      }
      if(element instanceof TextElement){
        int lastIndex = reformationList.size()-1;
        TextElement te = (TextElement) element;
        
        // 如果当前文本节点之前的一个节点也是文本节点时，将两个文本节点合并成一个替换之前的节点
        // 否则直接添加
        if(!reformationList.isEmpty() && reformationList.get(lastIndex) instanceof TextElement){
          te = (TextElement) reformationList.get(lastIndex);
          StringBuilder sb = new StringBuilder();
          sb.append(te.getContent()).append(((TextElement)element).getContent());
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
   * @author Pin Liu
   * @编写日期: 2014年12月22日下午5:50:15
   * @param node
   * @return
   */
  protected XmlElement transformElement(org.dom4j.Element node) {
    XmlElement xe = new XmlElement(node.getName());
    
    // 设置元素的属性 
    @SuppressWarnings("unchecked")
    Iterator<org.dom4j.Attribute> iterator = node.attributeIterator();
    while(iterator.hasNext()){
      org.dom4j.Attribute ab = iterator.next();
      xe.addAttribute(new Attribute(ab.getName(), ab.getValue()));
    }
    // 深度优先遍历子节点
    @SuppressWarnings("unchecked")
    Iterator<org.dom4j.Node> niter = node.nodeIterator();
    while(niter.hasNext()){
      org.dom4j.Node n = niter.next();
      // 文本节点
      if(n.getNodeType() == org.dom4j.Node.TEXT_NODE){
        Text text = (Text) n;
        TextElement te = new TextElement(text.getText().trim());
        xe.addElement(te);
      }
      // 元素节点
      if(n.getNodeType() == org.dom4j.Node.ELEMENT_NODE){
        xe.addElement(transformElement((org.dom4j.Element) n));
      }
      // 注释节点
      if(n.getNodeType() == org.dom4j.Node.COMMENT_NODE){
        TextElement te = new TextElement(n.asXML().trim());
        xe.addElement(te);
      }
      // CDATA 节点
      if(n.getNodeType() == org.dom4j.Node.CDATA_SECTION_NODE){
        TextElement te = new TextElement(n.asXML().trim());
        xe.addElement(te);
      }
    }
    return xe;
  }

  /**
   * 从MBG生成的DOM文档结构中找到与element代表同一节点的元素对象
   * @author Pin Liu
   * @编写日期: 2014年12月22日下午5:35:08
   * @param document
   * @param element
   * @return
   */
  protected XmlElement findMatchedElementIn(Document document, org.dom4j.Element element){
    org.dom4j.Attribute id = element.attribute("id");
    String idName = id.getName();
    String idValue = id.getValue();
    for(Element me : document.getRootElement().getElements()){
      if(me instanceof XmlElement){
        XmlElement xe = (XmlElement) me;
        for(Attribute ab : xe.getAttributes()){
          if(StringUtils.equals(idName, ab.getName()) && StringUtils.equals(idValue, ab.getValue())){
            return xe;
          }
        }
      }
    }
    return null;
  }
  
  protected String idValue(org.dom4j.Element element){
    org.dom4j.Attribute id = element.attribute("id");
    if(id != null){
      return id.getValue();
    }else{
      return "";
    }
  }

}
