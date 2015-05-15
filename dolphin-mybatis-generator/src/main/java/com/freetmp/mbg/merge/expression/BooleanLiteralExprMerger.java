package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class BooleanLiteralExprMerger extends AbstractMerger<BooleanLiteralExpr> {

  @Override public BooleanLiteralExpr doMerge(BooleanLiteralExpr first, BooleanLiteralExpr second) {
    BooleanLiteralExpr ble = new BooleanLiteralExpr();

    ble.setValue(first.getValue());

    return ble;
  }

  @Override public boolean doIsEquals(BooleanLiteralExpr first, BooleanLiteralExpr second) {

    if(first.getValue() != second.getValue()) return false;

    return true;
  }
}
