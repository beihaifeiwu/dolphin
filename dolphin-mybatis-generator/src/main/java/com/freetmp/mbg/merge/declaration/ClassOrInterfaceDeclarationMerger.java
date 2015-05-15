package com.freetmp.mbg.merge.declaration;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

/**
 * Created by LiuPin on 2015/3/27.
 */
public class ClassOrInterfaceDeclarationMerger extends AbstractMerger<ClassOrInterfaceDeclaration> {

  @Override
  public ClassOrInterfaceDeclaration doMerge(ClassOrInterfaceDeclaration first, ClassOrInterfaceDeclaration second) {

    ClassOrInterfaceDeclaration declaration = new ClassOrInterfaceDeclaration();

    declaration.setInterface(first.isInterface());
    declaration.setName(first.getName());

    declaration.setModifiers(mergeModifiers(first.getModifiers(), second.getModifiers()));
    declaration.setJavaDoc(mergeSingle(first.getJavaDoc(), second.getJavaDoc()));
    declaration.setTypeParameters(mergeCollections(first.getTypeParameters(), second.getTypeParameters()));

    declaration.setImplements(mergeCollections(first.getImplements(), second.getImplements()));
    declaration.setExtends(mergeCollections(first.getExtends(), second.getExtends()));

    declaration.setAnnotations(mergeCollections(first.getAnnotations(), second.getAnnotations()));
    declaration.setMembers(mergeCollections(first.getMembers(), second.getMembers()));

    return declaration;
  }

  @Override
  public boolean doIsEquals(ClassOrInterfaceDeclaration first, ClassOrInterfaceDeclaration second) {

    if (first.getName().equals(second.getName())) return true;

    return false;
  }
}
