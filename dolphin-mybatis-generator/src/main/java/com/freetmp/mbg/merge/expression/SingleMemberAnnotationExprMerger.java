package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;

/**
 * Created by pin on 2015/4/19.
 */
public class SingleMemberAnnotationExprMerger extends AbstractMerger<SingleMemberAnnotationExpr> {

    private SingleMemberAnnotationExprMerger(){}

    static {
        if(getMerger(SingleMemberAnnotationExpr.class) == null){
            register(SingleMemberAnnotationExpr.class,new SingleMemberAnnotationExprMerger());
        }
    }

    @Override
    public SingleMemberAnnotationExpr merge(SingleMemberAnnotationExpr first, SingleMemberAnnotationExpr second) {
        return first;
    }

    @Override
    public boolean isEquals(SingleMemberAnnotationExpr first, SingleMemberAnnotationExpr second) {

        if(first == second) return true;
        if(first == null || second == null) return false;

        return first.getName().equals(second.getName()) && first.getMemberValue().equals(second.getMemberValue());
    }
}
