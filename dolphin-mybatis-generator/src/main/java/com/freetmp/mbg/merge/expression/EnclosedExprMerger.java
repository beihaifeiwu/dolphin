package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.EnclosedExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class EnclosedExprMerger extends AbstractMerger<EnclosedExpr> {

  @Override public EnclosedExpr doMerge(EnclosedExpr first, EnclosedExpr second) {
    EnclosedExpr ee = new EnclosedExpr();

    ee.setInner(mergeSingle(first.getInner(),second.getInner()));

    return ee;
  }

  @Override public boolean doIsEquals(EnclosedExpr first, EnclosedExpr second) {

    if(!isEqualsUseMerger(first.getInner(),second.getInner())) return false;

    return true;
  }
}
