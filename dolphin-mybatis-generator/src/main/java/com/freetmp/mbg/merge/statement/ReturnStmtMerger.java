package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.ReturnStmt;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class ReturnStmtMerger extends AbstractMerger<ReturnStmt> {

  @Override public ReturnStmt doMerge(ReturnStmt first, ReturnStmt second) {
    ReturnStmt rs = new ReturnStmt();

    rs.setExpr(mergeSingle(first.getExpr(),second.getExpr()));

    return rs;
  }

  @Override public boolean doIsEquals(ReturnStmt first, ReturnStmt second) {

    if(!isEqualsUseMerger(first.getExpr(),second.getExpr())) return false;

    return true;
  }
}
