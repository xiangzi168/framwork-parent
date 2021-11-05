package com.amg.fulfillment.cloud.logistics.api.dto.logistic.Wanb;

import com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb.AbstractResponseMsg;
import lombok.Data;

import java.io.InputStream;
import java.util.Objects;

/**
 * Created by Seraph on 2021/4/29
 */

@Data
public class LogisticLabelResponseForWanb extends AbstractResponseWanb<LogisticOrderResponseForWanb> {

    private DetailData data;


    @Data
    public static class DetailData {
        private InputStream inputStream;
    }
}
