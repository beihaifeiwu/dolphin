package com.freetmp.mbg.merge.declaration;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.body.EmptyTypeDeclaration;

/**
 * Created by pin on 2015/4/19.
 */
public class EmptyTypeDeclarationMerger extends AbstractMerger<EmptyTypeDeclaration> {

    private EmptyTypeDeclarationMerger(){}

    static {
        if(getMerger(EmptyTypeDeclaration.class) == null){
            register(EmptyTypeDeclaration.class,new EmptyTypeDeclarationMerger());
        }
    }

    @Override
    public EmptyTypeDeclaration merge(EmptyTypeDeclaration first, EmptyTypeDeclaration second) {
        return first;
    }

    @Override
    public boolean isEquals(EmptyTypeDeclaration first, EmptyTypeDeclaration second) {
        return true;
    }
}
