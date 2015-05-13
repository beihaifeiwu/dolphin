package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.CatchClause;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class CatchClauseMerger extends AbstractMerger<CatchClause> {

  @Override public CatchClause merge(CatchClause first, CatchClause second) {
    CatchClause cc = new CatchClause();
    cc.setComment(mergeSingle(first.getComment(),second.getComment()));
    cc.setCatchBlock(mergeSingle(first.getCatchBlock(),second.getCatchBlock()));
    cc.setExcept(mergeSingle(first.getExcept(),second.getExcept()));
    return cc;
  }

  @Override public boolean isEquals(CatchClause first, CatchClause second) {
    if(first == second) return true;

    if(!isEqualsUseMerger(first.getExcept(),second.getExcept())) return false;
    if(!isEqualsUseMerger(first.getCatchBlock(),second.getCatchBlock())) return false;

    return true;
  }
}
