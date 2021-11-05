package com.amg.fulfillment.cloud.logistics.api.dto.logistic.Wanb;

import com.alibaba.fastjson.annotation.JSONField;
import com.amg.fulfillment.cloud.logistics.api.dto.logistic.px4.AbstractResponseFor4PX;
import lombok.Data;

/**
 * Created by Seraph on 2021/5/21
 */

@Data
public class AbstractResponseWanb <T extends AbstractResponseWanb> {

    @JSONField(name = "Succeeded")
    private Boolean succeeded;
    @JSONField(name = "Error")
    private Error error;
    @JSONField(name = "SystemError")
    private SystemError systemError;

    @Data
    public static class Error
    {
        @JSONField(name = "Succeeded")
        private String Code;
        @JSONField(name = "Succeeded")
        private String Message;
    }

    @Data
    public static class SystemError
    {
        @JSONField(name = "Message")
        private String essage;
        @JSONField(name = "ExceptionMessage")
        private String exceptionMessage;
        @JSONField(name = "ExceptionType")
        private String exceptionType;
        @JSONField(name = "StackTrace")
        private String stackTrace;
        @JSONField(name = "innerException")
        private InnerException innerException;
    }

    @Data
    public static class InnerException
    {
        @JSONField(name = "Message")
        private String message;
        @JSONField(name = "ExceptionMessage")
        private String exceptionMessage;
        @JSONField(name = "ExceptionType")
        private String exceptionType;
        @JSONField(name = "StackTrace")
        private String stackTrace;
        @JSONField(name = "InnerException")
        private String innerException;
    }
}
