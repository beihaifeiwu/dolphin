package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class ArrayInitializerExprMerger extends AbstractMerger<ArrayInitializerExpr> {

  @Override public ArrayInitializerExpr doMerge(ArrayInitializerExpr first, ArrayInitializerExpr second) {
    ArrayInitializerExpr aie = new ArrayInitializerExpr();
    aie.setValues(mergeCollections(first.getValues(),second.getValues()));
    return aie;
  }

  @Override public boolean doIsEquals(ArrayInitializerExpr first, ArrayInitializerExpr second) {
    if(!isEqualsUseMerger(first.getValues(),second.getValues())) return false;

    return true;
  }
}
