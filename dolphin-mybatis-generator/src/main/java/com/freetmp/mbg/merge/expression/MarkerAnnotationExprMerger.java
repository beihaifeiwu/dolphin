package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;

/**
 * Created by pin on 2015/4/19.
 */
public class MarkerAnnotationExprMerger extends AbstractMerger<MarkerAnnotationExpr> {

  @Override
  public MarkerAnnotationExpr doMerge(MarkerAnnotationExpr first, MarkerAnnotationExpr second) {
    MarkerAnnotationExpr mae = new MarkerAnnotationExpr();

    mae.setName(mergeSingle(first.getName(),second.getName()));

    return mae;
  }

  @Override
  public boolean doIsEquals(MarkerAnnotationExpr first, MarkerAnnotationExpr second) {

    return first.getName().equals(second.getName());
  }
}
