package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.UnaryExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class UnaryExprMerger extends AbstractMerger<UnaryExpr> {

  @Override public UnaryExpr merge(UnaryExpr first, UnaryExpr second) {
    UnaryExpr ue = new UnaryExpr();

    ue.setComment(mergeSingle(first.getComment(),second.getComment()));
    ue.setExpr(mergeSingle(first.getExpr(),second.getExpr()));
    ue.setOperator(first.getOperator());

    return ue;
  }

  @Override public boolean isEquals(UnaryExpr first, UnaryExpr second) {
    if(first == second) return true;

    if(!first.getOperator().equals(second.getOperator())) return false;
    if(!isEqualsUseMerger(first.getExpr(),second.getExpr())) return false;

    return true;
  }
}
