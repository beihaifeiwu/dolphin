package com.freetmp.errorcoder.mediator;

import com.freetmp.errorcoder.base.MapperClassMetadata;
import com.freetmp.errorcoder.resource.PathMatchingResourcePatternResolver;
import com.freetmp.errorcoder.resource.Resource;
import com.freetmp.errorcoder.resource.ResourcePatternResolver;
import com.freetmp.errorcoder.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by LiuPin on 2015/4/20.
 */
public class ClasspathScanner {

    private static final Logger log = LoggerFactory.getLogger(ClasspathScanner.class);

    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    protected String resolveBasePackage(String basePackage){
        return ClassUtils.convertClassNameToResourcePath(basePackage);
    }

    public Set<MapperClassMetadata> findCandidateMapperClasses(String basePackage) throws IOException {
        Set<MapperClassMetadata> candidates = new LinkedHashSet<>();

        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                resolveBasePackage(basePackage) + "/" + DEFAULT_RESOURCE_PATTERN;

        Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
        for(Resource resource : resources){
            log.trace("Scanning " + resource);

            if(resource.isReadable()){

            }
        }

        return candidates;
    }
}
