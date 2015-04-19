package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;

/**
 * Created by pin on 2015/4/19.
 */
public class MarkerAnnotationExprMerger extends AbstractMerger<MarkerAnnotationExpr> {

    private MarkerAnnotationExprMerger(){}

    static {
        if(getMerger(MarkerAnnotationExpr.class) == null){
            register(MarkerAnnotationExpr.class,new MarkerAnnotationExprMerger());
        }
    }

    @Override
    public MarkerAnnotationExpr merge(MarkerAnnotationExpr first, MarkerAnnotationExpr second) {
        return first;
    }

    @Override
    public boolean isEquals(MarkerAnnotationExpr first, MarkerAnnotationExpr second) {

        if(first == second) return true;
        if(first == null || second == null) return false;

        return first.getName().equals(second.getName());
    }
}
