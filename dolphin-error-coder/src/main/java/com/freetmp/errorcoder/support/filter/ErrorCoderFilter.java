package com.freetmp.errorcoder.support.filter;

import com.freetmp.common.util.ClassUtils;
import com.freetmp.errorcoder.mediator.ErrorCoder;
import com.freetmp.errorcoder.render.Render;
import com.freetmp.errorcoder.render.RenderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by LiuPin on 2015/4/21.
 */
public class ErrorCoderFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(ErrorCoderFilter.class);

    public static final String MAPPER_LOCATIONS_KEY = "mapperLocations";
    public static final String RENDER_TYPE_KEY = "renderType";
    public static final String RENDER_CLASS_KEY = "renderClass";

    ErrorCoder coder = null;

    @SuppressWarnings("unchecked") @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String mapperLocations = filterConfig.getInitParameter(MAPPER_LOCATIONS_KEY);
        String renderType = filterConfig.getInitParameter(RENDER_TYPE_KEY);
        String renderClass = filterConfig.getInitParameter(RENDER_CLASS_KEY);

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
                Class<? extends Render> clazz = (Class<? extends Render>) ClassUtils.forName(renderClass,Thread.currentThread().getContextClassLoader());
                Render render = clazz.newInstance();
                builder.render(render);
                configureRenderSuccess = true;
            } catch (Exception e) {
                log.error("Cannot create render object of type {}", renderClass, e);
            }
        }

        if(!configureRenderSuccess) builder.render(RenderType.JSON);

        coder = builder.paths(mapperLocations).build();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(request,response);
        } catch (Throwable throwable){
            String renderResult = null;
            if(!StringUtils.isEmpty(response.getCharacterEncoding())) {
                renderResult = coder.handle(throwable, response.getCharacterEncoding());
            }else {
                renderResult = coder.handle(throwable);
            }
            response.getWriter().println(renderResult);
            response.flushBuffer();
        }
    }

    @Override public void destroy() {
        coder.clear();
    }
}
