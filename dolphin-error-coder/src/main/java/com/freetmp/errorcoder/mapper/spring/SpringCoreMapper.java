package com.freetmp.errorcoder.mapper.spring;

import com.freetmp.errorcoder.annotation.ErrorCodeMapper;
import com.freetmp.errorcoder.annotation.LoadOnClassExist;
import com.freetmp.errorcoder.base.ErrorCode;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.NestedRuntimeException;
import org.springframework.core.convert.ConversionException;

/**
 * Created by LiuPin on 2015/4/22.
 */
@LoadOnClassExist(GenericTypeResolver.class)
public class SpringCoreMapper {

    @ErrorCodeMapper
    public ErrorCode nested(NestedRuntimeException exception, ErrorCode code){
        return code;
    }

    @ErrorCodeMapper
    public ErrorCode conversion(ConversionException exception, ErrorCode code){
        return code;
    }
}
