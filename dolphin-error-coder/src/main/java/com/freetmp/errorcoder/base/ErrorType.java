package com.freetmp.errorcoder.base;

/**
 * Created by LiuPin on 2015/4/22.
 */
public enum ErrorType {
    GENERAL_ERROR(10 * (long)Math.pow(10,10)),
    IO_ERROR(20 * (long)Math.pow(10,10)),
    SERVLET_ERROR(30 * (long)Math.pow(10,10)),
    SECURITY_ERROR(40 * (long)Math.pow(10,10)),
    DATASOURCE_ERROR(50 * (long)Math.pow(10,10)),
    REQUEST_ERROR(60 * (long)Math.pow(10,10)),
    INTERNAL_ERROR(70 * (long)Math.pow(10,10)),
    SERVER_ERROR(80 * (long)Math.pow(10,10)),
    DEAD_ERROR(90 * (long)Math.pow(10,10));

    private long header = 0L;

    ErrorType(long header){
        this.header = header;
    }

    public long getHeader() {
        return header;
    }
}
