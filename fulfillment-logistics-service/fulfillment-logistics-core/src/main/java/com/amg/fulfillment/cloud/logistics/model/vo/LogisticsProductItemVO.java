package com.amg.fulfillment.cloud.logistics.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Seraph on 2021/6/8
 */

@Data
@ApiModel("LogisticsProductItemVO")
public class LogisticsProductItemVO {

    @ApiModelProperty(value = "item id")
    private String itemId;
    @ApiModelProperty(value = "采购 id")
    private String purchaseId;
}
