package com.freetmp.mbg.merge.declaration;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.body.EnumDeclaration;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by pin on 2015/4/19.
 */
public class EnumDeclarationMerger extends AbstractMerger<EnumDeclaration> {

    private EnumDeclarationMerger(){}

    static {
        if(getMerger(EnumDeclaration.class) == null){
            register(EnumDeclaration.class, new EnumDeclarationMerger());
        }
    }

    @Override
    public EnumDeclaration merge(EnumDeclaration first, EnumDeclaration second) {

        EnumDeclaration ed = new EnumDeclaration();

        ed.setModifiers(mergeModifiers(first.getModifiers(), second.getModifiers()));
        ed.setJavaDoc(mergeSingle(first.getJavaDoc(), second.getJavaDoc()));
        ed.setComment(mergeSingle(first.getComment(), second.getComment()));
        ed.setAnnotations(mergeCollections(first.getAnnotations(), second.getAnnotations()));

        ed.setImplements(mergeCollections(first.getImplements(), second.getImplements()));
        ed.setEntries(mergeCollections(first.getEntries(), second.getEntries()));
        ed.setMembers(mergeCollections(first.getMembers(), second.getMembers()));

        ed.setName(first.getName());

        return ed;
    }

    @Override
    public boolean isEquals(EnumDeclaration first, EnumDeclaration second) {
        if(first == second) return true;
        if(first == null || second == null) return false;

        return StringUtils.equals(first.getName(),second.getName());
    }
}
