package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.SuperExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class SuperExprMerger extends AbstractMerger<SuperExpr> {

  @Override public SuperExpr merge(SuperExpr first, SuperExpr second) {
    SuperExpr se = new SuperExpr();

    se.setComment(mergeSingle(first.getComment(),second.getComment()));
    se.setClassExpr(mergeSingle(first.getClassExpr(),second.getClassExpr()));

    return se;
  }

  @Override public boolean isEquals(SuperExpr first, SuperExpr second) {
    if(first == second) return true;

    if(!isEqualsUseMerger(first.getClassExpr(),second.getClassExpr())) return false;

    return true;
  }
}
