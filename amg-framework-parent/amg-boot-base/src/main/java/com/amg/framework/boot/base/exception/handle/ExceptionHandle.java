package com.amg.framework.boot.base.exception.handle;

import com.amg.framework.boot.base.enums.ResponseCodeEnum;
import com.amg.framework.boot.base.model.Result;
import com.amg.framework.boot.base.exception.GlobalException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;


/**
 * 全局异常捕获
 */
@RestControllerAdvice
public class ExceptionHandle {

    private final Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);

    @Autowired
    private HttpServletRequest request;


    /**
     * GlobalException
     */
    @ExceptionHandler(value = GlobalException.class)
    public Object globalException(GlobalException exception) {
        logger.error("全局异常 请求地址: {}", request.getRequestURI(), exception);
        return new Result(exception.getErrorCode(), getExceptionMessage(exception));
    }


    /**
     * NullPointerException
     */
    @ExceptionHandler(value = NullPointerException.class)
    public Object nullPointerException(NullPointerException exception) {
        logger.error("空指针异常 请求地址: {}", request.getRequestURI(), exception);
        return new Result(ResponseCodeEnum.RETURN_CODE_100500, "空指针异常");
    }


    @ExceptionHandler(value = BindException.class)
    public Object bindException(BindException exception) {
        logger.error("系统异常 请求地址: {}", request.getRequestURI(), exception);
        return new Result(ResponseCodeEnum.RETURN_CODE_100400, exception.getBindingResult().getFieldError().getDefaultMessage());
    }


    @ExceptionHandler(value = ConstraintViolationException.class)
    public Object bindException(ConstraintViolationException exception) {
        logger.error("系统异常 请求地址: {}", request.getRequestURI(), exception);
        return new Result(ResponseCodeEnum.RETURN_CODE_100400, exception.getMessage().split(": ")[1]);
    }


    /**
     * SQLException
     */
    @ExceptionHandler(value = SQLException.class)
    public Object sqlException(SQLException exception) {
        logger.error("数据库操作异常 请求地址: {}", request.getRequestURI(), exception);
        return new Result(ResponseCodeEnum.RETURN_CODE_100500, "数据库操作异常");
    }


    /**
     * AccessDeniedException
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    public Object accessDeniedException(AccessDeniedException exception) {
        logger.error("权限异常 请求地址: {}", request.getRequestURI(), exception);
        return new Result(ResponseCodeEnum.RETURN_CODE_100401);
    }


    /**
     * other Exception
     */
    @ExceptionHandler(value = Exception.class)
    public Object exception(Exception exception) {
        logger.error("系统异常 请求地址: {}", request.getRequestURI(), exception);
        return new Result(ResponseCodeEnum.RETURN_CODE_100500, getExceptionMessage(exception));
    }


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Object methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        logger.error("系统异常 请求地址: {}", request.getRequestURI(), exception);
        return new Result(ResponseCodeEnum.RETURN_CODE_100400, exception.getBindingResult().getFieldError().getDefaultMessage());
    }


    /**
     * 获取异常信息
     * @param throwable
     * @return
     */
    public static String getExceptionMessage(Throwable throwable) {
        return throwable == null ? null : throwable.getCause() != null ? (throwable.getCause().getMessage() != null
                ? throwable.getCause().getMessage() : throwable.getCause().toString()) : (throwable.getMessage() != null ? throwable.getMessage() : throwable.toString());
    }


    /**
     * 获取异常日志信息
     * @param e
     * @return
     */
    public static String getLogErrorMessage(Exception e) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        StackTraceElement stackTraceElement = null;
        if (stackTrace != null && stackTrace.length >= 1) {
            stackTraceElement = stackTrace[0];
        }
        return ExceptionUtils.getRootCauseMessage(e) + " :: " + stackTraceElement;
    }

}
