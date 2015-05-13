package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.ThisExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class ThisExprMerger extends AbstractMerger<ThisExpr> {

  @Override public ThisExpr merge(ThisExpr first, ThisExpr second) {
    ThisExpr te = new ThisExpr();

    te.setComment(mergeSingle(first.getComment(),second.getComment()));
    te.setClassExpr(mergeSingle(first.getClassExpr(),second.getClassExpr()));

    return te;
  }

  @Override public boolean isEquals(ThisExpr first, ThisExpr second) {
    if(first == second) return true;

    if(!isEqualsUseMerger(first.getClassExpr(),second.getClassExpr())) return false;

    return true;
  }
}
