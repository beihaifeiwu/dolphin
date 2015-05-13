package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.StringLiteralExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class StringLiteralExprMerger extends AbstractMerger<StringLiteralExpr> {

  @Override public StringLiteralExpr merge(StringLiteralExpr first, StringLiteralExpr second) {
    StringLiteralExpr sle = new StringLiteralExpr();

    sle.setValue(first.getValue());
    sle.setComment(mergeSingle(first.getComment(),second.getComment()));

    return sle;
  }

  @Override public boolean isEquals(StringLiteralExpr first, StringLiteralExpr second) {
    if(first == second) return true;

    if(!first.getValue().equals(second.getValue())) return false;

    return true;
  }
}
