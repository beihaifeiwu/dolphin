package com.freetmp.errorcoder.annotation;

import com.freetmp.errorcoder.base.ErrorType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by LiuPin on 2015/4/20.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ErrorCodeMapper {

    Class<? extends Throwable> value() default Throwable.class;

    ErrorType type() default ErrorType.GENERAL_ERROR;
}