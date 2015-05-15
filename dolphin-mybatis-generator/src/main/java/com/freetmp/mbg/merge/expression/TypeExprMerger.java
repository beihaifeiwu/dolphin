package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.TypeExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class TypeExprMerger extends AbstractMerger<TypeExpr> {

  @Override public TypeExpr doMerge(TypeExpr first, TypeExpr second) {
    TypeExpr te = new TypeExpr();

    te.setType(mergeSingle(first.getType(),second.getType()));

    return te;
  }

  @Override public boolean doIsEquals(TypeExpr first, TypeExpr second) {

    if(!isEqualsUseMerger(first.getType(),second.getType())) return false;

    return true;
  }
}
