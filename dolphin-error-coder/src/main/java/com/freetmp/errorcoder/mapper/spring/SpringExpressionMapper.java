package com.freetmp.errorcoder.mapper.spring;

import com.freetmp.errorcoder.annotation.ErrorCodeMapper;
import com.freetmp.errorcoder.annotation.LoadOnClassExist;
import com.freetmp.errorcoder.base.ErrorCode;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionException;

import java.text.ParseException;

/**
 * Created by LiuPin on 2015/4/22.
 */
@LoadOnClassExist(Expression.class)
public class SpringExpressionMapper {

    @ErrorCodeMapper
    public ErrorCode expression(ExpressionException exception, ErrorCode code){
        return code;
    }

    @ErrorCodeMapper
    public ErrorCode evaluation(EvaluationException exception, ErrorCode code){
        return code;
    }

    @ErrorCodeMapper
    public ErrorCode parse(ParseException exception, ErrorCode code){
        return code;
    }
}
