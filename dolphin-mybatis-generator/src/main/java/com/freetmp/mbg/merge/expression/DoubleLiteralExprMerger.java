package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class DoubleLiteralExprMerger extends AbstractMerger<DoubleLiteralExpr> {

  @Override public DoubleLiteralExpr doMerge(DoubleLiteralExpr first, DoubleLiteralExpr second) {
    DoubleLiteralExpr dle = new DoubleLiteralExpr();

    dle.setValue(first.getValue());

    return dle;
  }

  @Override public boolean doIsEquals(DoubleLiteralExpr first, DoubleLiteralExpr second) {

    if(!first.getValue().equals(second.getValue())) return false;

    return true;
  }
}
