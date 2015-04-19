package com.freetmp.mbg.merge.type;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.WildcardType;

/**
 * Created by pin on 2015/4/19.
 */
public class WildcardTypeMerger extends AbstractMerger<WildcardType> {

    private WildcardTypeMerger(){}

    static {
        if(getMerger(WildcardType.class) == null){
            register(WildcardType.class,new WildcardTypeMerger());
        }
    }

    @Override
    public WildcardType merge(WildcardType first, WildcardType second) {
        return first;
    }

    @Override
    public boolean isEquals(WildcardType first, WildcardType second) {

        if(first == second) return true;
        if(first == null || second == null) return false;

        AbstractMerger<ReferenceType> merger = getMerger(ReferenceType.class);

        if(!merger.isEquals(first.getExtends(),second.getExtends())) return false;

        if(!merger.isEquals(first.getSuper(),second.getSuper())) return false;

        return true;
    }
}
