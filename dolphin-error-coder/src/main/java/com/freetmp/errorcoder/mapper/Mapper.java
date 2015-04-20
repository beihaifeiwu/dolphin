package com.freetmp.errorcoder.mapper;

import com.freetmp.errorcoder.base.ErrorCode;

/**
 * Created by LiuPin on 2015/4/20.
 */
public interface Mapper<T extends Throwable> {

    ErrorCode map(T throwable);

    Class<T>  mapTo();
}
