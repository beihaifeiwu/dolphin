package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class BooleanLiteralExprMerger extends AbstractMerger<BooleanLiteralExpr> {

  @Override public BooleanLiteralExpr merge(BooleanLiteralExpr first, BooleanLiteralExpr second) {
    BooleanLiteralExpr ble = new BooleanLiteralExpr();

    ble.setComment(mergeSingle(first.getComment(),second.getComment()));
    ble.setValue(first.getValue());

    return ble;
  }

  @Override public boolean isEquals(BooleanLiteralExpr first, BooleanLiteralExpr second) {
    if (first == second) return true;

    if(first.getValue() != second.getValue()) return false;

    return true;
  }
}
