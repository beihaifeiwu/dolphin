package com.freetmp.mbg.merge.parameter;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.body.VariableDeclaratorId;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by pin on 2015/4/19.
 */
public class VariableDeclaratorIdMerger extends AbstractMerger<VariableDeclaratorId> {

    private VariableDeclaratorIdMerger(){}

    static {
        if(getMerger(VariableDeclaratorId.class) == null){
            register(VariableDeclaratorId.class,new VariableDeclaratorIdMerger());
        }
    }

    @Override
    public VariableDeclaratorId merge(VariableDeclaratorId first, VariableDeclaratorId second) {
        return first;
    }

    @Override
    public boolean isEquals(VariableDeclaratorId first, VariableDeclaratorId second) {

        if(first == second) return true;

        if(!isAllNotNull(first,second)) return false;

        if(!StringUtils.equals(first.getName(),second.getName())) return false;

        if(first.getArrayCount() != second.getArrayCount()) return false;

        return true;
    }
}
