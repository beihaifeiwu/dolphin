package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.InstanceOfExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class InstanceOfExprMerger extends AbstractMerger<InstanceOfExpr> {

  @Override public InstanceOfExpr merge(InstanceOfExpr first, InstanceOfExpr second) {
    InstanceOfExpr ioe = new InstanceOfExpr();

    ioe.setComment(mergeSingle(first.getComment(),second.getComment()));
    ioe.setExpr(mergeSingle(first.getExpr(),second.getExpr()));
    ioe.setType(mergeSingle(first.getType(),second.getType()));

    return ioe;
  }

  @Override public boolean isEquals(InstanceOfExpr first, InstanceOfExpr second) {
    if(first == second) return true;

    if(!isEqualsUseMerger(first.getType(),second.getType())) return false;
    if(!isEqualsUseMerger(first.getExpr(),second.getExpr())) return false;

    return true;
  }
}
