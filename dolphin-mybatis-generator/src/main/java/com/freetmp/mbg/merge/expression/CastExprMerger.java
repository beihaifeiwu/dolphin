package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.CastExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class CastExprMerger extends AbstractMerger<CastExpr> {

  @Override public CastExpr doMerge(CastExpr first, CastExpr second) {
    CastExpr ce = new CastExpr();
    ce.setType(mergeSingle(first.getType(),second.getType()));
    ce.setExpr(mergeSingle(first.getExpr(),second.getExpr()));

    return ce;
  }

  @Override public boolean doIsEquals(CastExpr first, CastExpr second) {

    if(!isEqualsUseMerger(first.getType(),second.getType())) return false;
    if(!isEqualsUseMerger(first.getExpr(),second.getExpr())) return false;

    return true;
  }
}
