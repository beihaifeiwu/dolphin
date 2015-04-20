package com.freetmp.errorcoder.mediator;

import com.freetmp.errorcoder.mapper.Mapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LiuPin on 2015/4/20.
 */
public class MapperRegistry {

    protected static final Map<Class<? extends Throwable>,Mapper> registry = new ConcurrentHashMap<>();

    /**
     * if the mapper for the specific exception class already exists
     * just overwrite it
     */
    @SuppressWarnings("unchecked")
    public static void register(Mapper mapper){
        if(mapper == null) return;

        registry.put(mapper.mapTo(),mapper);
    }

    /**
     * find the mapper in the exception class hierarchy
     * @param throwable cached exception
     * @return mapper for input exception
     */
    @SuppressWarnings("unchecked")
    public Mapper findMapper(Throwable throwable){
        Mapper mapper = null;
        Class<? extends Throwable> clazz = throwable.getClass();

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
    public static void scanForMapper(String[] paths){


    }

}
