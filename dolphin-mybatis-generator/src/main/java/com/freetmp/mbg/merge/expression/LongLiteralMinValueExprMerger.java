package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.LongLiteralMinValueExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class LongLiteralMinValueExprMerger extends AbstractMerger<LongLiteralMinValueExpr> {

  @Override public LongLiteralMinValueExpr doMerge(LongLiteralMinValueExpr first, LongLiteralMinValueExpr second) {
    LongLiteralMinValueExpr llmve = new LongLiteralMinValueExpr();

    llmve.setValue(first.getValue());

    return llmve;
  }

  @Override public boolean doIsEquals(LongLiteralMinValueExpr first, LongLiteralMinValueExpr second) {

    if(!first.getValue().equals(second.getValue())) return false;

    return true;
  }
}
