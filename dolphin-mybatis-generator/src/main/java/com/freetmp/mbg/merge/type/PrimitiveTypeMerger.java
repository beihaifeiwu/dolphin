package com.freetmp.mbg.merge.type;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.type.PrimitiveType;


/**
 * Created by pin on 2015/4/19.
 */
public class PrimitiveTypeMerger extends AbstractMerger<PrimitiveType> {

    private PrimitiveTypeMerger(){}

    static {
        if(getMerger(PrimitiveType.class) == null){
            register(PrimitiveType.class,new PrimitiveTypeMerger());
        }
    }

    @Override
    public PrimitiveType merge(PrimitiveType first, PrimitiveType second) {
        return first;
    }

    @Override
    public boolean isEquals(PrimitiveType first, PrimitiveType second) {

        if(first == second) return true;
        if(first == null || second == null) return false;

        if(!first.getType().equals(second.getType())) return false;

        return true;
    }
}
