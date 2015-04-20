package com.freetmp.errorcoder.mapper.jdk;

import com.freetmp.errorcoder.annotation.ErrorCodeMapper;
import com.freetmp.errorcoder.base.ErrorCode;

/**
 * Created by LiuPin on 2015/4/20.
 */
@ErrorCodeMapper
public class HighLevelMapper {

    @ErrorCodeMapper(value = Throwable.class,code = 1000)
    public ErrorCode throwable(Throwable trowable,ErrorCode code){
        return code;
    }

    @ErrorCodeMapper(value = Error.class,code = 1001)
    public ErrorCode error(Error error, ErrorCode code){
        return code;
    }

    @ErrorCodeMapper(value = Exception.class,code = 2001)
    public ErrorCode exception(Exception exception, ErrorCode code){
        return code;
    }
}
