package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.BlockStmt;

/**
 * Created by pin on 2015/4/20.
 */
public class BlockStmtMerger extends AbstractMerger<BlockStmt> {

  @Override
  public BlockStmt doMerge(BlockStmt first, BlockStmt second) {
    BlockStmt bs = new BlockStmt();
    bs.setStmts(mergeCollectionsInOrder(first.getStmts(),second.getStmts()));
    return bs;
  }

  /**
   * Unless we dig into the method inner code or there is nothing we can dos
   */
  @Override
  public boolean doIsEquals(BlockStmt first, BlockStmt second) {

    return true;
  }
}