package com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

/**
 * @author Tom
 * @date 2021-04-09-12:09
 */
@ApiModel("万邦仓库响应对象")
@Data
public class DepositoryResponseMsgForWanB extends AbstractResponseMsg<DepositoryResponseMsgForWanB> {

    private DetailData Data;

    public DetailData getData() {
        if (Objects.isNull(Data)) {
            Data = new DetailData();
        }
        return Data;
    }

    @Data
    public static class DetailData {
        private String RefId;
        private String RefId1;
        private String RefId2;
        private String RefId3;
        private String AccountNo;
        private String Status;
        private String WarehouseCode;
        private String PurchaseReason;
        private String CreateOn;
        private String RequestOn;
        private String ResponseOn;
    }
}
