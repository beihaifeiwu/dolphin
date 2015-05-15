package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.ForeachStmt;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class ForeachStmtMerger extends AbstractMerger<ForeachStmt> {

  @Override public ForeachStmt doMerge(ForeachStmt first, ForeachStmt second) {
    ForeachStmt fs = new ForeachStmt();
    fs.setBody(mergeSingle(first.getBody(),second.getBody()));
    fs.setIterable(mergeSingle(first.getIterable(),second.getIterable()));
    fs.setVariable(mergeSingle(first.getVariable(),second.getVariable()));
    return fs;
  }

  @Override public boolean doIsEquals(ForeachStmt first, ForeachStmt second) {

    if (!isEqualsUseMerger(first.getVariable(), second.getVariable())) return false;
    if (!isEqualsUseMerger(first.getIterable(), second.getIterable())) return false;
    if (!isEqualsUseMerger(first.getBody(), second.getBody())) return false;

    return true;
  }
}
