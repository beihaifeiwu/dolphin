package com.freetmp.mbg.merge.declaration;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.body.FieldDeclaration;

/**
 * Created by LiuPin on 2015/4/20.
 */
public class FieldDeclarationMerger extends AbstractMerger<FieldDeclaration> {

    @Override public FieldDeclaration merge(FieldDeclaration first, FieldDeclaration second) {

        FieldDeclaration fd = new FieldDeclaration();
        fd.setComment(mergeSingle(first.getComment(),second.getComment()));
        fd.setJavaDoc(mergeSingle(first.getJavaDoc(),second.getJavaDoc()));
        fd.setType(mergeSingle(first.getType(),second.getType()));
        fd.setModifiers(mergeModifiers(first.getModifiers(),second.getModifiers()));

        fd.setVariables(mergeListNoDuplicate(first.getVariables(),second.getVariables(),true));

        return fd;
    }

    @Override public boolean isEquals(FieldDeclaration first, FieldDeclaration second) {

        if(first == second) return true;
        if(first == null || second == null) return false;

        if(!isSmallerHasEqualsInBigger(first.getVariables(),second.getVariables(),false)) return false;

        return true;
    }
}
