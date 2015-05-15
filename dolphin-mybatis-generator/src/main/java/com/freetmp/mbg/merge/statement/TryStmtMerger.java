package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.TryStmt;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class TryStmtMerger extends AbstractMerger<TryStmt> {

  @Override public TryStmt doMerge(TryStmt first, TryStmt second) {
    TryStmt ts = new TryStmt();
    ts.setResources(mergeCollectionsInOrder(first.getResources(),second.getResources()));
    ts.setCatchs(mergeCollectionsInOrder(first.getCatchs(),second.getCatchs()));
    ts.setTryBlock(mergeSingle(first.getTryBlock(),second.getTryBlock()));
    ts.setFinallyBlock(mergeSingle(first.getFinallyBlock(),second.getFinallyBlock()));
    return ts;
  }

  @Override public boolean doIsEquals(TryStmt first, TryStmt second) {
    if(!isEqualsUseMerger(first.getResources(),second.getResources())) return false;
    if(!isEqualsUseMerger(first.getCatchs(),second.getCatchs())) return false;

    return true;
  }
}
