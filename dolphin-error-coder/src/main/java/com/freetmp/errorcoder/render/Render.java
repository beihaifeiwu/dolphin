package com.freetmp.errorcoder.render;

import com.freetmp.errorcoder.base.ErrorCode;

/**
 * Created by LiuPin on 2015/4/20.
 */
public interface Render {
    String render(ErrorCode code, String charset);
}