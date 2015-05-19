package com.freetmp.mbg.plugin.page;

import com.freetmp.mbg.plugin.AbstractPlugin;
import com.freetmp.mbg.plugin.batch.BatchUpdatePlugin;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;

/**
 * MBG分页插件的抽象基类，提供统一的分页属性及分页操作
 * 使用统一的数据模型：offset 当前页离开始记录的偏移，limit 当前页的记录数限制
 * Created by LiuPin on 2015/1/30.
 */
public abstract class AbstractPaginationPlugin extends AbstractPlugin {
    
    public static final String LIMIT_NAME = "limit";
    public static final String OFFSET_NAME = "offset";
    
    public static final String BOUND_BUILDER_NAME = "BoundBuilder";

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass,
                                              IntrospectedTable introspectedTable) {
        // add field, getter, setter for limit clause
        addLimit(topLevelClass, introspectedTable, LIMIT_NAME);
        addLimit(topLevelClass, introspectedTable, OFFSET_NAME);
        addFluentApi(topLevelClass, introspectedTable, LIMIT_NAME, OFFSET_NAME);
        return super.modelExampleClassGenerated(topLevelClass,introspectedTable);
    }
    
    /**
     * 为topLevelClass添加分页的限制属性以及Getter和Setter方法
     * @author Pin Liu
     * @编写日期: 2014年11月26日下午12:53:22
     * @param topLevelClass 带生成的Example类
     * @param introspectedTable 数据库表的元数据
     * @param name 属性名
     */
    protected void addLimit(TopLevelClass topLevelClass,IntrospectedTable introspectedTable, String name) {
        CommentGenerator commentGenerator = context.getCommentGenerator();
        Field field = new Field();
        field.setVisibility(JavaVisibility.PROTECTED);
        field.setType(PrimitiveTypeWrapper.getIntegerInstance());
        field.setName(name);
        commentGenerator.addFieldComment(field, introspectedTable);
        topLevelClass.addField(field);
        String camel = getCamel(name);
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("set" + camel);
        method.addParameter(new Parameter(PrimitiveTypeWrapper.getIntegerInstance(), name));
        method.addBodyLine("this." + name + "=" + name + ";");
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(PrimitiveTypeWrapper.getIntegerInstance());
        method.setName("get" + camel);
        method.addBodyLine("return " + name + ";");
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
    }

    private String getCamel(String name) {
        char c = name.charAt(0);
        return Character.toUpperCase(c) + name.substring(1);
    }

    /**
     * 为topLevelClass添加分页方法控制的Fluent方法 
     * @param topLevelClass
     * @param introspectedTable
     * @param limit
     * @param offset
     */
    protected void addFluentApi(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String limit, String offset){

        InnerClass boundBuilder = generateBoundBuilder(topLevelClass, limit, offset);
        
        topLevelClass.addInnerClass(boundBuilder);
        
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("bound");
        method.setReturnType(boundBuilder.getType());
        method.addBodyLine("return new "+BOUND_BUILDER_NAME+"(this);");
        
        topLevelClass.addMethod(method);
    }

    /**
     * 生成边界建造器对象
     * @param topLevelClass
     * @param limit
     * @param offset
     * @return
     */
    private InnerClass generateBoundBuilder(TopLevelClass topLevelClass, String limit, String offset) {
        FullyQualifiedJavaType boundBuilderType = new FullyQualifiedJavaType(BOUND_BUILDER_NAME);

        InnerClass boundBuilder = new InnerClass(boundBuilderType);
        boundBuilder.setVisibility(JavaVisibility.PUBLIC);
        boundBuilder.setStatic(true);

        Field field = new Field();
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setType(PrimitiveTypeWrapper.getIntegerInstance());
        field.setName(limit);
        boundBuilder.addField(field);

        field = new Field();
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setType(PrimitiveTypeWrapper.getIntegerInstance());
        field.setName(offset);
        boundBuilder.addField(field);

        field = new Field();
        field.setVisibility(JavaVisibility.DEFAULT);
        field.setType(topLevelClass.getType());
        field.setName("target");
        boundBuilder.addField(field);

        // 构造方法
        Method method = new Method();
        method.setConstructor(true);
        method.setName(BOUND_BUILDER_NAME);
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addParameter(new Parameter(topLevelClass.getType(),"target"));
        method.addBodyLine("this.target = target;");
        boundBuilder.addMethod(method);
        
        // 设置limit的方法
        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName(limit);
        method.addParameter(new Parameter(PrimitiveTypeWrapper.getIntegerInstance(), limit));
        method.setReturnType(boundBuilderType);
        method.addBodyLine("this." + limit + "=" + limit + ";");
        method.addBodyLine("return this;");
        boundBuilder.addMethod(method);

        // 设置offset的方法
        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName(offset);
        method.addParameter(new Parameter(PrimitiveTypeWrapper.getIntegerInstance(), offset));
        method.setReturnType(boundBuilderType);
        method.addBodyLine("this." + offset + "=" + offset + ";");
        method.addBodyLine("return this;");
        boundBuilder.addMethod(method);

        // 将搜集到的page相关属性设置到target中
        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("build");
        method.setReturnType(topLevelClass.getType());
        method.addBodyLine("this.target.set" + getCamel(limit) + "(" + limit + ");");
        method.addBodyLine("this.target.set" + getCamel(offset) + "(" + offset + ");");
        method.addBodyLine("return this.target;");
        boundBuilder.addMethod(method);
        
        return boundBuilder;
    }

    /*
     * This plugin is always valid - no properties are required
     */
    public boolean validate(List<String> warnings) {
        return true;
    }
}
