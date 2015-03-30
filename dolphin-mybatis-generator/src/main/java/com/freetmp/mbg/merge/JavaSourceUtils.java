package com.freetmp.mbg.merge;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
 * Created by LiuPin on 2015/3/7.
 */
@SuppressWarnings("unchecked")
public class JavaSourceUtils {

    private static final Logger LOG = LoggerFactory.getLogger(JavaSourceUtils.class);


    public static <T> boolean isAllNull(T one, T two) {
        return one == null ? two == null : false;
    }

    public static <T> boolean isAllNotNull(T one, T two) {
        return one != null && two != null;
    }

    public static <T> T findFirstNotNull(T... types) {
        for (T type : types) {
            if (type != null) return type;
        }
        return null;
    }

    public static <T> int indexOf(int start, List<T> datas, T target) {
        int index = -1;

        for (int i = start; i < datas.size(); i++) {
            if (datas.get(i).equals(target)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public static <T> boolean isEitherContains(List<T> one, List<T> two) {
        if (!isAllNotNull(one, two)) return true;

        List<T> longer = one.size() > two.size() ? one : two;
        List<T> shorter = one.size() > two.size() ? two : one;

        boolean contains = true;

        for (T t : shorter) {
            if (!longer.contains(t)) {
                contains = false;
                break;
            }
        }

        return contains;

    }

    public static <T> T mergeSelective(T one, T two) {
        T t = null;

        if (isAllNull(one, two)) {
            return t;
        }

        t = findFirstNotNull(one, two);

        return t;
    }

    public static <T> boolean isListEquals(List<T> one, List two) {
        boolean isEqual = true;

        if (isAllNull(one, two)) {
            isEqual = true;
        } else if (isAllNotNull(one, two)) {
            if (one.size() != two.size()) {
                isEqual = false;
            } else {
                for (int index = 0; index < one.size(); index++) {
                    T t1 = one.get(index);
                    T t2 = one.get(index);
                    if (!t1.equals(t2)) {
                        isEqual = false;
                        break;
                    }
                }
            }
        } else {
            isEqual = false;
        }

        return isEqual;
    }

    public static <T extends BaseParameter> boolean isParametersEquals(List<T> one, List<T> two) {
        boolean isEqual = true;

        if (isAllNull(one, two)) {
            isEqual = true;
        } else if (isAllNotNull(one, two)) {
            if (one.size() != two.size()) {
                isEqual = false;
            } else {
                for (int index = 0; index < one.size(); index++) {
                    T t1 = one.get(index);
                    T t2 = one.get(index);
                    if (!t1.getId().equals(t2.getId())) {
                        isEqual = false;
                        break;
                    }
                }
            }
        } else {
            isEqual = false;
        }

        return isEqual;
    }

    /*
     * 合并修饰符
     */
    public static int mergeModifiers(int one, int two) {
        return ModifierSet.addModifier(one, two);
    }

    /*
     * 合并注解声明
     */
    public static <T> List<T> mergeListNoDuplicate(List<T> one, List<T> two) {

        if (isAllNull(one, two)) return null;

        List<T> result = new ArrayList<>();

        if (isAllNotNull(one, two)) {
            result.addAll(one);
            for (T t : two) {
                if (one.indexOf(t) == -1) {
                    result.add(t);
                }
            }
        } else {
            result.addAll(findFirstNotNull(one, two));
        }

        return result;
    }

    /*
     * 合并表达式集合
     */
    public static <T> List<T> mergeListInOrder(List<T> one, List<T> two) {
        List<T> results = new ArrayList<>();

        if (isAllNull(one, two)) return null;

        if (isAllNotNull(one, two)) {

            int start = 0;
            for (int i = 0; i < one.size(); i++) {
                T t = one.get(i);
                int index = indexOf(start, two, t);
                if (index == -1 || index == start) {
                    results.add(t);
                    start += 1;
                } else {

                    results.addAll(two.subList(start, ++index));
                    start = index;
                }
            }

            if (start < two.size()) {
                results.addAll(two.subList(start, two.size()));
            }

        } else {
            results.addAll(findFirstNotNull(one, two));
        }

        return results;
    }

    /*
     * 合并注解成员声明
     */
    public static AnnotationMemberDeclaration mergeAnnotationMember(
            AnnotationMemberDeclaration one, AnnotationMemberDeclaration two) {

        if (isAllNull(one, two)) return null;

        AnnotationMemberDeclaration amd = null;

        if (isAllNotNull(one, two)) {

            amd = new AnnotationMemberDeclaration();

            amd.setJavaDoc(mergeSelective(one.getJavaDoc(), two.getJavaDoc()));
            amd.setComment(mergeSelective(one.getComment(), two.getComment()));
            amd.setAnnotations(mergeListNoDuplicate(one.getAnnotations(), two.getAnnotations()));
            amd.setModifiers(mergeModifiers(one.getModifiers(), two.getModifiers()));
            amd.setName(one.getName());
            amd.setDefaultValue(mergeSelective(one.getDefaultValue(), two.getDefaultValue()));
            amd.setType(mergeSelective(one.getType(), two.getType()));

            LOG.info("merge AnnotationMemberDeclaration --> {}", amd.getName());

        } else {
            amd = findFirstNotNull(one, two);
            LOG.info("add AnnotationMemberDeclaration --> {}", amd.getName());
        }

        return amd;

    }

    /*
     * 合并代码块儿
     */
    public static BlockStmt mergeBlock(BlockStmt one, BlockStmt two) {

        if (isAllNull(one, two)) return null;

        BlockStmt blockStmt = null;
        if (isAllNotNull(one, two)) {

            blockStmt = new BlockStmt();
            blockStmt.setComment(mergeSelective(one.getComment(), two.getComment()));
            blockStmt.setStmts(mergeListInOrder(one.getStmts(), two.getStmts()));

        } else {
            blockStmt = findFirstNotNull(one, two);
        }
        return blockStmt;
    }

    /*
     * 合并参数列表
     */
    public static List<Parameter> mergeParameters(List<Parameter> one, List<Parameter> two) {
        if (isAllNull(one, two) || !isAllNotNull(one, two)) return null;

        if (one.size() != two.size()) return null;

        List<Parameter> result = new ArrayList<>();
        for (int index = 0; index < one.size(); index++) {
            Parameter p1 = one.get(index);
            Parameter p2 = two.get(index);

            Parameter np = new Parameter();
            np.setType(mergeSelective(p1.getType(), p2.getType()));
            np.setComment(mergeSelective(p1.getComment(), p2.getComment()));
            np.setAnnotations(mergeListNoDuplicate(p1.getAnnotations(), p2.getAnnotations()));
            np.setVarArgs(p1.isVarArgs());
            np.setId(p1.getId());

            result.add(np);
        }

        return result;
    }


    /*
     * 合并构造函数
     */
    public static ConstructorDeclaration mergeConstructor(ConstructorDeclaration one, ConstructorDeclaration two) {

        if (isAllNull(one, two)) return null;

        ConstructorDeclaration cd = null;

        if (isAllNotNull(one, two)) {

            cd = new ConstructorDeclaration();

            cd.setName(one.getName());
            cd.setComment(mergeSelective(one.getComment(), two.getComment()));
            cd.setAnnotations(mergeListNoDuplicate(one.getAnnotations(), two.getAnnotations()));
            cd.setModifiers(mergeModifiers(one.getModifiers(), two.getModifiers()));
            cd.setJavaDoc(mergeSelective(one.getJavaDoc(), two.getJavaDoc()));
            cd.setThrows(mergeListNoDuplicate(one.getThrows(), two.getThrows()));
            cd.setTypeParameters(findFirstNotNull(one.getTypeParameters(), two.getTypeParameters()));
            cd.setParameters(mergeParameters(one.getParameters(), two.getParameters()));

            cd.setBlock(mergeBlock(one.getBlock(), two.getBlock()));

            LOG.info("merge ConstructorDeclaration --> {}", cd.getName());

        } else {
            cd = findFirstNotNull(one, two);
            LOG.info("add ConstructorDeclaration --> {}", cd.getName());
        }

        return cd;
    }

    /*
     * 合并枚举常量声明
     */
    public static EnumConstantDeclaration mergeEnumConstant(EnumConstantDeclaration one, EnumConstantDeclaration two) {

        if (isAllNull(one, two)) return null;

        EnumConstantDeclaration ecd = null;

        if (isAllNotNull(one, two)) {

            ecd = new EnumConstantDeclaration();

            ecd.setName(one.getName());
            ecd.setJavaDoc(mergeSelective(one.getJavaDoc(), two.getJavaDoc()));
            ecd.setComment(mergeSelective(one.getComment(), two.getComment()));
            ecd.setAnnotations(mergeListNoDuplicate(one.getAnnotations(), two.getAnnotations()));
            ecd.setArgs(mergeListInOrder(one.getArgs(), two.getArgs()));
            ecd.setClassBody(mergeBodies(one.getClassBody(), two.getClassBody()));

            LOG.info("merge EnumConstantDeclaration --> {}", ecd.getName());

        } else {
            ecd = findFirstNotNull(one, two);
            LOG.info("add EnumConstantDeclaration --> {}", ecd.getName());
        }

        return ecd;
    }

    /*
     * 合并字段声明
     */
    public static FieldDeclaration mergeField(FieldDeclaration one, FieldDeclaration two) {

        if (isAllNull(one, two)) return null;

        FieldDeclaration fd = null;

        if (isAllNotNull(one, two)) {

            fd = new FieldDeclaration();
            fd.setType(mergeSelective(one.getType(), two.getType()));
            fd.setComment(mergeSelective(one.getComment(), two.getComment()));
            fd.setAnnotations(mergeListNoDuplicate(one.getAnnotations(), two.getAnnotations()));
            fd.setModifiers(mergeModifiers(one.getModifiers(), two.getModifiers()));
            fd.setJavaDoc(mergeSelective(one.getJavaDoc(), two.getJavaDoc()));
            fd.setVariables(mergeListNoDuplicate(one.getVariables(), two.getVariables()));

            LOG.info("merge FieldDeclaration --> {}", fd.getVariables());

        } else {
            fd = findFirstNotNull(one, two);
            LOG.info("add FieldDeclaration --> {}", fd.getVariables());
        }
        return fd;
    }

    /*
     * 合并初始化代码块
     */
    public static InitializerDeclaration mergeInitializer(InitializerDeclaration one, InitializerDeclaration two) {

        if (isAllNull(one, two)) return null;

        InitializerDeclaration id = null;

        if (isAllNotNull(one, two)) {
            if (one.isStatic() != two.isStatic()) return id;

            id = new InitializerDeclaration();
            id.setStatic(one.isStatic());
            id.setComment(mergeSelective(one.getComment(), two.getComment()));
            id.setJavaDoc(mergeSelective(one.getJavaDoc(), two.getJavaDoc()));
            id.setAnnotations(mergeListNoDuplicate(one.getAnnotations(), two.getAnnotations()));
            id.setBlock(mergeBlock(one.getBlock(), two.getBlock()));
            LOG.info("merge InitializerDeclaration --> {}", id.isStatic() ? "static { }" : "{ }");
        } else {
            id = findFirstNotNull(one, two);
            LOG.info("add InitializerDeclaration --> {}", id.isStatic() ? "static { }" : "{ }");
        }

        return id;
    }

    /*
     * 合并方法声明
     */
    public static MethodDeclaration mergeMethod(MethodDeclaration one, MethodDeclaration two) {
        if (isAllNull(one, two)) return null;

        MethodDeclaration md = null;

        if (isAllNotNull(one, two)) {

            md = new MethodDeclaration();
            md.setName(one.getName());
            md.setType(mergeSelective(one.getType(), two.getType()));
            md.setParameters(mergeParameters(one.getParameters(), two.getParameters()));
            md.setTypeParameters(findFirstNotNull(one.getTypeParameters(), two.getTypeParameters()));
            md.setThrows(mergeListNoDuplicate(one.getThrows(), two.getThrows()));
            md.setAnnotations(mergeListNoDuplicate(one.getAnnotations(), two.getAnnotations()));

            md.setArrayCount(one.getArrayCount());

            md.setModifiers(mergeModifiers(one.getModifiers(), two.getModifiers()));
            md.setBody(mergeBlock(one.getBody(), two.getBody()));
            md.setJavaDoc(mergeSelective(one.getJavaDoc(), two.getJavaDoc()));

            LOG.info("merge MethodDeclaration --> {}", md.getName());
        } else {
            md = findFirstNotNull(one, two);
            LOG.info("add MethodDeclaration --> {}", md.getName());
        }

        return md;
    }

    /*
     * 合并内容
     */
    @SuppressWarnings("uncheck")
    public static List<BodyDeclaration> mergeBodies(List<BodyDeclaration> one, List<BodyDeclaration> two) {
        List<BodyDeclaration> result = new ArrayList<>();

        List<BodyDeclaration> notMatched = new ArrayList<>();
        notMatched.addAll(two);

        for (BodyDeclaration outer : one) {

            boolean found = false;

            for (Iterator<BodyDeclaration> iterator = notMatched.iterator(); iterator.hasNext(); ) {

                BodyDeclaration inner = iterator.next();

                // only type matched can carry on
                if (inner.getClass().equals(outer.getClass())) {

                    // merge type declaration
                    if (inner instanceof TypeDeclaration) {
                        TypeDeclaration typeOne = (TypeDeclaration) outer;
                        TypeDeclaration typeTwo = (TypeDeclaration) inner;
                        if (typeOne.getName().equals(typeTwo.getName())) {
                            result.add(mergeType(typeOne, typeTwo));
                            found = true;
                            iterator.remove();
                        }

                        // merge annotation member declaration
                    } else if (inner instanceof AnnotationMemberDeclaration) {
                        AnnotationMemberDeclaration amdOne = (AnnotationMemberDeclaration) outer;
                        AnnotationMemberDeclaration amdTwo = (AnnotationMemberDeclaration) inner;
                        if (amdOne.getName().equals(amdTwo)) {
                            result.add(mergeAnnotationMember(amdOne, amdTwo));
                            found = true;
                            iterator.remove();
                        }

                        // merge constructor declaration
                    } else if (inner instanceof ConstructorDeclaration) {
                        ConstructorDeclaration cdOne = (ConstructorDeclaration) outer;
                        ConstructorDeclaration cdTwo = (ConstructorDeclaration) inner;
                        if (cdOne.getName().equals(cdTwo.getName()) &&
                                isParametersEquals(cdOne.getParameters(), cdTwo.getParameters()) &&
                                isListEquals(cdOne.getTypeParameters(), cdTwo.getTypeParameters())) {

                            result.add(mergeConstructor(cdOne, cdTwo));
                            found = true;
                            iterator.remove();

                        }

                        // merge empty member declaration
                    } else if (inner instanceof EmptyMemberDeclaration) {
                        result.add(mergeSelective(outer, inner));
                        found = true;
                        iterator.remove();

                        // merge enum constant declaration
                    } else if (inner instanceof EnumConstantDeclaration) {
                        EnumConstantDeclaration ecdOne = (EnumConstantDeclaration) outer;
                        EnumConstantDeclaration ecdTwo = (EnumConstantDeclaration) inner;
                        if (ecdOne.getName().equals(ecdTwo.getName())) {
                            result.add(mergeEnumConstant(ecdOne, ecdTwo));
                            found = true;
                            iterator.remove();
                        }

                        // merge field declaration
                    } else if (inner instanceof FieldDeclaration) {
                        FieldDeclaration fdOne = (FieldDeclaration) outer;
                        FieldDeclaration fdTwo = (FieldDeclaration) inner;

                        if (isEitherContains(fdOne.getVariables(), fdTwo.getVariables())
                                && fdOne.getType().equals(fdTwo.getType())) {
                            result.add(mergeField(fdOne, fdTwo));
                            found = true;
                            iterator.remove();
                        }

                        // merge initializer declaration
                    } else if (inner instanceof InitializerDeclaration) {

                        InitializerDeclaration idOne = (InitializerDeclaration) outer;
                        InitializerDeclaration idTwo = (InitializerDeclaration) inner;

                        if (idOne.isStatic() == idTwo.isStatic()) {
                            result.add(mergeInitializer(idOne, idTwo));
                            found = true;
                            iterator.remove();
                        }

                        // merge method declaration
                    } else if (inner instanceof MethodDeclaration) {

                        MethodDeclaration mdOne = (MethodDeclaration) outer;
                        MethodDeclaration mdTwo = (MethodDeclaration) inner;

                        if (mdOne.getName().equals(mdTwo.getName()) &&
                                isParametersEquals(mdOne.getParameters(), mdTwo.getParameters()) &&
                                isListEquals(mdOne.getTypeParameters(), mdTwo.getTypeParameters())) {
                            result.add(mergeMethod(mdOne, mdTwo));
                            found = true;
                            iterator.remove();
                        }
                    }

                }

            }

            if (!found) {
                result.add(outer);
            }
        }

        result.addAll(notMatched);

        return result;
    }

    /*
     * 合并注解声明
     */
    public static AnnotationDeclaration mergeType(AnnotationDeclaration one, AnnotationDeclaration two) {

        if (isAllNull(one, two)) return null;

        AnnotationDeclaration annotationDeclaration = null;

        if (isAllNotNull(one, two)) {

            annotationDeclaration = new AnnotationDeclaration();

            annotationDeclaration.setModifiers(
                    mergeModifiers(one.getModifiers(), two.getModifiers()));

            annotationDeclaration.setJavaDoc(
                    (JavadocComment) mergeSelective(one.getJavaDoc(), two.getJavaDoc()));

            annotationDeclaration.setComment(mergeSelective(one.getComment(), two.getComment()));

            annotationDeclaration.setAnnotations(
                    mergeListNoDuplicate(one.getAnnotations(), two.getAnnotations()));

            // merge content body
            annotationDeclaration.setMembers(mergeBodies(one.getMembers(), two.getMembers()));

            LOG.info("merge AnnotationDeclaration --> {}", annotationDeclaration.getName());

        } else {
            annotationDeclaration = findFirstNotNull(one, two);
            LOG.info("add AnnotationDeclaration --> {}", annotationDeclaration.getName());
        }

        return annotationDeclaration;
    }

    /*
     * 合并类或接口类型声明
     */
    public static ClassOrInterfaceDeclaration mergeType(ClassOrInterfaceDeclaration one, ClassOrInterfaceDeclaration two) {
        if (isAllNull(one, two)) return null;

        ClassOrInterfaceDeclaration coid = null;

        if (isAllNotNull(one, two)) {

            coid = new ClassOrInterfaceDeclaration();
            coid.setName(one.getName());
            coid.setJavaDoc(mergeSelective(one.getJavaDoc(), two.getJavaDoc()));
            coid.setComment(mergeSelective(one.getComment(), two.getComment()));
            coid.setAnnotations(mergeListNoDuplicate(one.getAnnotations(), two.getAnnotations()));
            coid.setModifiers(mergeModifiers(one.getModifiers(), two.getModifiers()));
            coid.setExtends(mergeListNoDuplicate(one.getExtends(), two.getExtends()));
            coid.setImplements(mergeListNoDuplicate(one.getImplements(), two.getImplements()));
            coid.setTypeParameters(mergeSelective(one.getTypeParameters(), two.getTypeParameters()));
            coid.setInterface(one.isInterface());
            coid.setMembers(mergeBodies(one.getMembers(), two.getMembers()));

            LOG.info("merge ClassOrInterfaceDeclaration --> {}", coid.getName());

        } else {
            coid = findFirstNotNull(one, two);
            LOG.info("add ClassOrInterfaceDeclaration --> {}", coid.getName());
        }
        return coid;
    }

    /*
     * 合并空类型声明
     */
    public static EmptyTypeDeclaration mergeType(EmptyTypeDeclaration one, EmptyTypeDeclaration two) {
        if (isAllNull(one, two)) return null;

        EmptyTypeDeclaration etd = null;

        if (isAllNotNull(one, two)) {

            etd = new EmptyTypeDeclaration();
            etd.setName(one.getName());
            etd.setJavaDoc(mergeSelective(one.getJavaDoc(), two.getJavaDoc()));
            etd.setComment(mergeSelective(one.getComment(), two.getComment()));
            etd.setAnnotations(mergeListNoDuplicate(one.getAnnotations(), two.getAnnotations()));
            etd.setMembers(mergeBodies(one.getMembers(), two.getMembers()));
            etd.setModifiers(mergeModifiers(one.getModifiers(), two.getModifiers()));

            LOG.info("merge EmptyTypeDeclaration --> {}", etd.getName());
        } else {
            etd = findFirstNotNull(one, two);
            LOG.info("add EmptyTypeDeclaration --> {}", etd.getName());
        }
        return etd;
    }

    /*
     * 合并枚举常量集合
     */
    public static List<EnumConstantDeclaration> mergeEnumConstants(List<EnumConstantDeclaration> one, List<EnumConstantDeclaration> two) {
        if (isAllNull(one, two)) return null;

        List<EnumConstantDeclaration> ecds = null;

        if (isAllNotNull(one, two)) {

            ecds = new ArrayList<>();

            List<EnumConstantDeclaration> notMatched = new ArrayList<>();
            notMatched.addAll(two);

            for (EnumConstantDeclaration outer : one) {

                boolean found = false;

                for (Iterator<EnumConstantDeclaration> iterator = notMatched.iterator(); iterator.hasNext(); ) {
                    EnumConstantDeclaration inner = iterator.next();

                    if (inner.getName().equals(outer.getName())) {
                        ecds.add(mergeEnumConstant(outer, inner));
                        found = true;
                        iterator.remove();
                    }
                }

                if (!found) {
                    ecds.add(outer);
                    LOG.info("add EnumConstantDeclaration --> {}", outer.getName());
                }
            }

            for (EnumConstantDeclaration inner : notMatched) {
                ecds.add(inner);
                LOG.info("add EnumConstantDeclaration --> {}", inner.getName());
            }

        } else {
            ecds = findFirstNotNull(one, two);
            for (EnumConstantDeclaration ecd : ecds) {
                LOG.info("add EnumConstantDeclaration --> {}", ecd.getName());
            }
        }

        return ecds;
    }

    /*
     * 合并枚举类型声明
     */
    public static EnumDeclaration mergeType(EnumDeclaration one, EnumDeclaration two) {

        if (isAllNull(one, two)) return null;

        EnumDeclaration ed = null;

        if (isAllNotNull(one, two)) {

            ed = new EnumDeclaration();
            ed.setJavaDoc(mergeSelective(one.getJavaDoc(), two.getJavaDoc()));
            ed.setComment(mergeSelective(one.getComment(), one.getComment()));
            ed.setModifiers(mergeModifiers(one.getModifiers(), two.getModifiers()));
            ed.setAnnotations(mergeListNoDuplicate(one.getAnnotations(), two.getAnnotations()));
            ed.setImplements(mergeListNoDuplicate(one.getImplements(), two.getImplements()));
            ed.setName(one.getName());
            ed.setEntries(mergeEnumConstants(one.getEntries(), two.getEntries()));
            LOG.info("merge EnumDeclaration --> {}", ed.getName());

        } else {
            ed = findFirstNotNull(one, two);
            LOG.info("add EnumDeclaration --> {}", ed.getName());
        }

        return ed;
    }


    /*
     * 合并类型声明的分发函数
     */
    @SuppressWarnings("uncheck")
    public static TypeDeclaration mergeType(TypeDeclaration one, TypeDeclaration two) {
        TypeDeclaration type = null;

        if (isAllNull(one, two)) return null;

        if (isAllNotNull(one, two)) {
            // just ignore when class type are not same
            if (one.getClass().equals(two.getClass())) {

                if (one instanceof AnnotationDeclaration) {

                    type = mergeType((AnnotationDeclaration) one, (AnnotationDeclaration) two);
                } else if (one instanceof ClassOrInterfaceDeclaration) {

                    type = mergeType((ClassOrInterfaceDeclaration) one, (ClassOrInterfaceDeclaration) two);
                } else if (one instanceof EmptyTypeDeclaration) {

                    type = mergeType((EmptyTypeDeclaration) one, (EmptyTypeDeclaration) two);
                } else if (one instanceof EnumDeclaration) {
                    type = mergeType((EnumDeclaration) one, (EnumDeclaration) two);
                }

            }

        } else {
            type = findFirstNotNull(one, two);
            LOG.info("add {} --> {}", type.getClass().getSimpleName(), type.getName());
        }

        return type;
    }

    /*
     * 合并类型声明
     * @throws Exception
     */
    public static List<TypeDeclaration> mergeTypes(List<TypeDeclaration> one, List<TypeDeclaration> two) throws Exception {
        List<TypeDeclaration> result = new ArrayList<>();

        for (TypeDeclaration outer : one) {

            boolean found = false;
            for (TypeDeclaration inner : two) {

                if (outer.getName().equals(inner.getName())) {
                    TypeDeclaration merged = mergeType(outer, inner);
                    result.add(merged);
                    found = true;
                }
            }

            if (!found) {
                result.add(outer);
                LOG.info("add {} --> {}", outer.getClass().getSimpleName(), outer.getName());
            }
        }
        return result;
    }

    /*
     * 合并两个编译单元的内容
     */
    public static String mergeContent(CompilationUnit one, CompilationUnit two) throws Exception {

        // 包声明不同，返回null
        if (!one.getPackage().equals(two.getPackage())) return null;

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
        List<TypeDeclaration> types = mergeTypes(one.getTypes(), two.getTypes());
        cu.setTypes(types);

        return cu.toString();
    }

    /*
     * 合并两个源Java source中的内容
     */
    public static String mergeContent(String one, String two) throws Exception {
        return mergeContent(generateAst(one), generateAst(two));
    }

    /*
     * 根据字符串生成编译单元
     * @throws ParseException
     */
    public static CompilationUnit generateAst(String source) throws ParseException {
        return JavaParser.parse(new StringReader(source), true);
    }
}
