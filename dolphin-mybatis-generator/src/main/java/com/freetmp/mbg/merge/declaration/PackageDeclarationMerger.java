package com.freetmp.mbg.merge.declaration;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.PackageDeclaration;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class PackageDeclarationMerger extends AbstractMerger<PackageDeclaration> {

  @Override public PackageDeclaration doMerge(PackageDeclaration first, PackageDeclaration second) {
    PackageDeclaration packageDeclaration = new PackageDeclaration();

    packageDeclaration.setName(first.getName());
    packageDeclaration.setAnnotations(mergeCollections(first.getAnnotations(),second.getAnnotations()));

    return packageDeclaration;
  }

  @Override public boolean doIsEquals(PackageDeclaration first, PackageDeclaration second) {

    if(!first.getName().equals(second.getName())) return false;

    return true;
  }
}
