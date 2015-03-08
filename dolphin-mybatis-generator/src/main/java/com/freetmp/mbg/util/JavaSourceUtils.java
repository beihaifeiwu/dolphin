package com.freetmp.mbg.util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.type.Type;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by LiuPin on 2015/3/7.
 */
public class JavaSourceUtils {


    public static <T> boolean isAllNull(T one, T two){
        return one == null ? two == null : false;
    }

    public static <T> boolean isAllNotNull(T one, T two){
        return one != null && two != null;
    }

    public static <T> T findFirstNotNull(T... types){
        for (T type : types){
            if (type != null) return type;
        }
        return null;
    }

    /**
     * 合并注释
     * @param one
     * @param two
     * @return
     */
    public static Comment mergeComment(Comment one, Comment two) {
        Comment fileComment = null;

        if(isAllNull(one,two)){
            return fileComment;
        }

        fileComment = findFirstNotNull(one,two);

        return fileComment;
    }

    /**
     * 合并引入类的声明
     * @param one
     * @param two
     * @return
     */
    public static List<ImportDeclaration> mergeImports(List<ImportDeclaration> one, List<ImportDeclaration> two) {
        List<ImportDeclaration> result = new ArrayList<>();

        TreeSet<ImportDeclaration> merged = new TreeSet<>();
        merged.addAll(one);
        merged.addAll(two);

        result.addAll(merged);

        return result;
    }

    /**
     * 合并修饰符
     * @param one
     * @param two
     * @return
     */
    public static int mergeModifiers(int one, int two){
        return ModifierSet.addModifier(one,two);
    }

    /**
     * 合并注解声明
     * @param one
     * @param two
     * @return
     */
    public static List<AnnotationExpr> mergeAnnotations(List<AnnotationExpr> one, List<AnnotationExpr> two){

        List<AnnotationExpr> result = new ArrayList<>();

        TreeSet<AnnotationExpr> set = new TreeSet<>();
        set.addAll(one);
        set.addAll(two);

        result.addAll(set);

        return result;
    }

    /**
     * 合并表达式
     * @param one
     * @param two
     * @return
     */
    public static Expression mergeEpression(Expression one, Expression two){
        Expression expression = null;

        //TODO

        return expression;
    }

    /**
     * 合并类型
     * @param one
     * @param two
     * @return
     */
    public static Type mergeType(Type one, Type two){
        Type type = null;

        if(isAllNull(one,two)) return null;

        if(isAllNotNull(one,two)){

            //TODO

        }else {
            type = findFirstNotNull(one,two);
        }

        return type;
    }

    /**
     * 合并注解成员声明
     * @param one
     * @param two
     * @return
     */
    public static AnnotationMemberDeclaration mergeAnnotationMember(
            AnnotationMemberDeclaration one, AnnotationMemberDeclaration two){

        if(isAllNull(one,two)) return null;

        AnnotationMemberDeclaration amd = null;

        if(isAllNotNull(one,two)){

            amd = new AnnotationMemberDeclaration();

            amd.setJavaDoc((JavadocComment) mergeComment(one.getJavaDoc(),two.getJavaDoc()));
            amd.setComment(mergeComment(one.getComment(),two.getComment()));
            amd.setAnnotations(mergeAnnotations(one.getAnnotations(),two.getAnnotations()));
            amd.setModifiers(mergeModifiers(one.getModifiers(),two.getModifiers()));
            amd.setName(one.getName());
            amd.setDefaultValue(mergeEpression(one.getDefaultValue(),two.getDefaultValue()));
            amd.setType(mergeType(one.getType(),two.getType()));

        }else {
            amd = findFirstNotNull(one,two);
        }

        return amd;

    }

    /**
     * 合并内容
     * @param one
     * @param two
     * @return
     */
    public static List<BodyDeclaration> mergeBodies(List<BodyDeclaration> one, List<BodyDeclaration> two){
        List<BodyDeclaration> result = new ArrayList<>();

        for(BodyDeclaration outer : one){

            boolean found = false;

            for(BodyDeclaration inner : two){

                // only type matched can carry on
                if(inner.getClass().equals(outer.getClass())){

                    if(inner instanceof TypeDeclaration){
                        TypeDeclaration typeOne = (TypeDeclaration) outer;
                        TypeDeclaration typeTwo = (TypeDeclaration) inner;
                        if(typeOne.getName().equals(typeTwo.getName())){
                            result.add(mergeType(typeOne,typeTwo));
                        }
                    } else if (inner instanceof AnnotationMemberDeclaration){
                        AnnotationMemberDeclaration amdOne = (AnnotationMemberDeclaration) outer;
                        AnnotationMemberDeclaration amdTwo = (AnnotationMemberDeclaration) inner;
                        if(amdOne.getName().equals(amdTwo)){
                            result.add(mergeAnnotationMember(amdOne,amdTwo));
                        }
                    }

                }

            }

            if(!found){
                result.add(outer);
            }
        }

        return result;
    }

