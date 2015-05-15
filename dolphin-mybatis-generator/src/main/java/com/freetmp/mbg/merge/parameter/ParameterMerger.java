package com.freetmp.mbg.merge.parameter;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclaratorId;

/**
 * Created by pin on 2015/4/19.
 */
public class ParameterMerger extends AbstractMerger<Parameter> {

    @Override
    public Parameter doMerge(Parameter first, Parameter second) {

        Parameter parameter = new Parameter();

        parameter.setType(first.getType());
        parameter.setId(first.getId());
        parameter.setVarArgs(first.isVarArgs());
        parameter.setModifiers(mergeModifiers(first.getModifiers(), second.getModifiers()));
        parameter.setAnnotations(mergeCollections(first.getAnnotations(), second.getAnnotations()));

        return parameter;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean doIsEquals(Parameter first, Parameter second) {

        if(first.isVarArgs() != second.isVarArgs()) return false;

        if(!getMerger(VariableDeclaratorId.class).isEquals(first.getId(),second.getId())) return false;

        if(!first.getType().getClass().equals(second.getType().getClass())) return false;

        AbstractMerger merger = getMerger(first.getType().getClass());
        if(!merger.isEquals(first.getType(),second.getType())) return false;

        return true;
    }
}
