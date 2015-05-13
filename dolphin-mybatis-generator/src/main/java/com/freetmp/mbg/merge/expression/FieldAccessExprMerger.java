package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.FieldAccessExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class FieldAccessExprMerger extends AbstractMerger<FieldAccessExpr> {

  @Override public FieldAccessExpr merge(FieldAccessExpr first, FieldAccessExpr second) {
    FieldAccessExpr fae = new FieldAccessExpr();

    fae.setComment(mergeSingle(first.getComment(),second.getComment()));
    fae.setFieldExpr(mergeSingle(first.getFieldExpr(),second.getFieldExpr()));
    fae.setScope(mergeSingle(first.getScope(),second.getScope()));
    fae.setTypeArgs(mergeCollections(first.getTypeArgs(),second.getTypeArgs()));

    return fae;
  }

  @Override public boolean isEquals(FieldAccessExpr first, FieldAccessExpr second) {
    if(first == second) return true;

    if(!isEqualsUseMerger(first.getScope(), second.getScope())) return false;
    if(!isEqualsUseMerger(first.getTypeArgs(),second.getTypeArgs())) return false;
    if(!isEqualsUseMerger(first.getFieldExpr(),second.getFieldExpr())) return false;

    return true;
  }
}
