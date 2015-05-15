package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class SwitchEntryStmtMerger extends AbstractMerger<SwitchEntryStmt> {

  @Override public SwitchEntryStmt doMerge(SwitchEntryStmt first, SwitchEntryStmt second) {
    SwitchEntryStmt ses = new SwitchEntryStmt();

    ses.setLabel(mergeSingle(first.getLabel(),second.getLabel()));
    ses.setStmts(mergeCollections(first.getStmts(),second.getStmts()));

    return ses;
  }

  @Override public boolean doIsEquals(SwitchEntryStmt first, SwitchEntryStmt second) {

    if(!isEqualsUseMerger(first.getLabel(),second.getLabel())) return false;

    return true;
  }
}
