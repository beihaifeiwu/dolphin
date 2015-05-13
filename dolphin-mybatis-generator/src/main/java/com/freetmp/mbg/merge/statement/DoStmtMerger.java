package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.DoStmt;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class DoStmtMerger extends AbstractMerger<DoStmt> {
  @Override public DoStmt merge(DoStmt first, DoStmt second) {
    DoStmt ds = new DoStmt();
    ds.setComment(mergeSingle(first.getComment(),second.getComment()));
    ds.setBody(mergeSingle(first.getBody(),second.getBody()));
    ds.setCondition(mergeSingle(first.getCondition(),second.getCondition()));
    return ds;
  }

  @Override public boolean isEquals(DoStmt first, DoStmt second) {
    if(first == second) return true;

    if(!isEqualsUseMerger(first.getCondition(),second.getCondition())) return false;
    if(!isEqualsUseMerger(first.getBody(),second.getBody())) return false;

    return true;
  }
}
