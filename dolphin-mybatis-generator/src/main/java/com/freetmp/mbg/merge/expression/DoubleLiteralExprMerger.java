package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class DoubleLiteralExprMerger extends AbstractMerger<DoubleLiteralExpr> {

  @Override public DoubleLiteralExpr merge(DoubleLiteralExpr first, DoubleLiteralExpr second) {
    DoubleLiteralExpr dle = new DoubleLiteralExpr();

    dle.setComment(mergeSingle(first.getComment(),second.getComment()));
    dle.setValue(first.getValue());

    return dle;
  }

  @Override public boolean isEquals(DoubleLiteralExpr first, DoubleLiteralExpr second) {
    if(first == second) return true;

    if(!first.getValue().equals(second.getValue())) return false;

    return true;
  }
}
