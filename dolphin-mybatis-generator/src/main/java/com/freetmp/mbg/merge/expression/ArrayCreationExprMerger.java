package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.ArrayCreationExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class ArrayCreationExprMerger extends AbstractMerger<ArrayCreationExpr> {

  @Override public ArrayCreationExpr merge(ArrayCreationExpr first, ArrayCreationExpr second) {
    ArrayCreationExpr ace = new ArrayCreationExpr();
    ace.setComment(mergeSingle(first.getComment(),second.getComment()));
    ace.setType(mergeSingle(first.getType(),second.getType()));

    return ace;
  }

  @Override public boolean isEquals(ArrayCreationExpr first, ArrayCreationExpr second) {
    if(first == second) return true;

    if(!isEqualsUseMerger(first.getType(), second.getType())) return false;
    if(first.getArrayCount() != second.getArrayCount()) return false;
    if(!isEqualsUseMerger(first.getInitializer(),second.getInitializer())) return false;
    if(!isEqualsUseMerger(first.getDimensions(),second.getDimensions())) return false;

    return true;
  }
}
