package com.freetmp.mbg.merge.variable;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.body.VariableDeclaratorId;

/**
 * Created by LiuPin on 2015/4/20.
 */
public class VariableDeclaratorMerger extends AbstractMerger<VariableDeclarator> {

    @Override public VariableDeclarator doMerge(VariableDeclarator first, VariableDeclarator second) {

        if(first.getInit() == second.getInit()) return first;

        VariableDeclarator vd = new VariableDeclarator();

        vd.setId(first.getId());
        vd.setInit(mergeSingle(first.getInit(),second.getInit()));

        return vd;
    }

    @Override public boolean doIsEquals(VariableDeclarator first, VariableDeclarator second) {

        if(!getMerger(VariableDeclaratorId.class).isEquals(first.getId(),second.getId())) return false;

        return true;
    }
}
