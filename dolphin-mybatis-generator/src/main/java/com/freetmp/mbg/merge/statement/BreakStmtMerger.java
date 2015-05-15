package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.BreakStmt;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class BreakStmtMerger extends AbstractMerger<BreakStmt> {

  @Override public BreakStmt doMerge(BreakStmt first, BreakStmt second) {
    BreakStmt bs = new BreakStmt();
    bs.setId(first.getId());
    return bs;
  }

  @Override public boolean doIsEquals(BreakStmt first, BreakStmt second) {
    if(!first.getId().equals(second.getId())) return false;
    return true;
  }
}
