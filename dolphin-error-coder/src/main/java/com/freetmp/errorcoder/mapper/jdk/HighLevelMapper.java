package com.freetmp.errorcoder.mapper.jdk;

import com.freetmp.errorcoder.annotation.ErrorCodeMapper;
import com.freetmp.errorcoder.base.ErrorCode;

/**
 * Created by LiuPin on 2015/4/20.
 */
@ErrorCodeMapper
public class HighLevelMapper {

    @ErrorCodeMapper(value = Throwable.class)
    public ErrorCode throwable(Throwable trowable,ErrorCode code){
        return code;
    }

    @ErrorCodeMapper(value = Error.class)
    public ErrorCode error(Error error, ErrorCode code){
        return code;
    }

    @ErrorCodeMapper(value = Exception.class)
    public ErrorCode exception(Exception exception, ErrorCode code){
        return code;
    }
}