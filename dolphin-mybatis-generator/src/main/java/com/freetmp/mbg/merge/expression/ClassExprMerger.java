package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.ClassExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class ClassExprMerger extends AbstractMerger<ClassExpr> {

  @Override public ClassExpr doMerge(ClassExpr first, ClassExpr second) {
    ClassExpr ce = new ClassExpr();

    ce.setType(mergeSingle(first.getType(),second.getType()));

    return ce;
  }

  @Override public boolean doIsEquals(ClassExpr first, ClassExpr second) {

    if(!isEqualsUseMerger(first.getType(),second.getType())) return false;

    return true;
  }
}
