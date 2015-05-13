package com.freetmp.mbg.merge.expression;

import com.freetmp.mbg.merge.AbstractMerger;
import com.github.javaparser.ast.expr.Expression;

/**
 * Created by LiuPin on 2015/4/20.
 */
public class ExpressionMerger extends AbstractMerger<Expression> {

    @Override public Expression merge(Expression first, Expression second) {
        return first;
    }

    @Override public boolean isEquals(Expression first, Expression second) {
        if(first == second) return true;
        if(first == null || second == null) return false;

        return first.equals(second);
    }
}
