package com.freetmp.common.provider;

import com.freetmp.common.type.classreading.MetadataReader;
import org.apache.log4j.BasicConfigurator;
import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

/**
 * Created by LiuPin on 2015/4/21.
 */
public class ClassPathScanningProviderTest {

    @BeforeClass
    public static void setUp(){
        BasicConfigurator.configure();
    }

    @Test
    public void testScan() throws IOException {

        ClassPathScanningProvider provider = new ClassPathScanningProvider();

        provider.addIncludeFilter((m, mf) -> true);

        Set<MetadataReader> components = provider.findCandidateComponents("com.freetmp.common.util");

        components.stream().map((mr)->mr.getResource()).forEach(System.out::println);

        Assertions.assertThat(components).isNotEmpty();
    }
}
