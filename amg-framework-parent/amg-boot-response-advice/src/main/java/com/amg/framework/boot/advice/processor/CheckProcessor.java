package com.amg.framework.boot.advice.processor;



import com.amg.framework.boot.base.enums.ResponseCodeEnum;
import com.amg.framework.boot.base.exception.GlobalException;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class CheckProcessor {

    /**
     * 检查是否系统级异常
     *
     * @param object
     */
    public static void checkException(Object object, HttpServletResponse response) {
        int status = response.getStatus();
        response.setStatus(HttpServletResponse.SC_OK);
        if (status == HttpServletResponse.SC_NOT_FOUND) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100404);
        } else if (status == HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, (String) ((Map) object).get("message"));
        } else if (status == HttpServletResponse.SC_UNAUTHORIZED) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100401, (String) ((Map) object).get("message"));
        }
        // ... 其他状态码在这里扩展
    }

}
