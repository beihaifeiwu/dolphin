package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.ClassExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class ClassExprMerger extends AbstractMerger<ClassExpr> {

  @Override public ClassExpr merge(ClassExpr first, ClassExpr second) {
    ClassExpr ce = new ClassExpr();

    ce.setComment(mergeSingle(first.getComment(),second.getComment()));
    ce.setType(mergeSingle(first.getType(),second.getType()));

    return ce;
  }

  @Override public boolean isEquals(ClassExpr first, ClassExpr second) {
    if(first == second) return true;

    if(!isEqualsUseMerger(first.getType(),second.getType())) return false;

    return true;
  }
}
