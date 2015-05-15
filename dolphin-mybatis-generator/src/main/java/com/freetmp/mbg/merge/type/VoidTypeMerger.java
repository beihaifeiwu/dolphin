package com.freetmp.mbg.merge.type;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.type.VoidType;

/**
 * Created by pin on 2015/4/19.
 */
public class VoidTypeMerger extends AbstractMerger<VoidType> {

  @Override
  public VoidType doMerge(VoidType first, VoidType second) {
    VoidType vt = new VoidType();

    vt.setAnnotations(mergeCollections(first.getAnnotations(),second.getAnnotations()));

    return vt;
  }

  @Override
  public boolean doIsEquals(VoidType first, VoidType second) {

    return true;
  }
}
