package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.ArrayAccessExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class ArrayAccessExprMerger extends AbstractMerger<ArrayAccessExpr> {

  @Override public ArrayAccessExpr merge(ArrayAccessExpr first, ArrayAccessExpr second) {
    ArrayAccessExpr aae = new ArrayAccessExpr();
    aae.setComment(mergeSingle(first.getComment(),second.getComment()));
    aae.setIndex(mergeSingle(first.getIndex(),second.getIndex()));
    aae.setName(mergeSingle(first.getName(),second.getName()));
    return aae;
  }

  @Override public boolean isEquals(ArrayAccessExpr first, ArrayAccessExpr second) {
    if(first == second) return true;

    if(!first.getName().equals(second.getName())) return false;
    if(!first.getIndex().equals(second.getIndex())) return false;

    return true;
  }
}
