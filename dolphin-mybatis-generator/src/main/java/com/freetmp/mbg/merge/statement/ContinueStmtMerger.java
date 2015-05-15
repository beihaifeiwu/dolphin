package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.ContinueStmt;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class ContinueStmtMerger extends AbstractMerger<ContinueStmt> {

  @Override public ContinueStmt doMerge(ContinueStmt first, ContinueStmt second) {
    ContinueStmt cs = new ContinueStmt();
    cs.setId(first.getId());
    return cs;
  }

  @Override public boolean doIsEquals(ContinueStmt first, ContinueStmt second) {
    if(!first.getId().equals(second.getId())) return false;
    return true;
  }
}
