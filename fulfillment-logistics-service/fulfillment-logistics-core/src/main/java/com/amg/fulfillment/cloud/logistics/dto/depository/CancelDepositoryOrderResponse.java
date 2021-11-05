package com.amg.fulfillment.cloud.logistics.dto.depository;

import lombok.Data;

/**
 * Created by Seraph on 2021/5/27
 */

@Data
public class CancelDepositoryOrderResponse extends AbstractDepositoryResponse {

    private String status;
    private String requestOn;
    private String responseOn;
}
