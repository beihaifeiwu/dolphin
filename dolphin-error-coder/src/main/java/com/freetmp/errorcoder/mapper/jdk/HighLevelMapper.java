package com.freetmp.errorcoder.mapper.jdk;

import com.freetmp.errorcoder.annotation.ErrorCodeMapper;
import com.freetmp.errorcoder.base.ErrorCode;
import com.freetmp.errorcoder.base.ErrorType;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;

/**
 * Created by LiuPin on 2015/4/20.
 */
@ErrorCodeMapper
public class HighLevelMapper {

    @ErrorCodeMapper
    public ErrorCode throwable(Throwable trowable,ErrorCode code){
        return code;
    }

    @ErrorCodeMapper(type = ErrorType.DEAD_ERROR)
    public ErrorCode error(Error error, ErrorCode code){
        return code;
    }

    @ErrorCodeMapper
    public ErrorCode exception(Exception exception, ErrorCode code){
        return code;
    }

    @ErrorCodeMapper(type = ErrorType.SECURITY_ERROR)
    public ErrorCode security(GeneralSecurityException exception, ErrorCode code){
        return code;
    }

    @ErrorCodeMapper(type = ErrorType.IO_ERROR)
    public ErrorCode io(IOException ioe, ErrorCode code){
        return code;
    }

    @ErrorCodeMapper(type = ErrorType.SERVER_ERROR)
    public ErrorCode runtime(RuntimeException re, ErrorCode code){
        return code;
    }

    @ErrorCodeMapper(type = ErrorType.DATASOURCE_ERROR)
    public ErrorCode sql(SQLException sql, ErrorCode code){
        return code;
    }
}