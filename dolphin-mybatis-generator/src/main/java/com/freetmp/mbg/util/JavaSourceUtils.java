package com.freetmp.mbg.util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;

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

    public static <T> int indexOf(int start, List<T> datas, T target){
        int index = -1;

        for(int i = start; i < datas.size(); i++){
            if(datas.get(i).equals(target)){
                index = i; break;
            }
        }
        return index;
    }

    /**
     * 合并注释
     * @param one
     * @param two
     * @return
     */
    public static <T> T mergeSelective(T one, T two) {
        T t = null;

        if(isAllNull(one,two)){
            return t;
        }

        t = findFirstNotNull(one,two);

        return t;
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
    public static <T> List<T> mergeListNoDuplicate(List<T> one, List<T> two){

        List<T> result = new ArrayList<>();

        TreeSet<T> set = new TreeSet<>();
        set.addAll(one);
        set.addAll(two);

        result.addAll(set);

        return result;
    }

    /**
     * 合并表达式集合
     * @param one
     * @param two
     * @return
     */
    public static <T> List<T> mergeListInOrder(List<T> one, List<T> two){
        List<T> results = new ArrayList<>();

        if(isAllNull(one,two)) return null;

        if(isAllNotNull(one,two)){

            int start = 0;
            for (int i = 0; i < one.size(); i++){
                T t = one.get(i);
                int index = indexOf(start,two,t);
                if(index == -1 || index == start){
                    results.add(t);
                }else {

                    results.addAll(two.subList(start, index));
                    start = index++;
                }
            }

            if(start < two.size()){
                results.addAll(two.subList(start, two.size()));
            }

        }else {
            results.addAll(findFirstNotNull(one, two));
        }

        return results;
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

            amd.setJavaDoc(mergeSelective(one.getJavaDoc(), two.getJavaDoc()));
            amd.setComment(mergeSelective(one.getComment(), two.getComment()));
            amd.setAnnotations(mergeListNoDuplicate(one.getAnnotations(), two.getAnnotations()));
            amd.setModifiers(mergeModifiers(one.getModifiers(),two.getModifiers()));
            amd.setName(one.getName());
            amd.setDefaultValue(mergeSelective(one.getDefaultValue(), two.getDefaultValue()));
            amd.setType(mergeSelective(one.getType(), two.getType()));

        }else {
            amd = findFirstNotNull(one,two);
        }

        return amd;

    }


    /**
     * 合并构造函数
     * @param one
     * @param two
     * @return
     */
    public static ConstructorDeclaration mergeConstructor(ConstructorDeclaration one, ConstructorDeclaration two){

        if(isAllNull(one,two)) return null;

        ConstructorDeclaration cd = null;

        if(isAllNotNull(one,two)){

            cd = new ConstructorDeclaration();

            cd.setName(one.getName());
            cd.setComment(mergeSelective(one.getComment(), two.getComment()));
            cd.setAnnotations(mergeListNoDuplicate(one.getAnnotations(), two.getAnnotations()));
            cd.setModifiers(mergeModifiers(one.getModifiers(), two.getModifiers()));
            cd.setJavaDoc( mergeSelective(one.getJavaDoc(), two.getJavaDoc()));
            cd.setThrows(mergeListNoDuplicate(one.getThrows(), two.getThrows()));
            cd.setTypeParameters(findFirstNotNull(one.getTypeParameters(),two.getTypeParameters()));

            // do not go further for now
            cd.setBlock(findFirstNotNull(one.getBlock(),two.getBlock()));

        }else {
            cd = findFirstNotNull(one,two);
        }

        return cd;
    }

    /**
     * 合并枚举常量声明
     * @param one
     * @param two
     * @return
     */
    public static EnumConstantDeclaration mergeEnumConstant(EnumConstantDeclaration one,EnumConstantDeclaration two){

        if(isAllNull(one,two)) return null;

        EnumConstantDeclaration ecd = null;

        if(isAllNotNull(one,two)){

            ecd = new EnumConstantDeclaration();

            ecd.setName(one.getName());
            ecd.setJavaDoc( mergeSelective(one.getJavaDoc(), two.getJavaDoc()));
            ecd.setComment(mergeSelective(one.getComment(), two.getComment()));
            ecd.setAnnotations(mergeListNoDuplicate(one.getAnnotations(), two.getAnnotations()));
            ecd.setArgs(mergeListInOrder(one.getArgs(), two.getArgs()));
            ecd.setClassBody(mergeBodies(one.getClassBody(),two.getClassBody()));

        }else {
            ecd = findFirstNotNull(one,two);
        }

        return ecd;
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

                    // merge type declaration
                    if(inner instanceof TypeDeclaration){
                        TypeDeclaration typeOne = (TypeDeclaration) outer;
                        TypeDeclaration typeTwo = (TypeDeclaration) inner;
                        if(typeOne.getName().equals(typeTwo.getName())){
                            result.add(mergeType(typeOne,typeTwo));
                        }

                    // merge annotation member declaration
                    } else if (inner instanceof AnnotationMemberDeclaration){
                        AnnotationMemberDeclaration amdOne = (AnnotationMemberDeclaration) outer;
                        AnnotationMemberDeclaration amdTwo = (AnnotationMemberDeclaration) inner;
                        if(amdOne.getName().equals(amdTwo)){
                            result.add(mergeAnnotationMember(amdOne,amdTwo));
                        }

                    // merge constructor declaration
                    } else if (inner instanceof ConstructorDeclaration){
                        ConstructorDeclaration cdOne = (ConstructorDeclaration) outer;
                        ConstructorDeclaration cdTwo = (ConstructorDeclaration) inner;
                        if(cdOne.getName().equals(cdTwo.getName()) && cdOne.getParameters().equals(cdTwo.getParameters())){
                            if(cdOne.getTypeParameters() != null && cdTwo.getTypeParameters() != null){
                                result.add(mergeConstructor(cdOne,cdTwo));
                            }else if(cdOne.getTypeParameters() == null && cdTwo.getTypeParameters() == null){
                                result.add(mergeConstructor(cdOne,cdTwo));
                            }
                        }

                    // merge empty member declaration
                    } else if (inner instanceof EmptyMemberDeclaration){
                        result.add(findFirstNotNull(outer,inner));

                    // merge enum constant declaration
                    } else if (inner instanceof EnumConstantDeclaration){
                        EnumConstantDeclaration ecdOne = (EnumConstantDeclaration) outer;
                        EnumConstantDeclaration ecdTwo = (EnumConstantDeclaration) inner;
                        if (ecdOne.getName().equals(ecdTwo.getName())){
                            result.add(mergeEnumConstant(ecdOne,ecdTwo));
                        }

                    // merge field declaration
                    } else if (inner instanceof FieldDeclaration){
                        FieldDeclaration fdOne = (FieldDeclaration) outer;
                        FieldDeclaration fdTwo = (FieldDeclaration) inner;

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
                    (JavadocComment) mergeSelective(one.getJavaDoc(), two.getJavaDoc()));

            annotationDeclaration.setComment(mergeSelective(one.getComment(), two.getComment()));

            annotationDeclaration.setAnnotations(
                    mergeListNoDuplicate(one.getAnnotations(), two.getAnnotations()));

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
        Comment fileComment = mergeSelective(one.getComment(), two.getComment());
        cu.setComment(fileComment);

        // check and merge imports
        List<ImportDeclaration> ids = mergeListNoDuplicate(one.getImports(), two.getImports());
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
