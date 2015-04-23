package com.freetmp.errorcoder.mapper.spring;

import com.freetmp.errorcoder.annotation.ErrorCodeMapper;
import com.freetmp.errorcoder.annotation.LoadOnClassExist;
import com.freetmp.errorcoder.base.ErrorCode;
import com.freetmp.errorcoder.base.ErrorType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;

/**
 * Created by LiuPin on 2015/4/22.
 */
@LoadOnClassExist(ApplicationContext.class)
public class SpringContextMapper {

    @ErrorCodeMapper(type = ErrorType.INTERNAL_ERROR)
    public ErrorCode context(ApplicationContextException exception, ErrorCode code){
        return code;
    }
}
