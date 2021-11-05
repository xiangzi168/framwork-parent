package com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb;

import lombok.Data;

import java.util.Objects;

/**
 * Created by Seraph on 2021/5/27
 */

@Data
public class AbstractDepositoryResponseForWanB<T extends AbstractDepositoryResponseForWanB>{

    private boolean Succeeded;
    private ErrorMessage Error;
    private String SystemError;

    protected static final String ERROR_CODE_GENERAL = "500";

    public ErrorMessage getError() {
        if (Objects.isNull(Error)) {
            Error = new ErrorMessage();
        }
        return Error;
    }

    @Data
    public static class ErrorMessage {
        private String Code;
        private String Message;
    }

    public T fail500(String responseMessage) {
        this.setSucceeded(false);
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setMessage(responseMessage);
        errorMessage.setCode(ERROR_CODE_GENERAL);
        this.setError(errorMessage);
        this.setSystemError(responseMessage);
        return (T) this;
    }
}