    /**
     * 合并注解声明
     * @param one
     * @param two
     * @return
     */
    public static AnnotationDeclaration mergeType(AnnotationDeclaration one, AnnotationDeclaration two){

        if(isAllNull(one,two)) return null;

        AnnotationDeclaration annotationDeclaration = null;

        if(isAllNotNull(one,two)){

            annotationDeclaration = new AnnotationDeclaration();

            annotationDeclaration.setModifiers(
                    mergeModifiers(one.getModifiers(), two.getModifiers()));

            annotationDeclaration.setJavaDoc(
                    (JavadocComment) mergeComment(one.getJavaDoc(),two.getJavaDoc()));

            annotationDeclaration.setComment(mergeComment(one.getComment(),two.getComment()));

            annotationDeclaration.setAnnotations(
                    mergeAnnotations(one.getAnnotations(),two.getAnnotations()));

            // merge content body
            annotationDeclaration.setMembers(mergeBodies(one.getMembers(),two.getMembers()));

        }else {
            annotationDeclaration = findFirstNotNull(one,two);
        }

        return annotationDeclaration;
    }


    /**
     * 合并类型声明的分发函数
     * @param one
     * @param two
     * @return
     */
    public static TypeDeclaration mergeType(TypeDeclaration one,TypeDeclaration two){
        TypeDeclaration type = null;

        if(isAllNull(one,two)) return null;

        if(isAllNotNull(one,two)){
            // just ignore when class type are not same
            if(one.getClass().equals(two.getClass())){

                if(one instanceof AnnotationDeclaration){
                    type = mergeType((AnnotationDeclaration)one,(AnnotationDeclaration)two);
                }

            }

        }else {
            type = findFirstNotNull(one,two);
        }

        return type;
    }

    /**
     * 合并类型声明
     * @param one
     * @param two
     * @return
     * @throws Exception
     */
    public static List<TypeDeclaration> mergeTypes(List<TypeDeclaration> one, List<TypeDeclaration> two) throws Exception {
        List<TypeDeclaration> result = new ArrayList<>();

        for(TypeDeclaration outer : one){

            boolean found = false;
            for(TypeDeclaration inner : two){

                if(outer.getName().equals(inner.getName())){
                    TypeDeclaration merged = mergeType(outer,inner);
                    result.add(merged);
                    found = true;
                }
            }

            if(!found){
                result.add(outer);
            }
        }
        return result;
    }
    
    /**
     * 合并两个编译单元的内容
     * @param one
     * @param two
     */
    public static String mergeContent(CompilationUnit one,CompilationUnit two) throws Exception {

        // 包声明不同，返回null
        if(!one.getPackage().equals(two.getPackage())) return null;

        CompilationUnit cu = new CompilationUnit();

        // add package declaration to the compilation unit
        PackageDeclaration pd = new PackageDeclaration();
        pd.setName(one.getPackage().getName());
        cu.setPackage(pd);

        // check and merge file comment;
        Comment fileComment = mergeComment(one.getComment(), two.getComment());
        cu.setComment(fileComment);

        // check and merge imports
        List<ImportDeclaration> ids = mergeImports(one.getImports(), two.getImports());
        cu.setImports(ids);

        // check and merge Types
        List<TypeDeclaration> types = mergeTypes(one.getTypes(),two.getTypes());
        cu.setTypes(types);

        return cu.toString();
    }

    /**
     * 合并两个源Java source中的内容
     * @param one
     * @param two
     * @return
     */
    public static String mergeContent(String one, String two) throws Exception {
        return mergeContent(generateAst(one),generateAst(two));
    }

    /**
     * 根据字符串生成编译单元
     * @param source
     * @return
     * @throws ParseException
     */
    public static CompilationUnit generateAst(String source) throws ParseException {
        return JavaParser.parse(new StringReader(source), true);
    }
}
