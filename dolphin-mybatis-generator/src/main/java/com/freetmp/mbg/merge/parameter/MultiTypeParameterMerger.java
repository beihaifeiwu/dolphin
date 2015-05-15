package com.freetmp.mbg.merge.parameter;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.body.MultiTypeParameter;
import com.github.javaparser.ast.body.VariableDeclaratorId;
import com.github.javaparser.ast.type.Type;

import java.util.List;

/**
 * Created by pin on 2015/4/19.
 */
public class MultiTypeParameterMerger extends AbstractMerger<MultiTypeParameter> {

    @Override
    public MultiTypeParameter doMerge(MultiTypeParameter first, MultiTypeParameter second) {

        MultiTypeParameter mtp = new MultiTypeParameter();

        mtp.setModifiers(mergeModifiers(first.getModifiers(), second.getModifiers()));
        mtp.setId(first.getId());
        mtp.setAnnotations(mergeCollections(first.getAnnotations(), second.getAnnotations()));
        mtp.setTypes(first.getTypes());

        return mtp;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean doIsEquals(MultiTypeParameter first, MultiTypeParameter second) {

        if(!getMerger(VariableDeclaratorId.class).isEquals(first.getId(),second.getId())) return false;

        List<Type> firstTypes = first.getTypes();
        List<Type> secondTypes = second.getTypes();

        if(firstTypes == null) return secondTypes == null;
        if(secondTypes == null) return false;

        if(firstTypes.size() != secondTypes.size()) return false;

        for(Type ft : firstTypes){
            boolean found = false;
            AbstractMerger merger = getMerger(ft.getClass());
            for(Type st : secondTypes){
                if(merger.isEquals(ft, st)){
                    found = true; break;
                }
            }
            if(!found) return false;
        }

        return true;
    }
}
