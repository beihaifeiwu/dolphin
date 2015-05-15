package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.ThrowStmt;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class ThrowStmtMerger extends AbstractMerger<ThrowStmt> {

  @Override public ThrowStmt doMerge(ThrowStmt first, ThrowStmt second) {
    ThrowStmt ts = new ThrowStmt();
    ts.setExpr(mergeSingle(first.getExpr(),second.getExpr()));
    return ts;
  }

  @Override public boolean doIsEquals(ThrowStmt first, ThrowStmt second) {
    if(!isEqualsUseMerger(first.getExpr(),second.getExpr())) return false;
    return true;
  }
}
