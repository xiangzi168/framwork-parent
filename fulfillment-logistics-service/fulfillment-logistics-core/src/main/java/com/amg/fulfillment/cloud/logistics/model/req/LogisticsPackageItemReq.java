package com.amg.fulfillment.cloud.logistics.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("包裹详情")
public class LogisticsPackageItemReq {

    @ApiModelProperty(value = "sku")
    private String sku;
    @ApiModelProperty(value = "itemId")
    private String itemId;
    @ApiModelProperty(value = "采购id")
    private String purchaseId;
    @ApiModelProperty(value = "采购渠道  1.1688  2.AE  3.备货，添加到包裹采购渠道必填")
    private Integer channel;
}
