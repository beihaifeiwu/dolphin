package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.TypeDeclarationStmt;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class TypeDeclarationStmtMerger extends AbstractMerger<TypeDeclarationStmt> {

  @Override public TypeDeclarationStmt doMerge(TypeDeclarationStmt first, TypeDeclarationStmt second) {
    TypeDeclarationStmt tds = new TypeDeclarationStmt();
    tds.setTypeDeclaration(mergeSingle(first.getTypeDeclaration(),second.getTypeDeclaration()));
    return tds;
  }

  @Override public boolean doIsEquals(TypeDeclarationStmt first, TypeDeclarationStmt second) {

    if(!isEqualsUseMerger(first.getTypeDeclaration(),second.getTypeDeclaration())) return false;

    return true;
  }
}
