package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.MethodCallExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class MethodCallExprMerger extends AbstractMerger<MethodCallExpr> {

  @Override public MethodCallExpr doMerge(MethodCallExpr first, MethodCallExpr second) {
    MethodCallExpr mce = new MethodCallExpr();

    mce.setNameExpr(mergeSingle(first.getNameExpr(),second.getNameExpr()));
    mce.setArgs(mergeCollectionsInOrder(first.getArgs(),second.getArgs()));
    mce.setScope(mergeSingle(first.getScope(),second.getScope()));
    mce.setTypeArgs(mergeCollectionsInOrder(first.getTypeArgs(),second.getTypeArgs()));

    return mce;
  }

  @Override public boolean doIsEquals(MethodCallExpr first, MethodCallExpr second) {

    if(!isEqualsUseMerger(first.getNameExpr(),second.getNameExpr())) return false;
    if(!isEqualsUseMerger(first.getTypeArgs(),second.getTypeArgs())) return false;
    if(!isEqualsUseMerger(first.getScope(),second.getScope())) return false;
    if(!isEqualsUseMerger(first.getArgs(),second.getArgs())) return false;

    return true;
  }
}
