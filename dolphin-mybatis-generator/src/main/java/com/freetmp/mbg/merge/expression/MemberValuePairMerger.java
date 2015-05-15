package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.MemberValuePair;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class MemberValuePairMerger extends AbstractMerger<MemberValuePair> {

  @Override public MemberValuePair doMerge(MemberValuePair first, MemberValuePair second) {
    MemberValuePair mvp = new MemberValuePair();

    mvp.setName(first.getName());
    mvp.setValue(mergeSingle(first.getValue(),second.getValue()));

    return mvp;
  }

  @Override public boolean doIsEquals(MemberValuePair first, MemberValuePair second) {

    if(!first.getName().equals(second.getName())) return false;
    if(!isEqualsUseMerger(first.getValue(),second.getValue())) return false;

    return true;
  }
}
