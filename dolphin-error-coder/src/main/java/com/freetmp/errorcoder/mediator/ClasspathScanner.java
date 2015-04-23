package com.freetmp.errorcoder.mediator;

import com.freetmp.common.provider.ClassPathScanningProvider;
import com.freetmp.common.type.AnnotationMetadata;
import com.freetmp.common.type.classreading.MetadataReader;
import com.freetmp.common.type.filter.AnnotationTypeFilter;
import com.freetmp.common.util.ClassUtils;
import com.freetmp.errorcoder.annotation.ErrorCodeMapper;
import com.freetmp.errorcoder.annotation.LoadOnClassExist;
import com.freetmp.errorcoder.base.MapperMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by LiuPin on 2015/4/20.
 */
public class ClasspathScanner {

    private static final Logger log = LoggerFactory.getLogger(ClasspathScanner.class);

    public static boolean checkIfSpecifyClassExists(AnnotationMetadata annotationMetadata){
        String baseClass = null;
        if(annotationMetadata.hasAnnotation(LoadOnClassExist.class.getTypeName())){
           Map<String,Object> attributes = annotationMetadata.getAnnotationAttributes(LoadOnClassExist.class.getTypeName(),true);
           baseClass = (String) attributes.get("value");

       }else {
            Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(ErrorCodeMapper.class.getTypeName(), true);
            baseClass = (String) attributes.get("value");
        }
       return ClassUtils.isPresent(baseClass,Thread.currentThread().getContextClassLoader());
    }

    public static Set<MapperMetadata> scanForCandidateMapper(String... paths){
        Set<MapperMetadata> candidates = new HashSet<>();

        if(paths == null || paths.length <= 0) return candidates;

        ClassPathScanningProvider provider = new ClassPathScanningProvider();
        provider.addIncludeFilter(new AnnotationTypeFilter(ErrorCodeMapper.class));
        provider.addIncludeFilter(new AnnotationTypeFilter(LoadOnClassExist.class));

        Set<MetadataReader> metadataReaders = new HashSet<>();

        Arrays.asList(paths).forEach(path -> {
            try {
                Set<MetadataReader> mrs = provider.findCandidateComponents(path);
                metadataReaders.addAll(mrs);
            } catch (IOException e) {
                log.error("Cannot scan the path {}", path, e);
            }
        });

        Set<MapperMetadata> mms = metadataReaders.stream()
                .filter(metadataReader -> checkIfSpecifyClassExists(metadataReader.getAnnotationMetadata()))
                .map(mr-> new MapperMetadata(mr))
                .collect(Collectors.toSet());

        candidates.addAll(mms);

        return candidates;
    }
}
