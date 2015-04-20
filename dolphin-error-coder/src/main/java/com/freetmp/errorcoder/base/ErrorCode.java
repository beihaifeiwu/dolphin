package com.freetmp.errorcoder.base;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by LiuPin on 2015/4/20.
 */
public class ErrorCode implements Serializable {

    public static final ErrorCode ERROR_MAPPING = new ErrorCode(1000,"Error occurred when invoke the map method");

    private long code;

    private String message;

    private Map<String,Object> props;

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getProps() {
        return props;
    }

    public void setProps(Map<String, Object> props) {
        this.props = props;
    }

    public ErrorCode(){}

    public ErrorCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorCode(long code, String message, Map<String, Object> props) {
        this.code = code;
        this.message = message;
        this.props = props;
    }

    public ErrorCode merge(ErrorCode errorCode){
        this.code = errorCode.code;
        this.message = errorCode.message;
        this.props.putAll(errorCode.getProps());
        return this;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ErrorCode errorCode = (ErrorCode) o;

        return new EqualsBuilder()
                .append(code, errorCode.code)
                .append(message, errorCode.message)
                .append(props, errorCode.props)
                .isEquals();
    }

    @Override public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(code)
                .append(message)
                .append(props)
                .toHashCode();
    }

    @Override public String toString() {
        return new ToStringBuilder(this)
                .append("code", code)
                .append("message", message)
                .append("props", props)
                .toString();
    }
}
