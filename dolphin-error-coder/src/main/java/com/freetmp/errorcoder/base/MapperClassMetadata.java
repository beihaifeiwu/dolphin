package com.freetmp.errorcoder.base;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by LiuPin on 2015/4/20.
 */
public class MapperClassMetadata {

    private Class<?> mapperClass;

    private String baseClassValue;

    public Class<?> getMapperClass() {
        return mapperClass;
    }

    public void setMapperClass(Class<?> mapperClass) {
        this.mapperClass = mapperClass;
    }

    public String getBaseClassValue() {
        return baseClassValue;
    }

    public void setBaseClassValue(String baseClassValue) {
        this.baseClassValue = baseClassValue;
    }

    public MapperClassMetadata(Class<?> mapperClass, String baseClassValue) {
        this.mapperClass = mapperClass;
        this.baseClassValue = baseClassValue;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MapperClassMetadata that = (MapperClassMetadata) o;

        return new EqualsBuilder()
                .append(mapperClass, that.mapperClass)
                .append(baseClassValue, that.baseClassValue)
                .isEquals();
    }

    @Override public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(mapperClass)
                .append(baseClassValue)
                .toHashCode();
    }

    @Override public String toString() {
        return new ToStringBuilder(this)
                .append("mapperClass", mapperClass)
                .append("baseClassValue", baseClassValue)
                .toString();
    }
}
