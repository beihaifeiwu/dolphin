package com.freetmp.errorcoder.support.springmvc;

import com.freetmp.errorcoder.mediator.ErrorCoder;
import com.freetmp.errorcoder.support.ErrorCoderConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by LiuPin on 2015/4/22.
 */
public class ErrorCoderHandlerExceptionResolver implements HandlerExceptionResolver {

    private static final Logger log = LoggerFactory.getLogger(ErrorCoderHandlerExceptionResolver.class);

    @Autowired
    private ErrorCoderConfiguration errorCoderConfiguration;

    private ErrorCoder coder = null;

    @PostConstruct
    public void  init(){

        if(errorCoderConfiguration != null) errorCoderConfiguration = new ErrorCoderConfiguration();

        coder = errorCoderConfiguration.configure();
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        String renderResult;
        if(!StringUtils.isEmpty(response.getCharacterEncoding())) {
            renderResult = coder.handle(ex, response.getCharacterEncoding());
        }else {
            renderResult = coder.handle(ex);
        }
        try {
            response.getWriter().println(renderResult);
            response.flushBuffer();
        } catch (IOException e) {
            log.error("Cannot write response to the client");
        }

        return null;
    }

    @PreDestroy
    public void clear(){
        if(coder != null) coder.clear();
    }
}
