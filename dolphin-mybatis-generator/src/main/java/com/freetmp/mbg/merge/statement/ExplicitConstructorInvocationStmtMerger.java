package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class ExplicitConstructorInvocationStmtMerger extends AbstractMerger<ExplicitConstructorInvocationStmt> {

  @Override public ExplicitConstructorInvocationStmt doMerge(ExplicitConstructorInvocationStmt first, ExplicitConstructorInvocationStmt second) {
    ExplicitConstructorInvocationStmt ecis = new ExplicitConstructorInvocationStmt();
    ecis.setArgs(mergeCollectionsInOrder(first.getArgs(),second.getArgs()));
    ecis.setTypeArgs(mergeCollectionsInOrder(first.getTypeArgs(),second.getTypeArgs()));
    ecis.setExpr(mergeSingle(first.getExpr(),second.getExpr()));
    ecis.setThis(first.isThis());
    return ecis;
  }

  @Override public boolean doIsEquals(ExplicitConstructorInvocationStmt first, ExplicitConstructorInvocationStmt second) {

    if(!isEqualsUseMerger(first.getExpr(),second.getExpr())) return false;
    if(!isEqualsUseMerger(first.getTypeArgs(),second.getTypeArgs())) return false;
    if(!isEqualsUseMerger(first.getArgs(),second.getArgs())) return false;
    if(first.isThis() != second.isThis()) return false;

    return true;
  }
}
