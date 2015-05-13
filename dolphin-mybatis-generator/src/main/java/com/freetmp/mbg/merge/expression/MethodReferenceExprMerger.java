package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.MethodReferenceExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class MethodReferenceExprMerger extends AbstractMerger<MethodReferenceExpr> {

  @Override public MethodReferenceExpr merge(MethodReferenceExpr first, MethodReferenceExpr second) {
    MethodReferenceExpr mre = new MethodReferenceExpr();

    mre.setComment(mergeSingle(first.getComment(),second.getComment()));
    mre.setScope(mergeSingle(first.getScope(),second.getScope()));
    mre.setIdentifier(first.getIdentifier());
    mre.setTypeParameters(mergeCollectionsInOrder(first.getTypeParameters(),second.getTypeParameters()));

    return mre;
  }

  @Override public boolean isEquals(MethodReferenceExpr first, MethodReferenceExpr second) {
    if(first == second) return true;

    if(!first.getIdentifier().equals(second.getIdentifier())) return false;
    if(!isEqualsUseMerger(first.getScope(), second.getScope())) return false;
    if(!isEqualsUseMerger(first.getTypeParameters(),second.getTypeParameters())) return false;

    return true;
  }
}
