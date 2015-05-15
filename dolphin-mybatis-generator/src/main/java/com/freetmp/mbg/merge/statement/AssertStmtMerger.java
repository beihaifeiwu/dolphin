package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.AssertStmt;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class AssertStmtMerger extends AbstractMerger<AssertStmt> {

  @Override public AssertStmt doMerge(AssertStmt first, AssertStmt second) {
    AssertStmt as = new AssertStmt();

    as.setCheck(mergeSingle(first.getCheck(),second.getCheck()));
    as.setMessage(mergeSingle(first.getMessage(),second.getMessage()));

    return as;
  }

  @Override public boolean doIsEquals(AssertStmt first, AssertStmt second) {

    if(!isEqualsUseMerger(first.getCheck(),second.getCheck())) return false;
    if(!isEqualsUseMerger(first.getMessage(),second.getMessage())) return false;

    return true;
  }
}
