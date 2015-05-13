package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.NameExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class NameExprMerger extends AbstractMerger<NameExpr> {

  @Override public NameExpr merge(NameExpr first, NameExpr second) {
    NameExpr ne = new NameExpr();

    ne.setComment(mergeSingle(first.getComment(),second.getComment()));
    ne.setName(first.getName());

    return ne;
  }

  @Override public boolean isEquals(NameExpr first, NameExpr second) {
    if(first == second) return true;

    if(!first.getName().equals(second.getName())) return false;

    return true;
  }
}
