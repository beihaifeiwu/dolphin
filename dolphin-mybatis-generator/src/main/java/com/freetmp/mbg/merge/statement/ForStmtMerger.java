package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.ForStmt;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class ForStmtMerger extends AbstractMerger<ForStmt> {

  @Override public ForStmt doMerge(ForStmt first, ForStmt second) {
    ForStmt fs = new ForStmt();
    fs.setBody(mergeSingle(first.getBody(), second.getBody()));
    fs.setUpdate(mergeCollectionsInOrder(first.getUpdate(), second.getUpdate()));
    fs.setCompare(mergeSingle(first.getCompare(), second.getCompare()));
    fs.setInit(mergeCollectionsInOrder(first.getInit(), second.getInit()));

    return fs;
  }

  @Override public boolean doIsEquals(ForStmt first, ForStmt second) {

    if(!isEqualsUseMerger(first.getInit(),second.getInit())) return false;
    if(!isEqualsUseMerger(first.getCompare(),second.getCompare())) return false;
    if(!isEqualsUseMerger(first.getUpdate(),second.getUpdate())) return false;
    if(!isEqualsUseMerger(first.getBody(),second.getBody())) return false;

    return true;
  }
}
