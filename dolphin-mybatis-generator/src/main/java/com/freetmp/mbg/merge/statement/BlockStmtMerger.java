package com.freetmp.mbg.merge.statement;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.stmt.BlockStmt;

/**
 * Created by pin on 2015/4/20.
 */
public class BlockStmtMerger extends AbstractMerger<BlockStmt> {

    @Override
    public BlockStmt merge(BlockStmt first, BlockStmt second) {
        return first.getStmts().size() >= second.getStmts().size() ? first : second;
    }

    /**
     * Unless we dig into the method inner code or there is nothing we can dos
     */
    @Override
    public boolean isEquals(BlockStmt first, BlockStmt second) {

        return true;
    }
}