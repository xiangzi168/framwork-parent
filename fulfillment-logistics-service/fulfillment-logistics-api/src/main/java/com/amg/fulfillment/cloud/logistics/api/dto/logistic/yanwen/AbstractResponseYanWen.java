package com.amg.fulfillment.cloud.logistics.api.dto.logistic.yanwen;


import lombok.Data;

import java.io.InputStream;
import java.util.Objects;

/**
 * Created by Seraph on 2021/5/21
 */
@Data
public class AbstractResponseYanWen <T extends AbstractResponseYanWen> {

    private Boolean CallSuccess;
    private Response Response;

    public Response getResponse() {
        if (Objects.isNull(Response)) {
            Response = new Response();
        }
        return Response;
    }


    @Data
    public static class Response {
        private String Userid;
        private String Operation;
        private boolean Success;
        private String Reason;
        private String ReasonMessage;
        private String Epcode;
        private InputStream inputStream;
    }

    public static <T> T fail500(String message) {
        Response response = new Response();
        response.setSuccess(Boolean.FALSE);
        response.setReasonMessage(message);
        response.setReason("系统自定义错误");
        AbstractResponseYanWen abstractResponseYanWen = new AbstractResponseYanWen();
        abstractResponseYanWen.setCallSuccess(Boolean.FALSE);
        abstractResponseYanWen.setResponse(response);
        return (T)abstractResponseYanWen;
    }
}
