package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.EmptyStmt;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class EmptyStmtMerger extends AbstractMerger<EmptyStmt> {

  @Override public EmptyStmt doMerge(EmptyStmt first, EmptyStmt second) {
    EmptyStmt es = new EmptyStmt();
    return es;
  }

  @Override public boolean doIsEquals(EmptyStmt first, EmptyStmt second) {
    return true;
  }
}
