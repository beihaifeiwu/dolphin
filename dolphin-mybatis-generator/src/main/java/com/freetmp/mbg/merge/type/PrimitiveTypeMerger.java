package com.freetmp.mbg.merge.type;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.type.PrimitiveType;


/**
 * Created by pin on 2015/4/19.
 */
public class PrimitiveTypeMerger extends AbstractMerger<PrimitiveType> {

  @Override
  public PrimitiveType doMerge(PrimitiveType first, PrimitiveType second) {
    PrimitiveType pt = new PrimitiveType();
    pt.setAnnotations(mergeCollections(first.getAnnotations(),second.getAnnotations()));
    pt.setType(first.getType());
    return first;
  }

  @Override
  public boolean doIsEquals(PrimitiveType first, PrimitiveType second) {

    if (!first.getType().equals(second.getType())) return false;

    return true;
  }
}
