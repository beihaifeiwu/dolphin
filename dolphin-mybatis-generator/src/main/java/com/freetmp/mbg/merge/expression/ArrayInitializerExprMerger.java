package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class ArrayInitializerExprMerger extends AbstractMerger<ArrayInitializerExpr> {

  @Override public ArrayInitializerExpr merge(ArrayInitializerExpr first, ArrayInitializerExpr second) {
    ArrayInitializerExpr aie = new ArrayInitializerExpr();
    aie.setComment(mergeSingle(first.getComment(),second.getComment()));
    aie.setValues(mergeCollections(first.getValues(),second.getValues()));
    return aie;
  }

  @Override public boolean isEquals(ArrayInitializerExpr first, ArrayInitializerExpr second) {
    if(first == second) return true;

    if(!isEqualsUseMerger(first.getValues(),second.getValues())) return false;

    return true;
  }
}
