package com.freetmp.errorcoder.support.filter;

import com.freetmp.errorcoder.mediator.ErrorCoder;
import com.freetmp.errorcoder.support.ErrorCoderConfiguration;
import com.freetmp.errorcoder.support.ParameterConstant;
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

    ErrorCoder coder = null;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String mapperLocations = filterConfig.getInitParameter(ParameterConstant.MAPPER_LOCATIONS_KEY);
        String renderType = filterConfig.getInitParameter(ParameterConstant.RENDER_TYPE_KEY);
        String renderClass = filterConfig.getInitParameter(ParameterConstant.RENDER_CLASS_KEY);

        ErrorCoderConfiguration configuration = new ErrorCoderConfiguration(mapperLocations,renderType,renderClass);

        coder = configuration.configure();
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
        if(coder != null) coder.clear();
    }
}
