package com.freetmp.errorcoder.mediator;

import com.freetmp.errorcoder.base.ErrorCode;
import com.freetmp.errorcoder.mapper.Mapper;
import com.freetmp.errorcoder.render.JacksonRender;
import com.freetmp.errorcoder.render.Render;
import com.freetmp.errorcoder.render.ToStringRender;
import org.apache.log4j.BasicConfigurator;
import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

/**
 * Created by LiuPin on 2015/4/21.
 */
public class RenderTest {

    static ErrorCode code = null;

    @BeforeClass
    public static void setUp(){
        BasicConfigurator.configure();
        MapperRegistry.scanForMapper("com.freetmp.errorcoder.mapper.jdk");

        Throwable throwable = new ClassNotFoundException(RenderTest.class.getName());
        Mapper mapper = MapperRegistry.findMapper(throwable);
        code = mapper.map(throwable);

        Assertions.assertThat(code).isNotNull();
    }

    @Test
    public void testToStringRender(){

        Render render = new ToStringRender();
        String result = render.render(code, StandardCharsets.UTF_8.displayName());

        Assertions.assertThat(result).isNotEmpty();
        System.out.println(result);
    }

    @Test
    public void testJacksonRender(){
        Render render = new JacksonRender();
        String result = render.render(code, StandardCharsets.UTF_8.displayName());

        Assertions.assertThat(result).isNotEmpty();
        System.out.println(result);
    }
}
