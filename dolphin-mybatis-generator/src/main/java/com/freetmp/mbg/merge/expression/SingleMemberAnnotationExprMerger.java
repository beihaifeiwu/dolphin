package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;

/**
 * Created by pin on 2015/4/19.
 */
public class SingleMemberAnnotationExprMerger extends AbstractMerger<SingleMemberAnnotationExpr> {

  @Override
  public SingleMemberAnnotationExpr doMerge(SingleMemberAnnotationExpr first, SingleMemberAnnotationExpr second) {
    SingleMemberAnnotationExpr smae = new SingleMemberAnnotationExpr();
    smae.setName(mergeSingle(first.getName(),second.getName()));
    smae.setMemberValue(mergeSingle(first.getMemberValue(),second.getMemberValue()));
    return smae;
  }

  @Override
  public boolean doIsEquals(SingleMemberAnnotationExpr first, SingleMemberAnnotationExpr second) {

    return first.getName().equals(second.getName()) && first.getMemberValue().equals(second.getMemberValue());
  }
}
