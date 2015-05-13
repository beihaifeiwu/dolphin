package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.TryStmt;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class TryStmtMerger extends AbstractMerger<TryStmt> {

  @Override public TryStmt merge(TryStmt first, TryStmt second) {
    TryStmt ts = new TryStmt();
    ts.setComment(mergeSingle(first.getComment(),second.getComment()));
    ts.setResources(mergeCollectionsInOrder(first.getResources(),second.getResources()));
    ts.setCatchs(mergeCollectionsInOrder(first.getCatchs(),second.getCatchs()));
    ts.setTryBlock(mergeSingle(first.getTryBlock(),second.getTryBlock()));
    ts.setFinallyBlock(mergeSingle(first.getFinallyBlock(),second.getFinallyBlock()));
    return ts;
  }

  @Override public boolean isEquals(TryStmt first, TryStmt second) {
    if(first == second) return true;
    if(!isEqualsUseMerger(first.getResources(),second.getResources())) return false;
    if(!isEqualsUseMerger(first.getCatchs(),second.getCatchs())) return false;

    return true;
  }
}
