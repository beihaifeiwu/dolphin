package com.freetmp.errorcoder.mapper.spring;

import com.freetmp.errorcoder.annotation.ErrorCodeMapper;
import com.freetmp.errorcoder.annotation.LoadOnClassExist;
import com.freetmp.errorcoder.base.ErrorCode;
import com.freetmp.errorcoder.base.ErrorType;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.dao.support.DaoSupport;

/**
 * Created by LiuPin on 2015/4/22.
 */
@LoadOnClassExist(DaoSupport.class)
public class SpringDaoMapper {

    @ErrorCodeMapper(type = ErrorType.DATASOURCE_ERROR)
    public ErrorCode dae(DataAccessException exception, ErrorCode code){
        return code;
    }

    @ErrorCodeMapper(type = ErrorType.DATASOURCE_ERROR)
    public ErrorCode ntdae(NonTransientDataAccessException exception, ErrorCode code){
        return code;
    }

    @ErrorCodeMapper(type = ErrorType.DATASOURCE_ERROR)
    public ErrorCode rdae(RecoverableDataAccessException exception, ErrorCode code){
        return code;
    }

    @ErrorCodeMapper(type = ErrorType.DATASOURCE_ERROR)
    public ErrorCode tdae(TransientDataAccessException exception, ErrorCode code){
        return code;
    }
}
