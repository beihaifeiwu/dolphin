package com.freetmp.errorcoder.support.springmvc;

import com.freetmp.errorcoder.mediator.ErrorCoder;
import com.freetmp.errorcoder.support.ErrorCoderConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by LiuPin on 2015/4/22.
 */
@ControllerAdvice
public class ErrorCoderControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(ErrorCoderControllerAdvice.class);

    @Autowired
    private ErrorCoderConfiguration errorCoderConfiguration;

    private ErrorCoder coder = null;

    @SuppressWarnings("unchecked") @PostConstruct
    public void init(){
        if(errorCoderConfiguration == null) errorCoderConfiguration = new ErrorCoderConfiguration();

        coder = errorCoderConfiguration.configure();
    }

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public String handleThrowable(Throwable throwable){
        return coder.handle(throwable);
    }

    @PreDestroy
    public void clear(){
       if(coder != null) coder.clear();
    }
}
