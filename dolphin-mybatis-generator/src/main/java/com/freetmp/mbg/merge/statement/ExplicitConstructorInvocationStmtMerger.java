package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class ExplicitConstructorInvocationStmtMerger extends AbstractMerger<ExplicitConstructorInvocationStmt> {

  @Override public ExplicitConstructorInvocationStmt merge(ExplicitConstructorInvocationStmt first, ExplicitConstructorInvocationStmt second) {
    ExplicitConstructorInvocationStmt ecis = new ExplicitConstructorInvocationStmt();
    ecis.setComment(mergeSingle(first.getComment(),second.getComment()));
    ecis.setArgs(mergeCollectionsInOrder(first.getArgs(),second.getArgs()));
    ecis.setTypeArgs(mergeCollectionsInOrder(first.getTypeArgs(),second.getTypeArgs()));
    ecis.setExpr(mergeSingle(first.getExpr(),second.getExpr()));
    ecis.setThis(first.isThis());
    return ecis;
  }

  @Override public boolean isEquals(ExplicitConstructorInvocationStmt first, ExplicitConstructorInvocationStmt second) {
    if(first == second) return true;

    if(!isEqualsUseMerger(first.getExpr(),second.getExpr())) return false;
    if(!isEqualsUseMerger(first.getTypeArgs(),second.getTypeArgs())) return false;
    if(!isEqualsUseMerger(first.getArgs(),second.getArgs())) return false;
    if(first.isThis() != second.isThis()) return false;

    return true;
  }
}
