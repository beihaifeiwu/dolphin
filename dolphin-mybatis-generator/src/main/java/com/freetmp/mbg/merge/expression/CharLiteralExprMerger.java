package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.CharLiteralExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class CharLiteralExprMerger extends AbstractMerger<CharLiteralExpr> {

  @Override public CharLiteralExpr doMerge(CharLiteralExpr first, CharLiteralExpr second) {
    CharLiteralExpr cle = new CharLiteralExpr();
    cle.setValue(first.getValue());
    return cle;
  }

  @Override public boolean doIsEquals(CharLiteralExpr first, CharLiteralExpr second) {
    if(!first.getValue().equals(second.getValue())) return false;

    return true;
  }
}
