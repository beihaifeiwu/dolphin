package com.freetmp.mbg.merge.type;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by pin on 2015/4/19.
 */
public class ClassOrInterfaceTypeMerger extends AbstractMerger<ClassOrInterfaceType> {

    @Override
    public ClassOrInterfaceType doMerge(ClassOrInterfaceType first, ClassOrInterfaceType second) {

        ClassOrInterfaceType cift = new ClassOrInterfaceType();

        cift.setName(first.getName());
        cift.setScope(first.getScope());
        cift.setTypeArgs(first.getTypeArgs());

        cift.setAnnotations(mergeCollections(first.getAnnotations(), second.getAnnotations()));

        return cift;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean doIsEquals(ClassOrInterfaceType first, ClassOrInterfaceType second) {

        if(!StringUtils.equals(first.getName(),second.getName())) return false;

        // check type args in order
        List<Type> firstTypeArgs = first.getTypeArgs();
        List<Type> secondTypeArgs = second.getTypeArgs();

        if(firstTypeArgs == secondTypeArgs) return true;

        if(firstTypeArgs == null || secondTypeArgs == null) return false;

        for(int i = 0; i < firstTypeArgs.size(); i++){
            Type ft = firstTypeArgs.get(i);
            Type st = secondTypeArgs.get(i);
            if(ft.getClass().equals(st.getClass())) {
                AbstractMerger merger = getMerger(ft.getClass());
                if(!merger.isEquals(ft,st)) return false;
            }else {
                return false;
            }
        }

        // check scope recursively
        if(isAllNotNull(first.getScope(),second.getScope())){
            if(!isEquals(first.getScope(),second.getScope())) return false;

        }else if(!isAllNull(first.getScope(),second.getScope())){
            return false;
        }

        return true;
    }
}
