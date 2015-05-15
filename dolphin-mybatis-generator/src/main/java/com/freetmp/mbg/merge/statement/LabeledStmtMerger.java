package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.LabeledStmt;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class LabeledStmtMerger extends AbstractMerger<LabeledStmt> {

  @Override public LabeledStmt doMerge(LabeledStmt first, LabeledStmt second) {
    LabeledStmt ls = new LabeledStmt();
    ls.setLabel(first.getLabel());
    ls.setStmt(mergeSingle(first.getStmt(),second.getStmt()));

    return ls;
  }

  @Override public boolean doIsEquals(LabeledStmt first, LabeledStmt second) {

    if(!first.getLabel().equals(second.getLabel())) return false;
    if(!isEqualsUseMerger(first.getStmt(),second.getStmt())) return false;

    return true;
  }
}
