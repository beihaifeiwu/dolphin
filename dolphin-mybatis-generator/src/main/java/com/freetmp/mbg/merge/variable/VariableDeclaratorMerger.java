package com.freetmp.mbg.merge.variable;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.body.VariableDeclaratorId;

/**
 * Created by LiuPin on 2015/4/20.
 */
public class VariableDeclaratorMerger extends AbstractMerger<VariableDeclarator> {

    private VariableDeclaratorMerger(){}

    static {
        if(getMerger(VariableDeclarator.class) == null){
            register(VariableDeclarator.class,new VariableDeclaratorMerger());
        }
    }

    @Override public VariableDeclarator merge(VariableDeclarator first, VariableDeclarator second) {

        if(first.getInit() == second.getInit()) return first;

        VariableDeclarator vd = new VariableDeclarator();

        vd.setId(first.getId());
        vd.setComment(mergeSingle(first.getComment(),second.getComment()));
        vd.setInit(mergeSingle(first.getInit(),second.getInit()));

        return vd;
    }

    @Override public boolean isEquals(VariableDeclarator first, VariableDeclarator second) {

        if(first == second) return true;
        if(first == null || second == null) return false;

        if(!getMerger(VariableDeclaratorId.class).isEquals(first.getId(),second.getId())) return false;

        return true;
    }
}
