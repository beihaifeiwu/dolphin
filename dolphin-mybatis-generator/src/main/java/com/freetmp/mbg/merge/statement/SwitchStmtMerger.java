package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.SwitchStmt;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class SwitchStmtMerger extends AbstractMerger<SwitchStmt> {

  @Override public SwitchStmt doMerge(SwitchStmt first, SwitchStmt second) {
    SwitchStmt ss = new SwitchStmt();
    ss.setEntries(mergeCollections(first.getEntries(),second.getEntries()));
    ss.setSelector(mergeSingle(first.getSelector(),second.getSelector()));
    return ss;
  }

  @Override public boolean doIsEquals(SwitchStmt first, SwitchStmt second) {

    if(!isEqualsUseMerger(first.getSelector(),second.getSelector())) return false;
    if(!isEqualsUseMerger(first.getEntries(),second.getEntries())) return false;

    return true;
  }
}
