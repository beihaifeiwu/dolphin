package com.freetmp.mbg.merge;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.TypeDeclaration;

/**
 * Created by LiuPin on 2015/3/27.
 */
public class CompilationUnitMerger extends AbstractMerger<CompilationUnit> {

    private CompilationUnitMerger(){}

    static {
        if(getMerger(CompilationUnit.class) == null){
            register(CompilationUnit.class,new CompilationUnitMerger());
        }
    }

    @Override
    public CompilationUnit merge(CompilationUnit first, CompilationUnit second) {

        CompilationUnit unit = new CompilationUnit();

        unit.setPackage(mergeSingle(first.getPackage(), second.getPackage()));

        unit.setImports(mergeCollections(first.getImports(), second.getImports()));

        unit.setTypes(mergeCollections(first.getTypes(), second.getTypes()));

        return unit;
    }

    @Override
    public boolean isEquals(CompilationUnit first, CompilationUnit second) {

        boolean equals = false;

        // 检测包声明
        if(!first.getPackage().equals(second)) return equals;

        // 检查公共类声明
        for(TypeDeclaration outer : first.getTypes()){
            for(TypeDeclaration inner : second.getTypes()){
                if(ModifierSet.isPublic(outer.getModifiers()) && ModifierSet.isPublic(inner.getModifiers())){
                    if(outer.getName().equals(inner.getName())){
                        equals = true; break;
                    }
                }
            }
        }

        return equals;
    }
}
