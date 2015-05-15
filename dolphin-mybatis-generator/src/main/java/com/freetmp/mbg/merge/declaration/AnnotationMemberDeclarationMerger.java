package com.freetmp.mbg.merge.declaration;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.body.AnnotationMemberDeclaration;

/**
 * Created by pin on 2015/4/19.
 */
public class AnnotationMemberDeclarationMerger extends AbstractMerger<AnnotationMemberDeclaration> {

  @Override
  public AnnotationMemberDeclaration doMerge(AnnotationMemberDeclaration first, AnnotationMemberDeclaration second) {

    AnnotationMemberDeclaration amd = new AnnotationMemberDeclaration();
    amd.setName(first.getName());
    amd.setType(mergeSingle(first.getType(),second.getType()));
    amd.setJavaDoc(mergeSingle(first.getJavaDoc(), second.getJavaDoc()));

    amd.setModifiers(mergeModifiers(first.getModifiers(), second.getModifiers()));
    amd.setAnnotations(mergeCollections(first.getAnnotations(), second.getAnnotations()));
    amd.setDefaultValue(mergeSingle(first.getDefaultValue(), second.getDefaultValue()));

    return amd;
  }

  @Override
  public boolean doIsEquals(AnnotationMemberDeclaration first, AnnotationMemberDeclaration second) {

    if (!isEqualsUseMerger(first.getType(), second.getType())) return false;
    if (!first.getName().equals(second.getName())) return false;

    return true;
  }
}
