package com.freetmp.mbg.merge.type;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.type.ReferenceType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pin on 2015/4/19.
 */
public class ReferenceTypeMerger extends AbstractMerger<ReferenceType> {

  @Override
  public ReferenceType merge(ReferenceType first, ReferenceType second) {

    ReferenceType rf = new ReferenceType();
    rf.setArrayCount(first.getArrayCount());
    rf.setComment(mergeSingle(first.getComment(), second.getComment()));
    rf.setType(first.getType());
    rf.setAnnotations(mergeCollections(first.getAnnotations(), second.getAnnotations()));

    List<List<AnnotationExpr>> lists = null;
    if (first.getArraysAnnotations() == null || first.getArraysAnnotations().isEmpty()) lists = second.getArraysAnnotations();
    if (second.getArraysAnnotations() == null || second.getArraysAnnotations().isEmpty()) lists = first.getArraysAnnotations();

    if (lists == null) {
      List<List<AnnotationExpr>> faas = first.getArraysAnnotations();
      List<List<AnnotationExpr>> saas = second.getArraysAnnotations();
      lists = new ArrayList<>();
      int size = faas.size() > saas.size() ? faas.size() : saas.size();
      for (int i = 0; i < size; i++) {
        List<AnnotationExpr> faa = i < faas.size() ? faas.get(i) : null;
        List<AnnotationExpr> saa = i < saas.size() ? saas.get(i) : null;
        if (isAllNotNull(faa, saa)) {
          lists.add(mergeCollections(faa, saa));
        } else {
          lists.add(faa == null ? saa : faa);
        }
      }
    }
    rf.setArraysAnnotations(lists);

    return rf;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean isEquals(ReferenceType first, ReferenceType second) {

    if (first == second) return true;
    if (first == null || second == null) return false;

    if (first.getArrayCount() != second.getArrayCount()) return false;

    if (!isEqualsUseMerger(first.getType(),second.getType())) return false;

    return true;
  }
}
