package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;

import java.util.List;

/**
 * Created by pin on 2015/4/19.
 */
public class NormalAnnotationExprMerger extends AbstractMerger<NormalAnnotationExpr> {

    private NormalAnnotationExprMerger(){}

    static {
        if(getMerger(NormalAnnotationExpr.class) == null){
            register(NormalAnnotationExpr.class,new NormalAnnotationExprMerger());
        }
    }

    @Override
    public NormalAnnotationExpr merge(NormalAnnotationExpr first, NormalAnnotationExpr second) {

        if(first.getPairs()==null) return first;

        return first.getPairs().size() < second.getPairs().size() ? second : first;
    }

    /**
     * 1. check the name
     * 2. check the member including key and value
     *      if their size is not the same and the less one is all matched in the more one return true
     */
    @Override
    public boolean isEquals(NormalAnnotationExpr first, NormalAnnotationExpr second) {

        if(first == second) return true;
        if(first == null || second == null) return false;

        boolean equals = true;

        if(!first.getName().equals(second.getName())) equals = false;

        if(equals == true){

            if(first.getPairs() == null) return second.getPairs() == null;

            List<MemberValuePair> smaller;
            List<MemberValuePair> bigger;

            if(first.getPairs().size() <= second.getPairs().size()){
                smaller = first.getPairs();
                bigger = second.getPairs();
            }else {
                smaller = second.getPairs();
                bigger = second.getPairs();
            }

            for(MemberValuePair mvp : smaller){
                if(!bigger.contains(mvp)){
                    equals = false; break;
                }
            }

        }

        return equals;
    }
}
