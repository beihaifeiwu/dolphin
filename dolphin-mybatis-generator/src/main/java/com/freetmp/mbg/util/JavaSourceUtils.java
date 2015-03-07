package com.freetmp.mbg.util;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.*;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by LiuPin on 2015/3/7.
 */
public class JavaSourceUtils {


    /**
     * 合并注释的分发函数
     * @param one
     * @param two
     * @return
     */
    public static Comment mergeComment(Comment one, Comment two) throws Exception {
        Comment fileComment = null;

        if(one == null && two == null){
            return fileComment;
        }

        // find class type and record comment content
        Class<? extends Comment> type = null;
        String content = null;
        if(one != null){
            type = one.getClass();
            content = one.getContent();
        }else{
            type = two.getClass();
            content = two.getContent();
        }

        fileComment = type.newInstance();
        fileComment.setContent(content);

        return fileComment;
    }

    /**
     * 合并引入类的声明
     * @param one
     * @param two
     * @return
     */
    public static List<ImportDeclaration> mergeImports(List<ImportDeclaration> one, List<ImportDeclaration> two) throws Exception {
        List<ImportDeclaration> result = new ArrayList<>();

        TreeSet<ImportDeclaration> merged = new TreeSet<>();
        merged.addAll(one);
        merged.addAll(two);

        for(ImportDeclaration declaration : merged){
            ImportDeclaration id = new ImportDeclaration();
            id.setComment(declaration.getComment());
            id.setAsterisk(declaration.isAsterisk());
            id.setName(declaration.getName());
            id.setStatic(declaration.isStatic());
            result.add(id);
        }

        return result;
    }


    /**
     * 合并类型声明的分发函数
     * @param one
     * @param two
     * @return
     */
    public static TypeDeclaration mergeType(TypeDeclaration one,TypeDeclaration two){
        TypeDeclaration type = null;

        //TODO merge type

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
