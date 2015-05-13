package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class ObjectCreationExprMerger extends AbstractMerger<ObjectCreationExpr> {

  @Override public ObjectCreationExpr merge(ObjectCreationExpr first, ObjectCreationExpr second) {
    ObjectCreationExpr oce = new ObjectCreationExpr();

    oce.setComment(mergeSingle(first.getComment(),second.getComment()));
    oce.setScope(mergeSingle(first.getScope(),second.getScope()));
    oce.setType(mergeSingle(first.getType(),second.getType()));
    oce.setTypeArgs(mergeCollectionsInOrder(first.getTypeArgs(),second.getTypeArgs()));
    oce.setArgs(mergeCollectionsInOrder(first.getArgs(),second.getArgs()));
    oce.setAnonymousClassBody(mergeCollections(first.getAnonymousClassBody(),second.getAnonymousClassBody()));

    return oce;
  }

  @Override public boolean isEquals(ObjectCreationExpr first, ObjectCreationExpr second) {
    if(first == second) return true;

    if(!isEqualsUseMerger(first.getScope(),second.getScope())) return false;
    if(!isEqualsUseMerger(first.getType(),second.getType())) return false;
    if(!isEqualsUseMerger(first.getTypeArgs(),second.getTypeArgs())) return false;
    if(!isEqualsUseMerger(first.getArgs(),second.getArgs())) return false;

    return true;
  }
}
