package com.freetmp.mbg.merge.declaration;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.body.BodyDeclaration;

/**
 * Created by pin on 2015/4/19.
 */
public class BodyDeclarationMerger extends AbstractMerger<BodyDeclaration> {

  @Override
  public BodyDeclaration merge(BodyDeclaration first, BodyDeclaration second) {
    return null;
  }

  /**
   * Without further information, we can do nothing
   */
  @Override
  public boolean isEquals(BodyDeclaration first, BodyDeclaration second) {
    return false;
  }
}
