package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.AssertStmt;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class AssertStmtMerger extends AbstractMerger<AssertStmt> {

  @Override public AssertStmt merge(AssertStmt first, AssertStmt second) {
    AssertStmt as = new AssertStmt();

    as.setComment(mergeSingle(first.getComment(),second.getComment()));
    as.setCheck(mergeSingle(first.getCheck(),second.getCheck()));
    as.setMessage(mergeSingle(first.getMessage(),second.getMessage()));

    return as;
  }

  @Override public boolean isEquals(AssertStmt first, AssertStmt second) {
    if(first == second) return true;

    if(!isEqualsUseMerger(first.getCheck(),second.getCheck())) return false;
    if(!isEqualsUseMerger(first.getMessage(),second.getMessage())) return false;

    return true;
  }
}
