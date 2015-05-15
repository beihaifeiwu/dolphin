package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class IntegerLiteralExprMerger extends AbstractMerger<IntegerLiteralExpr> {

  @Override public IntegerLiteralExpr doMerge(IntegerLiteralExpr first, IntegerLiteralExpr second) {
    IntegerLiteralExpr ile = new IntegerLiteralExpr();

    ile.setValue(first.getValue());

    return ile;
  }

  @Override public boolean doIsEquals(IntegerLiteralExpr first, IntegerLiteralExpr second) {

    if(!first.getValue().equals(second.getValue())) return false;

    return true;
  }
}
