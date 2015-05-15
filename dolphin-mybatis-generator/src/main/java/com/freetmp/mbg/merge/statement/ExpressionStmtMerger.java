package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.ExpressionStmt;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class ExpressionStmtMerger extends AbstractMerger<ExpressionStmt> {

  @Override public ExpressionStmt doMerge(ExpressionStmt first, ExpressionStmt second) {
    ExpressionStmt es = new ExpressionStmt();

    es.setExpression(mergeSingle(first.getExpression(),second.getExpression()));

    return es;
  }

  @Override public boolean doIsEquals(ExpressionStmt first, ExpressionStmt second) {

    if(!isEqualsUseMerger(first.getExpression(),second.getExpression())) return false;

    return true;
  }
}
