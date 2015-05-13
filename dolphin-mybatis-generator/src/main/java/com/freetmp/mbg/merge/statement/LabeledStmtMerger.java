package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.LabeledStmt;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class LabeledStmtMerger extends AbstractMerger<LabeledStmt> {

  @Override public LabeledStmt merge(LabeledStmt first, LabeledStmt second) {
    LabeledStmt ls = new LabeledStmt();
    ls.setComment(mergeSingle(first.getComment(),second.getComment()));
    ls.setLabel(first.getLabel());
    ls.setStmt(mergeSingle(first.getStmt(),second.getStmt()));

    return ls;
  }

  @Override public boolean isEquals(LabeledStmt first, LabeledStmt second) {
    if(first == second) return true;

    if(!first.getLabel().equals(second.getLabel())) return false;
    if(!isEqualsUseMerger(first.getStmt(),second.getStmt())) return false;

    return true;
  }
}
