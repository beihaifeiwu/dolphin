package com.freetmp.errorcoder.mediator;

import com.freetmp.common.util.Assert;
import com.freetmp.errorcoder.base.ErrorCode;
import com.freetmp.errorcoder.mapper.Mapper;
import com.freetmp.errorcoder.render.JacksonRender;
import com.freetmp.errorcoder.render.Render;
import com.freetmp.errorcoder.render.RenderType;
import com.freetmp.errorcoder.render.ToStringRender;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by LiuPin on 2015/4/21.
 */
public class ErrorCoder {

    private Render render;

    private String charset;

    private ErrorCoder(List<String> paths, Render render, String charset){
        MapperRegistry.scanForMapper(paths.toArray(new String[]{}));
        this.render = render;
        this.charset = charset;
    }

    public Render getRender() {
        return render;
    }

    /**
     * take care of the handle of the exception
     * @param throwable exception that happened at runtime
     * @return
     */
    public String handle(Throwable throwable, String charset){
        Mapper mapper = MapperRegistry.findMapper(throwable);
        ErrorCode code = mapper.map(throwable);
        return render.render(code,charset);
    }

    public String handle(Throwable throwable){
        return handle(throwable, this.charset);
    }

    public void clear(){
        MapperRegistry.clear();
    }

    public static ErrorCoderBuilder newBuilder(){
        return new ErrorCoderBuilder();
    }

    public static class ErrorCoderBuilder{
        private List<String> paths = new ArrayList<>();
        private Render render;
        private String charset = StandardCharsets.UTF_8.displayName();
        {
            paths.add("com.freetmp.errorcoder.mapper.jdk");
        }

        public ErrorCoderBuilder paths(String... path){
            paths.addAll(Arrays.asList(path));
            return this;
        }

        public ErrorCoderBuilder path(String path){
            paths.add(path);
            return this;
        }

        public ErrorCoderBuilder render(RenderType type){
            switch (type){
                case PLAIN_TEXT: render = new ToStringRender();break;
                case JSON: render = new JacksonRender();break;
                default: render = new JacksonRender();break;
            }
            return this;
        }

        public ErrorCoderBuilder render(Render render){
            this.render = render;
            return this;
        }

        public ErrorCoderBuilder charset(String charset){
            if(!StringUtils.isEmpty(charset)){
                this.charset = charset;
            }
            return this;
        }

        public ErrorCoder build(){
            Assert.notEmpty(paths);
            Assert.notNull(render);
            Assert.notNull(charset);
            return new ErrorCoder(paths,render,charset);
        }
    }
}
