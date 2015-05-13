package com.freetmp.mbg.merge.declaration;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.body.AnnotationMemberDeclaration;

/**
 * Created by pin on 2015/4/19.
 */
public class AnnotationMemberDeclarationMerger extends AbstractMerger<AnnotationMemberDeclaration> {

  @Override
  public AnnotationMemberDeclaration merge(AnnotationMemberDeclaration first, AnnotationMemberDeclaration second) {

    if (first.getDefaultValue() == null) return second;
    if (second.getDefaultValue() == null) return first;

    AnnotationMemberDeclaration amd = new AnnotationMemberDeclaration();
    amd.setName(first.getName());
    amd.setType(mergeSingle(first.getType(),second.getType()));
    amd.setJavaDoc(mergeSingle(first.getJavaDoc(), second.getJavaDoc()));
    amd.setComment(mergeSingle(first.getComment(), second.getComment()));

    amd.setModifiers(mergeModifiers(first.getModifiers(), second.getModifiers()));
    amd.setAnnotations(mergeCollections(first.getAnnotations(), second.getAnnotations()));
    amd.setDefaultValue(mergeSingle(first.getDefaultValue(), second.getDefaultValue()));

    return amd;
  }

  @Override
  public boolean isEquals(AnnotationMemberDeclaration first, AnnotationMemberDeclaration second) {
    if (first == second) return true;
    if (first == null || second == null) return false;

    if (!isEqualsUseMerger(first.getType(), second.getType())) return false;
    if (!first.getName().equals(second.getName())) return false;

    return true;
  }
}
