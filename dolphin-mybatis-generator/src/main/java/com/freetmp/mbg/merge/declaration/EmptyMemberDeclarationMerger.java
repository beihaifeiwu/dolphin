package com.freetmp.mbg.merge.declaration;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.body.EmptyMemberDeclaration;

/**
 * Created by LiuPin on 2015/4/20.
 */
public class EmptyMemberDeclarationMerger extends AbstractMerger<EmptyMemberDeclaration> {

    private EmptyMemberDeclarationMerger(){}

    static {
        if(getMerger(EmptyMemberDeclaration.class) == null){
            register(EmptyMemberDeclaration.class,new EmptyMemberDeclarationMerger());
        }
    }

    @Override public EmptyMemberDeclaration merge(EmptyMemberDeclaration first, EmptyMemberDeclaration second) {
        return first;
    }

    @Override public boolean isEquals(EmptyMemberDeclaration first, EmptyMemberDeclaration second) {
        return true;
    }
}
