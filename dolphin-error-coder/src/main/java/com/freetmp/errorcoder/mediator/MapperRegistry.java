package com.freetmp.errorcoder.mediator;

import com.freetmp.common.util.Assert;
import com.freetmp.errorcoder.base.MapperMetadata;
import com.freetmp.errorcoder.mapper.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by LiuPin on 2015/4/20.
 */
public class MapperRegistry {

    private static final Logger log = LoggerFactory.getLogger(MapperRegistry.class);

    protected static final Map<Class<? extends Throwable>,Mapper> registry = new ConcurrentHashMap<>();

    /**
     * if the mapper for the specific exception class already exists
     * just overwrite it
     */
    @SuppressWarnings("unchecked")
    public static void register(Mapper mapper){
        if(mapper == null) return;
        if(registry.get(mapper.mapTo()) != null){
            log.info("Overwrite the existing mapper {} with mapper {}",registry.get(mapper.mapTo()),mapper);
        }else {
            log.debug("Register mapper {}",mapper);
        }
        registry.put(mapper.mapTo(),mapper);
    }

    /**
     * find the mapper in the exception class hierarchy
     * @param throwable cached exception
     * @return mapper for input exception
     */
    public static Mapper findMapper(Throwable throwable){
        Assert.notNull(throwable);
        return findMapper(throwable.getClass());
    }

    @SuppressWarnings("unchecked")
    public static Mapper findMapper(Class<? extends Throwable> clazz){
        Mapper mapper = null;

        while (mapper == null && clazz != null){
            mapper = registry.get(clazz);

            if(clazz.equals(Throwable.class)) break;
            clazz = (Class<? extends Throwable>) clazz.getSuperclass();
        }

        return mapper;
    }

    /**
     * scan the input paths and register the found mapper
     * @param paths the location where mapper exists
     */
    public static void scanForMapper(String... paths){

        log.info("Scan mappers in locations {}", Arrays.asList(paths));

        Set<MapperMetadata> mapperMetadatas = ClasspathScanner.scanForCandidateMapper(paths);

        Set<Mapper> mappers = mapperMetadatas.parallelStream()
                .flatMap(mapperMetadata -> mapperMetadata.getMappers().stream())
                .collect(Collectors.toSet());

        mappers.forEach(MapperRegistry::register);
    }

    public static void clear(){
        registry.clear();
    }

}
