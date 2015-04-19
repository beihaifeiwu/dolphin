package com.freetmp.mbg.merge.declaration;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by pin on 2015/4/19.
 */
public class AnnotationDeclarationMerger extends AbstractMerger<AnnotationDeclaration> {

    private AnnotationDeclarationMerger(){}

    static {
        if(getMerger(AnnotationDeclaration.class) == null){
            register(AnnotationDeclaration.class,new AnnotationDeclarationMerger());
        }
    }

    @Override
    public AnnotationDeclaration merge(AnnotationDeclaration first, AnnotationDeclaration second) {

        AnnotationDeclaration ad = new AnnotationDeclaration();
        ad.setJavaDoc(mergeSingle(first.getJavaDoc(), second.getJavaDoc()));
        ad.setComment(mergeSingle(first.getComment(), second.getComment()));

        ad.setMembers(mergeCollections(first.getMembers(), second.getMembers()));
        ad.setAnnotations(mergeCollections(first.getAnnotations(), second.getAnnotations()));
        ad.setModifiers(mergeModifiers(first.getModifiers(),second.getModifiers()));

        return ad;
    }

    @Override
    public boolean isEquals(AnnotationDeclaration first, AnnotationDeclaration second) {
        if(first == second) return true;
        if(first == null || second == null) return false;

        return StringUtils.equals(first.getName(),second.getName());
    }

}