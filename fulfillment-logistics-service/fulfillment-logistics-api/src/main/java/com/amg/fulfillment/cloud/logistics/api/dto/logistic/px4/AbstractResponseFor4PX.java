package com.amg.fulfillment.cloud.logistics.api.dto.logistic.px4;

import lombok.Data;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

/**
 * @author Tom
 * @date 2021-04-20-16:06
 */
@Data
@ToString
public class AbstractResponseFor4PX<T extends AbstractResponseFor4PX> {


    private String result;
    private String msg;
    private List<Error> errors;

    public static String RESULT_SUCCESS = "1";
    public static String RESULT_FAIL = "0";
    public static String FAIL_CODE = "500";
    public static String FAIL_MES = "4px下单请求发生错误";

    public List<Error> getErrors() {
        if (errors == null) {
            errors=Collections.EMPTY_LIST;
        }
        return errors;
    }

    @Data
    @ToString
    public static class Error {
        private String errorCode;
        private String errorMsg;
    }

    public T fail500(String response) {
        this.setMsg(response);
        this.setResult(RESULT_FAIL);
        Error error = new Error();
        error.setErrorMsg(FAIL_MES);
        error.setErrorCode(FAIL_CODE);
        this.setErrors(Collections.singletonList(error));
        return (T) this;
    }
}
