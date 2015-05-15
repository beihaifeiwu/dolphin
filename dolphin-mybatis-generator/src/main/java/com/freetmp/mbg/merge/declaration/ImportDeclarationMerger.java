package com.freetmp.mbg.merge.declaration;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.ImportDeclaration;

/**
 * Created by LiuPin on 2015/5/15.
 */
public class ImportDeclarationMerger extends AbstractMerger<ImportDeclaration> {

  @Override public ImportDeclaration doMerge(ImportDeclaration first, ImportDeclaration second) {
    ImportDeclaration id = new ImportDeclaration();
    id.setName(mergeSingle(first.getName(),second.getName()));
    id.setAsterisk(first.isAsterisk());
    id.setStatic(first.isStatic());
    return id;
  }

  @Override public boolean doIsEquals(ImportDeclaration first, ImportDeclaration second) {
    if(!isEqualsUseMerger(first.getName(),second.getName())) return false;
    if(first.isStatic() != second.isStatic()) return false;
    if(first.isAsterisk() != second.isAsterisk()) return false;
    return true;
  }
}
