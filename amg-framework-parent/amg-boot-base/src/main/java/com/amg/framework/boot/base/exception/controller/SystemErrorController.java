package com.amg.framework.boot.base.exception.controller;

import com.amg.framework.boot.base.enums.ResponseCodeEnum;
import com.amg.framework.boot.base.model.Result;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
public class SystemErrorController implements ErrorController {

    @RequestMapping(value = "/error")
    public Object error(HttpServletRequest request, HttpServletResponse response){
        int statusCode = response.getStatus();
        response.setStatus(HttpStatus.OK.value()); // 200返回码
        if (statusCode == 404) {
            return new Result(ResponseCodeEnum.RETURN_CODE_100404);
        }
        if (statusCode == 403) {
            return new Result(ResponseCodeEnum.RETURN_CODE_100403);
        }
        if (statusCode == 401) {
            return new Result(ResponseCodeEnum.RETURN_CODE_100401);
        }
        return new Result(ResponseCodeEnum.RETURN_CODE_100500);
    }


    @Override
    public String getErrorPath() {
        return "/error";
    }

}
