package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.BinaryExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class BinaryExprMerger extends AbstractMerger<BinaryExpr> {

  @Override public BinaryExpr doMerge(BinaryExpr first, BinaryExpr second) {
    BinaryExpr be = new BinaryExpr();

    be.setOperator(first.getOperator());
    be.setLeft(mergeSingle(first.getLeft(),second.getLeft()));
    be.setRight(mergeSingle(first.getRight(),second.getRight()));

    return be;
  }

  @Override public boolean doIsEquals(BinaryExpr first, BinaryExpr second) {

    if(!isEqualsUseMerger(first.getLeft(),second.getLeft())) return false;
    if(!isEqualsUseMerger(first.getRight(),second.getRight())) return false;
    if(!first.getOperator().equals(second.getOperator())) return false;

    return true;
  }
}
