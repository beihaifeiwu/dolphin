package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.ArrayCreationExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class ArrayCreationExprMerger extends AbstractMerger<ArrayCreationExpr> {

  @Override public ArrayCreationExpr doMerge(ArrayCreationExpr first, ArrayCreationExpr second) {
    ArrayCreationExpr ace = new ArrayCreationExpr();
    ace.setType(mergeSingle(first.getType(),second.getType()));

    return ace;
  }

  @Override public boolean doIsEquals(ArrayCreationExpr first, ArrayCreationExpr second) {
    if(!isEqualsUseMerger(first.getType(), second.getType())) return false;
    if(first.getArrayCount() != second.getArrayCount()) return false;
    if(!isEqualsUseMerger(first.getInitializer(),second.getInitializer())) return false;
    if(!isEqualsUseMerger(first.getDimensions(),second.getDimensions())) return false;

    return true;
  }
}
