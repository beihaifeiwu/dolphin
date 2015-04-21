package com.freetmp.errorcoder.base;

import com.freetmp.common.annotation.AnnotationUtils;
import com.freetmp.common.type.ClassMetadata;
import com.freetmp.common.type.classreading.MetadataReader;
import com.freetmp.common.util.Assert;
import com.freetmp.common.util.ClassUtils;
import com.freetmp.errorcoder.annotation.ErrorCodeMapper;
import com.freetmp.errorcoder.mapper.AnnotationBasedCodeMapper;
import com.freetmp.errorcoder.mapper.Mapper;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by LiuPin on 2015/4/21.
 */
public class MapperMetadata {

    Class<?> clazz;

    private volatile Object object;

    public MapperMetadata(MetadataReader metadataReader) {

        Assert.notNull(metadataReader);

        ClassMetadata classMetadata = classMetadata = metadataReader.getClassMetadata();
        try {
            clazz = ClassUtils.forName(classMetadata.getClassName(), Thread.currentThread().getContextClassLoader());
            object = clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected Mapper wrap(Method method) {
        Assert.notNull(method);
        ErrorCodeMapper errorCodeMapper = AnnotationUtils.getAnnotation(method, ErrorCodeMapper.class);

        AnnotationBasedCodeMapper mapper = new AnnotationBasedCodeMapper(method,object,errorCodeMapper.code(),errorCodeMapper.value());

        return mapper;
    }

    public Set<Mapper> getMappers(){
        Set<Mapper> mappers = null;

        mappers = Arrays.asList(clazz.getDeclaredMethods())
                    .stream()
                    .filter(m -> AnnotationUtils.getAnnotation(m, ErrorCodeMapper.class) != null)
                    .map(this::wrap)
                    .collect(Collectors.toSet());

        return mappers;
    }

}
