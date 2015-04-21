package com.freetmp.errorcoder.mapper;

import com.freetmp.errorcoder.base.ErrorCode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Created by LiuPin on 2015/4/20.
 */
public class AnnotationBasedCodeMapper implements Mapper {

    private static final Logger log = LoggerFactory.getLogger(AnnotationBasedCodeMapper.class);

    private final Method method;

    private final Object object;

    private final Class<? extends Throwable> clazz;

    private long code;

    public AnnotationBasedCodeMapper(Method method, Object object, long code, Class<? extends Throwable> clazz){
        this.method = method;
        this.object = object;
        this.code = code;
        this.clazz = clazz;
    }

    @Override public ErrorCode map(Throwable throwable) {
        try {
            Parameter[] parameters = method.getParameters();

            switch (parameters.length){
                case 0:
                    return (ErrorCode) method.invoke(object);
                case 1:
                    return (ErrorCode) method.invoke(object,throwable);
                case 2:
                    return (ErrorCode) method.invoke(object,throwable,new ErrorCode(code,throwable.getMessage()));
            }
        } catch (Exception e) {
            log.error("error occurred when invoked the mapping method",e);
        }
        return ErrorCode.ERROR_MAPPING;
    }

    @Override public Class<? extends Throwable> mapTo() {
        return clazz;
    }

    @Override public String toString() {
        return new ToStringBuilder(this)
                .append("method", method.getName())
                .append("object", object.getClass().getName())
                .append("clazz", clazz.getTypeName())
                .append("code", code)
                .toString();
    }
}
