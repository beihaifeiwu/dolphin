package com.freetmp.errorcoder.mapper;

import com.freetmp.errorcoder.base.ErrorCode;
import com.freetmp.errorcoder.base.ErrorType;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

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

    private ErrorType type;

    public AnnotationBasedCodeMapper(Method method, Object object, ErrorType type, Class<? extends Throwable> clazz){
        this.method = method;
        this.object = object;
        this.type = type;
        this.clazz = clazz;
    }

    public String determineMessage(Throwable throwable){
        if(StringUtils.isEmpty(throwable.getMessage())){
            return throwable.getClass().getTypeName();
        }
        return throwable.getMessage();
    }

    public long determineCode(Throwable throwable){
       int hashcode = new HashCodeBuilder(17, 37).append(throwable.getClass().getTypeName()).toHashCode();

       return type.getHeader() | hashcode;
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
                    return (ErrorCode) method.invoke(object,throwable,new ErrorCode(determineCode(throwable),determineMessage(throwable)));
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
        final StringBuilder sb = new StringBuilder("AnnotationBasedCodeMapper{");
        sb.append("method=").append(method);
        sb.append(", object=").append(object);
        sb.append(", clazz=").append(clazz);
        sb.append(", type=").append(type);
        sb.append('}');
        return sb.toString();
    }
}
