package com.amg.fulfillment.cloud.logistics.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * Created by Seraph on 2021/5/27
 */

@Data
@ApiModel(value = "DeliveryPackageCannelVO")
public class DeliveryPackageCannelVO {

    private Long id;
    private String status;
    private String errorMsg;
}
