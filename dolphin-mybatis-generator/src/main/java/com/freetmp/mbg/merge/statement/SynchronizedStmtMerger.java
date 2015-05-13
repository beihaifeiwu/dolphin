package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.SynchronizedStmt;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class SynchronizedStmtMerger extends AbstractMerger<SynchronizedStmt> {

  @Override public SynchronizedStmt merge(SynchronizedStmt first, SynchronizedStmt second) {
    SynchronizedStmt ss = new SynchronizedStmt();
    ss.setComment(mergeSingle(first.getComment(),second.getComment()));
    ss.setExpr(mergeSingle(first.getExpr(),second.getExpr()));
    ss.setBlock(mergeSingle(first.getBlock(),second.getBlock()));

    return ss;
  }

  @Override public boolean isEquals(SynchronizedStmt first, SynchronizedStmt second) {
    if(first == second) return true;

    if(!isEqualsUseMerger(first.getExpr(),second.getExpr())) return false;
    if(!isEqualsUseMerger(first.getBlock(),second.getBlock())) return false;

    return true;
  }
}
