package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.AssignExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class AssignExprMerger extends AbstractMerger<AssignExpr> {

  @Override public AssignExpr merge(AssignExpr first, AssignExpr second) {
    AssignExpr ae = new AssignExpr();

    ae.setComment(mergeSingle(first.getComment(),second.getComment()));
    ae.setOperator(first.getOperator());
    ae.setTarget(mergeSingle(first.getTarget(),second.getTarget()));
    ae.setValue(mergeSingle(first.getValue(),second.getValue()));

    return ae;
  }

  @Override public boolean isEquals(AssignExpr first, AssignExpr second) {
    if(first == second) return true;

    if(!isEqualsUseMerger(first.getTarget(),second.getTarget())) return false;
    if(!isEqualsUseMerger(first.getValue(),second.getValue())) return false;
    if(first.getOperator().equals(second.getOperator())) return false;

    return true;
  }
}
