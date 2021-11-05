package com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb;

import com.sun.org.apache.bcel.internal.generic.NEW;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

/**
 * @author Tom
 * @date 2021-04-13-15:50
 */
@ApiModel
@Getter
@Setter
@ToString
public abstract class AbstractResponseMsg<T extends AbstractResponseMsg> {

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
