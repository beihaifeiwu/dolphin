package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.CastExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class CastExprMerger extends AbstractMerger<CastExpr> {

  @Override public CastExpr merge(CastExpr first, CastExpr second) {
    CastExpr ce = new CastExpr();

    ce.setComment(mergeSingle(first.getComment(),second.getComment()));
    ce.setType(mergeSingle(first.getType(),second.getType()));
    ce.setExpr(mergeSingle(first.getExpr(),second.getExpr()));

    return ce;
  }

  @Override public boolean isEquals(CastExpr first, CastExpr second) {
    if(first == second) return true;

    if(!isEqualsUseMerger(first.getType(),second.getType())) return false;
    if(!isEqualsUseMerger(first.getExpr(),second.getExpr())) return false;

    return true;
  }
}
