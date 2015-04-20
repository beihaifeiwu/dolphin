package com.freetmp.errorcoder.render;

import com.freetmp.errorcoder.base.ErrorCode;

/**
 * Created by LiuPin on 2015/4/20.
 */
public class ToStringRender implements Render {

    @Override public String render(ErrorCode code, String charset) {
        return code.toString();
    }

}
