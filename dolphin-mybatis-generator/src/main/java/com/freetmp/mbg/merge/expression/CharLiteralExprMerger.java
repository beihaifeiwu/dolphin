package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.CharLiteralExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class CharLiteralExprMerger extends AbstractMerger<CharLiteralExpr> {

  @Override public CharLiteralExpr merge(CharLiteralExpr first, CharLiteralExpr second) {
    CharLiteralExpr cle = new CharLiteralExpr();
    cle.setComment(mergeSingle(first.getComment(),second.getComment()));
    cle.setValue(first.getValue());
    return cle;
  }

  @Override public boolean isEquals(CharLiteralExpr first, CharLiteralExpr second) {
    if(first == second) return true;

    if(!first.getValue().equals(second.getValue())) return false;

    return true;
  }
}
