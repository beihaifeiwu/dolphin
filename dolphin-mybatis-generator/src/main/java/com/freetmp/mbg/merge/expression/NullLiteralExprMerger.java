package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.NullLiteralExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class NullLiteralExprMerger extends AbstractMerger<NullLiteralExpr> {

  @Override public NullLiteralExpr doMerge(NullLiteralExpr first, NullLiteralExpr second) {
    NullLiteralExpr nle = new NullLiteralExpr();

    return nle;
  }

  @Override public boolean doIsEquals(NullLiteralExpr first, NullLiteralExpr second) {
    return true;
  }
}
