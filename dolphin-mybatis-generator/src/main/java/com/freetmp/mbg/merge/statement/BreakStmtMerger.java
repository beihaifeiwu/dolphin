package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.BreakStmt;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class BreakStmtMerger extends AbstractMerger<BreakStmt> {

  @Override public BreakStmt merge(BreakStmt first, BreakStmt second) {
    BreakStmt bs = new BreakStmt();
    bs.setComment(mergeSingle(first.getComment(),second.getComment()));
    bs.setId(first.getId());
    return bs;
  }

  @Override public boolean isEquals(BreakStmt first, BreakStmt second) {
    if(first == second) return true;
    if(!first.getId().equals(second.getId())) return false;
    return true;
  }
}
