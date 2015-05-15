package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.IntegerLiteralMinValueExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class IntegerLiteralMinValueExprMerger extends AbstractMerger<IntegerLiteralMinValueExpr> {

  @Override public IntegerLiteralMinValueExpr doMerge(IntegerLiteralMinValueExpr first, IntegerLiteralMinValueExpr second) {
    IntegerLiteralMinValueExpr ilmve = new IntegerLiteralMinValueExpr();

    ilmve.setValue(first.getValue());

    return ilmve;
  }

  @Override public boolean doIsEquals(IntegerLiteralMinValueExpr first, IntegerLiteralMinValueExpr second) {

    if(!first.getValue().equals(second.getValue())) return false;

    return true;
  }
}
