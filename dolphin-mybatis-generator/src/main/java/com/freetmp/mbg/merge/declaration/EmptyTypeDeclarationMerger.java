package com.freetmp.mbg.merge.declaration;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.body.EmptyTypeDeclaration;

/**
 * Created by pin on 2015/4/19.
 */
public class EmptyTypeDeclarationMerger extends AbstractMerger<EmptyTypeDeclaration> {

  @Override
  public EmptyTypeDeclaration doMerge(EmptyTypeDeclaration first, EmptyTypeDeclaration second) {
    EmptyTypeDeclaration etd = new EmptyTypeDeclaration();

    etd.setJavaDoc(mergeSingle(first.getJavaDoc(),second.getJavaDoc()));
    etd.setAnnotations(mergeCollections(first.getAnnotations(),second.getAnnotations()));
    etd.setModifiers(mergeModifiers(first.getModifiers(),second.getModifiers()));
    etd.setNameExpr(mergeSingle(first.getNameExpr(),second.getNameExpr()));
    etd.setMembers(mergeCollections(first.getMembers(),second.getMembers()));

    return etd;
  }

  @Override
  public boolean doIsEquals(EmptyTypeDeclaration first, EmptyTypeDeclaration second) {
    return true;
  }
}
