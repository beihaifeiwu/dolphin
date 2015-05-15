package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.ConditionalExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class ConditionalExprMerger extends AbstractMerger<ConditionalExpr> {

  @Override public ConditionalExpr doMerge(ConditionalExpr first, ConditionalExpr second) {
    ConditionalExpr ce = new ConditionalExpr();

    ce.setCondition(mergeSingle(first.getCondition(),second.getCondition()));
    ce.setElseExpr(mergeSingle(first.getElseExpr(),second.getElseExpr()));
    ce.setThenExpr(mergeSingle(first.getThenExpr(),second.getThenExpr()));

    return ce;
  }

  @Override public boolean doIsEquals(ConditionalExpr first, ConditionalExpr second) {

    if(!isEqualsUseMerger(first.getCondition(),second.getCondition())) return false;
    if(!isEqualsUseMerger(first.getElseExpr(), second.getElseExpr())) return false;
    if(!isEqualsUseMerger(first.getThenExpr(),second.getThenExpr())) return false;

    return true;
  }
}
