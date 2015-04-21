package com.freetmp.errorcoder.render;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freetmp.common.util.Assert;
import com.freetmp.errorcoder.base.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by LiuPin on 2015/4/21.
 */
public class JacksonRender implements Render {

    private static final Logger log = LoggerFactory.getLogger(JacksonRender.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Override public String render(ErrorCode code, String charset) {
        Assert.notNull(code);
        try {
            return objectMapper.writeValueAsString(code);
        } catch (JsonProcessingException e) {
            log.error("Cannot translate error code object to json", e);
        }

        String error = "{code:-1,message:\"Something going wrong with the jackson\"}";
        try {
            error = objectMapper.writeValueAsString(ErrorCode.ERROR_RENDERRING);
        } catch (JsonProcessingException e) {
            log.error("Cannot translate error code object to json", e);
        }

        return error;
    }
}
