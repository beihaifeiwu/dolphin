package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;

/**
 * Created by pin on 2015/4/19.
 */
public class MarkerAnnotationExprMerger extends AbstractMerger<MarkerAnnotationExpr> {

  @Override
  public MarkerAnnotationExpr merge(MarkerAnnotationExpr first, MarkerAnnotationExpr second) {
    MarkerAnnotationExpr mae = new MarkerAnnotationExpr();

    mae.setComment(mergeSingle(first.getComment(),second.getComment()));
    mae.setName(mergeSingle(first.getName(),second.getName()));

    return mae;
  }

  @Override
  public boolean isEquals(MarkerAnnotationExpr first, MarkerAnnotationExpr second) {

    if (first == second) return true;
    if (first == null || second == null) return false;

    return first.getName().equals(second.getName());
  }
}
