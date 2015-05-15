package com.freetmp.mbg.merge.type;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.WildcardType;

/**
 * Created by pin on 2015/4/19.
 */
public class WildcardTypeMerger extends AbstractMerger<WildcardType> {

  @Override
  public WildcardType doMerge(WildcardType first, WildcardType second) {
    WildcardType wt = new WildcardType();
    wt.setAnnotations(mergeCollections(first.getAnnotations(),second.getAnnotations()));
    wt.setExtends(mergeSingle(first.getExtends(),second.getExtends()));
    wt.setSuper(mergeSingle(first.getSuper(),second.getSuper()));
    return first;
  }

  @Override
  public boolean doIsEquals(WildcardType first, WildcardType second) {

    if (!isEqualsUseMerger(first.getExtends(), second.getExtends())) return false;

    if (!isEqualsUseMerger(first.getSuper(), second.getSuper())) return false;

    return true;
  }
}
