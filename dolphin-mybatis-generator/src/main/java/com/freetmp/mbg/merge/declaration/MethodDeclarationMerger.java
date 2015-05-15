package com.freetmp.mbg.merge.declaration;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by LiuPin on 2015/4/20.
 */
public class MethodDeclarationMerger extends AbstractMerger<MethodDeclaration> {

  @Override public MethodDeclaration merge(MethodDeclaration first, MethodDeclaration second) {

    MethodDeclaration md = new MethodDeclaration();
    md.setName(first.getName());
    md.setType(mergeSingle(first.getType(), second.getType()));
    md.setJavaDoc(mergeSingle(first.getJavaDoc(), second.getJavaDoc()));
    md.setModifiers(mergeModifiers(first.getModifiers(), second.getModifiers()));
    md.setComment(mergeSingle(first.getComment(), second.getComment()));

    md.setDefault(first.isDefault() || second.isDefault());
    md.setArrayCount(Math.max(first.getArrayCount(), second.getArrayCount()));

    md.setAnnotations(mergeCollections(first.getAnnotations(), second.getAnnotations()));

    md.setThrows(mergeListNoDuplicate(first.getThrows(), second.getThrows(), false));
    md.setParameters(mergeCollectionsInOrder(first.getParameters(), second.getParameters()));
    md.setTypeParameters(mergeCollectionsInOrder(first.getTypeParameters(), second.getTypeParameters()));

    md.setBody(mergeSingle(first.getBody(), second.getBody()));

    List<Comment> list = mergeCollections(first.getOrphanComments(),second.getOrphanComments());
    for(Comment comment : list){
      md.addOrphanComment(comment);
    }

    return md;
  }

  @Override public boolean isEquals(MethodDeclaration first, MethodDeclaration second) {

    if (first == second) return true;
    if (first == null || second == null) return false;

    if (StringUtils.equals(first.getName(), second.getName())) return false;

    if (!isParametersEquals(first.getParameters(), second.getParameters())) return false;

    if (!isTypeParameterEquals(first.getTypeParameters(), second.getTypeParameters())) return false;

    return true;
  }
}