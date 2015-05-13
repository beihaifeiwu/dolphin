package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.TypeDeclarationStmt;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class TypeDeclarationStmtMerger extends AbstractMerger<TypeDeclarationStmt> {

  @Override public TypeDeclarationStmt merge(TypeDeclarationStmt first, TypeDeclarationStmt second) {
    TypeDeclarationStmt tds = new TypeDeclarationStmt();
    tds.setComment(mergeSingle(first.getComment(),second.getComment()));
    tds.setTypeDeclaration(mergeSingle(first.getTypeDeclaration(),second.getTypeDeclaration()));
    return tds;
  }

  @Override public boolean isEquals(TypeDeclarationStmt first, TypeDeclarationStmt second) {
    if(first == second) return true;

    if(!isEqualsUseMerger(first.getTypeDeclaration(),second.getTypeDeclaration())) return false;

    return true;
  }
}
