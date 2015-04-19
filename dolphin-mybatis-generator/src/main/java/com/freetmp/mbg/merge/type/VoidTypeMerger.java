package com.freetmp.mbg.merge.type;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.type.VoidType;

/**
 * Created by pin on 2015/4/19.
 */
public class VoidTypeMerger extends AbstractMerger<VoidType> {

    private VoidTypeMerger(){}

    static {
        if(getMerger(VoidType.class) == null){
            register(VoidType.class,new VoidTypeMerger());
        }
    }

    @Override
    public VoidType merge(VoidType first, VoidType second) {
        return first;
    }

    @Override
    public boolean isEquals(VoidType first, VoidType second) {

        return true;
    }
}
