package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;

import java.util.List;

/**
 * Created by pin on 2015/4/19.
 */
public class NormalAnnotationExprMerger extends AbstractMerger<NormalAnnotationExpr> {

  @Override
  public NormalAnnotationExpr doMerge(NormalAnnotationExpr first, NormalAnnotationExpr second) {
    NormalAnnotationExpr nae = new NormalAnnotationExpr();

    nae.setPairs(mergeCollections(first.getPairs(),second.getPairs()));
    nae.setName(mergeSingle(first.getName(),second.getName()));

    return nae;
  }

  /**
   * 1. check the name
   * 2. check the member including key and value
   * if their size is not the same and the less one is all matched in the more one return true
   */
  @Override
  public boolean doIsEquals(NormalAnnotationExpr first, NormalAnnotationExpr second) {

    boolean equals = true;

    if (!first.getName().equals(second.getName())) equals = false;

    if (equals == true) {

      if (first.getPairs() == null) return second.getPairs() == null;

      if (!isSmallerHasEqualsInBigger(first.getPairs(), second.getPairs(), true)) return false;

    }

    return equals;
  }
}
