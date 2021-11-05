package com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb;

import lombok.Data;

/**
 * Created by Seraph on 2021/5/27
 */

@Data
public class CancelDepositoryOrderResponseForWanB extends AbstractDepositoryResponseForWanB<CancelDepositoryOrderResponseForWanB> {

    private CancelDepositoryOrderResponseDataForWanB Data;

    @Data
    public static class CancelDepositoryOrderResponseDataForWanB
    {
        private String Status;
        private String RequestOn;
        private String ResponseOn;
        private String Message;
    }

}
