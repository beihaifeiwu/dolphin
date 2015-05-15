package com.freetmp.mbg.merge.declaration;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.body.BodyDeclaration;

/**
 * Created by pin on 2015/4/19.
 */
public class BodyDeclarationMerger extends AbstractMerger<BodyDeclaration> {

  @Override
  public BodyDeclaration doMerge(BodyDeclaration first, BodyDeclaration second) {
    return first;
  }

  /**
   * Without further information, we can do nothing
   */
  @Override
  public boolean doIsEquals(BodyDeclaration first, BodyDeclaration second) {
    return first.equals(second);
  }
}
