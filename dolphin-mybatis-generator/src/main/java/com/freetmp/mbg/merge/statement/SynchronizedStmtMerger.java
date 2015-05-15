package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.SynchronizedStmt;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class SynchronizedStmtMerger extends AbstractMerger<SynchronizedStmt> {

  @Override public SynchronizedStmt doMerge(SynchronizedStmt first, SynchronizedStmt second) {
    SynchronizedStmt ss = new SynchronizedStmt();
    ss.setExpr(mergeSingle(first.getExpr(),second.getExpr()));
    ss.setBlock(mergeSingle(first.getBlock(),second.getBlock()));

    return ss;
  }

  @Override public boolean doIsEquals(SynchronizedStmt first, SynchronizedStmt second) {

    if(!isEqualsUseMerger(first.getExpr(),second.getExpr())) return false;
    if(!isEqualsUseMerger(first.getBlock(),second.getBlock())) return false;

    return true;
  }
}
