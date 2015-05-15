package com.freetmp.mbg.merge.declaration;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.body.InitializerDeclaration;

/**
 * Created by LiuPin on 2015/4/20.
 */
public class InitializerDeclarationMerger extends AbstractMerger<InitializerDeclaration> {

    @Override public InitializerDeclaration doMerge(InitializerDeclaration first, InitializerDeclaration second) {

        InitializerDeclaration id = new InitializerDeclaration();

        id.setJavaDoc(mergeSingle(first.getJavaDoc(),second.getJavaDoc()));
        id.setStatic(first.isStatic());
        id.setAnnotations(mergeCollections(first.getAnnotations(),second.getAnnotations()));
        id.setBlock(mergeSingle(first.getBlock(),second.getBlock()));

        return id;
    }

    @Override public boolean doIsEquals(InitializerDeclaration first, InitializerDeclaration second) {

        if(first.isStatic() != second.isStatic()) return false;

        return false;
    }
}
