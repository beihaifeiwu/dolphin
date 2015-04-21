package com.freetmp.errorcoder.mediator;

import org.apache.log4j.BasicConfigurator;
import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by LiuPin on 2015/4/21.
 */
public class MapperRegistryTest {

    @BeforeClass
    public static void setUp(){
        BasicConfigurator.configure();
    }

    @Test
    public void testLoadMapper(){
        MapperRegistry.scanForMapper("com.freetmp.errorcoder.mapper.jdk");

        Assertions.assertThat(MapperRegistry.findMapper(Throwable.class)).isNotNull();
        Assertions.assertThat(MapperRegistry.findMapper(Error.class)).isNotNull();
        Assertions.assertThat(MapperRegistry.findMapper(Exception.class)).isNotNull();

        Assertions.assertThat(MapperRegistry.findMapper(TestHierarchyException.class))
                    .isEqualTo(MapperRegistry.findMapper(IOException.class));
    }
}
