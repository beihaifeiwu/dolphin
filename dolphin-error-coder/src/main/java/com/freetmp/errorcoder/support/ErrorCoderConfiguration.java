package com.freetmp.errorcoder.support;

import com.freetmp.common.util.ClassUtils;
import com.freetmp.errorcoder.mediator.ErrorCoder;
import com.freetmp.errorcoder.render.Render;
import com.freetmp.errorcoder.render.RenderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Created by LiuPin on 2015/4/22.
 */
public class ErrorCoderConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ErrorCoderConfiguration.class);

    /**
     * user custom exception mapper classpath, split by comma
     */
    private String mapperLocations;

    /**
     * specify which response type to use, PLAIN_TEXT or JSON, default is JSON
     */
    private String renderType;

    /**
     * specify the class name of custom render
     */
    private String renderClass;

    public ErrorCoderConfiguration(){}

    public ErrorCoderConfiguration(String mapperLocations, String renderType, String renderClass) {
        this.mapperLocations = mapperLocations;
        this.renderType = renderType;
        this.renderClass = renderClass;
    }

    public String getMapperLocations() {
        return mapperLocations;
    }

    public void setMapperLocations(String mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    public String getRenderType() {
        return renderType;
    }

    public void setRenderType(String renderType) {
        this.renderType = renderType;
    }

    public String getRenderClass() {
        return renderClass;
    }

    public void setRenderClass(String renderClass) {
        this.renderClass = renderClass;
    }

    @SuppressWarnings("unchecked")
    public ErrorCoder configure(){
        ErrorCoder.ErrorCoderBuilder builder = ErrorCoder.newBuilder();

        boolean configureRenderSuccess = false;
        if(!StringUtils.isEmpty(renderType)){
            try {
                RenderType type = RenderType.valueOf(renderType);
                builder.render(type);
                configureRenderSuccess = true;
            } catch (IllegalArgumentException e) {
                log.warn("Cannot create render type from {}", renderType, e);
            }
        }

        if(!StringUtils.isEmpty(renderClass)){
            try {
                Class<? extends Render> clazz = (Class<? extends Render>) ClassUtils.forName(renderClass, Thread.currentThread().getContextClassLoader());
                Render render = clazz.newInstance();
                builder.render(render);
                configureRenderSuccess = true;
            } catch (Exception e) {
                log.error("Cannot create render object of type {}", renderClass, e);
            }
        }

        if(!configureRenderSuccess) builder.render(RenderType.JSON);

        return builder.paths(StringUtils.commaDelimitedListToStringArray(mapperLocations)).build();
    }

    @Override public String toString() {
        final StringBuilder sb = new StringBuilder("ErrorCoderConfiguration{");
        sb.append("mapperLocations='").append(mapperLocations).append('\'');
        sb.append(", renderType='").append(renderType).append('\'');
        sb.append(", renderClass='").append(renderClass).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
