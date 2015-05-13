package com.freetmp.mbg.merge.declaration;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.PackageDeclaration;

/**
 * Created by LiuPin on 2015/5/13.
 */
public class PackageDeclarationMerger extends AbstractMerger<PackageDeclaration> {

  @Override public PackageDeclaration merge(PackageDeclaration first, PackageDeclaration second) {
    PackageDeclaration packageDeclaration = new PackageDeclaration();

    packageDeclaration.setName(first.getName());
    packageDeclaration.setComment(mergeSingle(first.getComment(),second.getComment()));
    packageDeclaration.setAnnotations(mergeCollections(first.getAnnotations(),second.getAnnotations()));

    return packageDeclaration;
  }

  @Override public boolean isEquals(PackageDeclaration first, PackageDeclaration second) {

    if(first == second) return true;

    if(!first.getName().equals(second.getName())) return false;

    return true;
  }
}
