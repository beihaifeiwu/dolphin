package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.IntegerLiteralMinValueExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class IntegerLiteralMinValueExprMerger extends AbstractMerger<IntegerLiteralMinValueExpr> {

  @Override public IntegerLiteralMinValueExpr merge(IntegerLiteralMinValueExpr first, IntegerLiteralMinValueExpr second) {
    IntegerLiteralMinValueExpr ilmve = new IntegerLiteralMinValueExpr();

    ilmve.setComment(mergeSingle(first.getComment(),second.getComment()));
    ilmve.setValue(first.getValue());

    return ilmve;
  }

  @Override public boolean isEquals(IntegerLiteralMinValueExpr first, IntegerLiteralMinValueExpr second) {
    if(first == second) return true;

    if(!first.getValue().equals(second.getValue())) return false;

    return true;
  }
}
