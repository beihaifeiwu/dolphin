package com.freetmp.mbg.merge.declaration;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.body.EnumConstantDeclaration;

/**
 * Created by pin on 2015/4/19.
 */
public class EnumConstantDeclarationMerger extends AbstractMerger<EnumConstantDeclaration> {

    @Override
    public EnumConstantDeclaration doMerge(EnumConstantDeclaration first, EnumConstantDeclaration second) {

        EnumConstantDeclaration ecd = new EnumConstantDeclaration();

        ecd.setName(first.getName());
        ecd.setArgs(mergeListInOrder(first.getArgs(), second.getArgs()));
        ecd.setJavaDoc(mergeSingle(first.getJavaDoc(), second.getJavaDoc()));

        ecd.setAnnotations(mergeCollections(first.getAnnotations(), second.getAnnotations()));
        ecd.setClassBody(mergeCollections(first.getClassBody(), second.getClassBody()));

        return ecd;
    }

    @Override
    public boolean doIsEquals(EnumConstantDeclaration first, EnumConstantDeclaration second) {

        return first.getName().equals(second.getName());
    }

}
