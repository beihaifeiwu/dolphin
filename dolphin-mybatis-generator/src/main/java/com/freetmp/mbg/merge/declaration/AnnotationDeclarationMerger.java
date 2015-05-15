package com.freetmp.mbg.merge.declaration;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by pin on 2015/4/19.
 */
public class AnnotationDeclarationMerger extends AbstractMerger<AnnotationDeclaration> {

  @Override
  public AnnotationDeclaration doMerge(AnnotationDeclaration first, AnnotationDeclaration second) {

    AnnotationDeclaration ad = new AnnotationDeclaration();

    ad.setJavaDoc(mergeSingle(first.getJavaDoc(), second.getJavaDoc()));
    ad.setMembers(mergeCollections(first.getMembers(), second.getMembers()));
    ad.setAnnotations(mergeCollections(first.getAnnotations(), second.getAnnotations()));
    ad.setModifiers(mergeModifiers(first.getModifiers(), second.getModifiers()));

    return ad;
  }

  @Override
  public boolean doIsEquals(AnnotationDeclaration first, AnnotationDeclaration second) {

    return StringUtils.equals(first.getName(), second.getName());
  }

}