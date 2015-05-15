package com.freetmp.mbg.merge.variable;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.body.VariableDeclaratorId;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by pin on 2015/4/19.
 */
public class VariableDeclaratorIdMerger extends AbstractMerger<VariableDeclaratorId> {

    @Override
    public VariableDeclaratorId doMerge(VariableDeclaratorId first, VariableDeclaratorId second) {
        return first;
    }

    @Override
    public boolean doIsEquals(VariableDeclaratorId first, VariableDeclaratorId second) {

        if(!StringUtils.equals(first.getName(),second.getName())) return false;

        if(first.getArrayCount() != second.getArrayCount()) return false;

        return true;
    }
}
