package com.freetmp.mbg.merge;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.TypeDeclaration;

import java.io.StringReader;

/**
 * Created by LiuPin on 2015/3/27.
 */
public class CompilationUnitMerger extends AbstractMerger<CompilationUnit> {

  @Override
  public CompilationUnit doMerge(CompilationUnit first, CompilationUnit second) {

    CompilationUnit unit = new CompilationUnit();

    unit.setPackage(mergeSingle(first.getPackage(), second.getPackage()));

    unit.setImports(mergeCollections(first.getImports(), second.getImports()));

    unit.setTypes(mergeCollections(first.getTypes(), second.getTypes()));

    return unit;
  }

  @Override
  public boolean doIsEquals(CompilationUnit first, CompilationUnit second) {
    // 检测包声明
    if (!isEqualsUseMerger(first.getPackage(), second.getPackage())) return false;

    // 检查公共类声明
    for (TypeDeclaration outer : first.getTypes()) {
      for (TypeDeclaration inner : second.getTypes()) {
        if (ModifierSet.isPublic(outer.getModifiers()) && ModifierSet.isPublic(inner.getModifiers())) {
          if (outer.getName().equals(inner.getName())) {
            return true;
          }
        }
      }
    }

    return false;
  }

  /**
   * Util method to make source merge more convenient
   *
   * @param first  merge params, specifically for the existing source
   * @param second merge params, specifically for the new source
   * @return merged result
   * @throws ParseException cannot parse the input params
   */
  public static String merge(String first, String second) throws ParseException {
    JavaParser.setDoNotAssignCommentsPreceedingEmptyLines(false);

    CompilationUnit cu1 = JavaParser.parse(new StringReader(first), true);
    CompilationUnit cu2 = JavaParser.parse(new StringReader(second), true);
    AbstractMerger<CompilationUnit> merger = AbstractMerger.getMerger(CompilationUnit.class);
    CompilationUnit result = merger.merge(cu1, cu2);
    return result.toString();
  }

}
