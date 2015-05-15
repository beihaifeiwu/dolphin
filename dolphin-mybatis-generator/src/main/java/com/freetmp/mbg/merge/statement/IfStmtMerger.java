package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.IfStmt;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class IfStmtMerger extends AbstractMerger<IfStmt> {

  @Override public IfStmt doMerge(IfStmt first, IfStmt second) {
    IfStmt is = new IfStmt();

    is.setCondition(mergeSingle(first.getCondition(),second.getCondition()));
    is.setElseStmt(mergeSingle(first.getElseStmt(),second.getElseStmt()));
    is.setThenStmt(mergeSingle(first.getThenStmt(),second.getThenStmt()));

    return is;
  }

  @Override public boolean doIsEquals(IfStmt first, IfStmt second) {

    if(!isEqualsUseMerger(first.getCondition(),second.getCondition())) return false;
    if(!isEqualsUseMerger(first.getElseStmt(),second.getElseStmt())) return false;
    if(!isEqualsUseMerger(first.getThenStmt(),second.getThenStmt())) return false;

    return true;
  }
}
