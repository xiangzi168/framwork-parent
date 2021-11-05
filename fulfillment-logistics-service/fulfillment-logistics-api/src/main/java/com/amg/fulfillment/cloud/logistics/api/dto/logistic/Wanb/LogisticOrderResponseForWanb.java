package com.amg.fulfillment.cloud.logistics.api.dto.logistic.Wanb;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.util.Objects;

/**
 * @author Tom
 * @date 2021-04-16-11:20
 */
@ApiModel(value = "万邦物流单返回响应对象")
@Data
@ToString(callSuper = true)
public class LogisticOrderResponseForWanb extends AbstractResponseWanb<LogisticOrderResponseForWanb> {

    private DetailData data;

    public DetailData getData() {
        if (Objects.isNull(data)) {
            data = new DetailData();
        }
        return data;
    }

    @Data
    public static class DetailData {
        private String ProcessCode;
        private String IndexNumber;
        private String ReferenceId;
        private String TrackingNumber;
        private Boolean IsVirtualTrackingNumber;
        private String IsRemoteArea;
        private String Status;
    }

}
