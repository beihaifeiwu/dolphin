package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.LambdaExpr;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class LambdaExprMerger extends AbstractMerger<LambdaExpr> {

  @Override public LambdaExpr doMerge(LambdaExpr first, LambdaExpr second) {
    LambdaExpr le = new LambdaExpr();

    le.setBody(mergeSingle(first.getBody(),second.getBody()));
    le.setParameters(mergeCollectionsInOrder(first.getParameters(),second.getParameters()));
    le.setParametersEnclosed(first.isParametersEnclosed());

    return le;
  }

  @Override public boolean doIsEquals(LambdaExpr first, LambdaExpr second) {

    if(first.isParametersEnclosed() != second.isParametersEnclosed()) return false;
    if(!isEqualsUseMerger(first.getParameters(),second.getParameters())) return false;
    if(!isEqualsUseMerger(first.getBody(),second.getBody())) return false;

    return true;
  }

}
