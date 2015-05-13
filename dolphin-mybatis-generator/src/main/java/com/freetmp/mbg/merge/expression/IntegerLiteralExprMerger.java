package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class IntegerLiteralExprMerger extends AbstractMerger<IntegerLiteralExpr> {

  @Override public IntegerLiteralExpr merge(IntegerLiteralExpr first, IntegerLiteralExpr second) {
    IntegerLiteralExpr ile = new IntegerLiteralExpr();

    ile.setComment(mergeSingle(first.getComment(),second.getComment()));
    ile.setValue(first.getValue());

    return ile;
  }

  @Override public boolean isEquals(IntegerLiteralExpr first, IntegerLiteralExpr second) {
    if(first == second) return true;

    if(!first.getValue().equals(second.getValue())) return false;

    return true;
  }
}
