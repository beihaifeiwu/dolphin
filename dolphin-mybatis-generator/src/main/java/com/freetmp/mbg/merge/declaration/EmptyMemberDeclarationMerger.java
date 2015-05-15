package com.freetmp.mbg.merge.declaration;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.body.EmptyMemberDeclaration;

/**
 * Created by LiuPin on 2015/4/20.
 */
public class EmptyMemberDeclarationMerger extends AbstractMerger<EmptyMemberDeclaration> {

  @Override public EmptyMemberDeclaration doMerge(EmptyMemberDeclaration first, EmptyMemberDeclaration second) {
    EmptyMemberDeclaration emd = new EmptyMemberDeclaration();

    emd.setJavaDoc(mergeSingle(first.getJavaDoc(),second.getJavaDoc()));
    emd.setAnnotations(mergeCollections(first.getAnnotations(),second.getAnnotations()));

    return emd;
  }

  @Override public boolean doIsEquals(EmptyMemberDeclaration first, EmptyMemberDeclaration second) {
    return true;
  }
}
