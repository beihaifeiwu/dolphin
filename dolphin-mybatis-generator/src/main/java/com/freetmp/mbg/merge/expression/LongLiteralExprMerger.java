package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.LongLiteralExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class LongLiteralExprMerger extends AbstractMerger<LongLiteralExpr> {

  @Override public LongLiteralExpr merge(LongLiteralExpr first, LongLiteralExpr second) {
    LongLiteralExpr lle = new LongLiteralExpr();

    lle.setComment(mergeSingle(first.getComment(),second.getComment()));
    lle.setValue(first.getValue());

    return lle;
  }

  @Override public boolean isEquals(LongLiteralExpr first, LongLiteralExpr second) {
    if(first == second) return true;

    if(!first.getValue().equals(second.getValue())) return false;

    return true;
  }
}
