package com.freetmp.mbg.merge;

import com.github.javaparser.ast.TypeParameter;

/**
 * Created by pin on 2015/4/12.
 */
public class TypeParameterMerger extends AbstractMerger<TypeParameter> {

    static {
        if(getMerger(TypeParameter.class) == null){
            register(TypeParameter.class,new TypeParameterMerger());
        }
    }

    private TypeParameterMerger(){}

    @Override
    public TypeParameter merge(TypeParameter first, TypeParameter second) {

        // TODO
        return null;
    }

    @Override
    public boolean isEquals(TypeParameter first, TypeParameter second) {

        // TODO
        return false;
    }
}
