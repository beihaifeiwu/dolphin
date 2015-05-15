package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.WhileStmt;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class WhileStmtMerger extends AbstractMerger<WhileStmt> {
  @Override public WhileStmt doMerge(WhileStmt first, WhileStmt second) {
    WhileStmt ws = new WhileStmt();
    ws.setCondition(mergeSingle(first.getCondition(),second.getCondition()));
    ws.setBody(mergeSingle(first.getBody(),second.getBody()));
    return ws;
  }

  @Override public boolean doIsEquals(WhileStmt first, WhileStmt second) {

    if(!isEqualsUseMerger(first.getCondition(),second.getCondition())) return false;
    if(!isEqualsUseMerger(first.getBody(),second.getBody())) return false;
    return true;
  }
}
