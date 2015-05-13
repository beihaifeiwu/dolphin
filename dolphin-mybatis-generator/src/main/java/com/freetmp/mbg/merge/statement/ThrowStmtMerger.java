package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.ThrowStmt;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class ThrowStmtMerger extends AbstractMerger<ThrowStmt> {

  @Override public ThrowStmt merge(ThrowStmt first, ThrowStmt second) {
    ThrowStmt ts = new ThrowStmt();
    ts.setComment(mergeSingle(first.getComment(),second.getComment()));
    ts.setExpr(mergeSingle(first.getExpr(),second.getExpr()));
    return ts;
  }

  @Override public boolean isEquals(ThrowStmt first, ThrowStmt second) {
    if(first == second) return true;
    if(!isEqualsUseMerger(first.getExpr(),second.getExpr())) return false;
    return true;
  }
}
