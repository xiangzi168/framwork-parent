package com.amg.fulfillment.cloud.logistics.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by Seraph on 2021/5/13
 */

@Data
@ApiModel("LogisticsProductVO")
public class LogisticsProductVO {

    @ApiModelProperty("商品 sku")
    private String sku;
    @ApiModelProperty("商品名称")
    private String productName;
    @ApiModelProperty("商品图片")
    private String productImg;
    @ApiModelProperty("商品属性")
    private String productAttribute;
    @ApiModelProperty("商品数量")
    private Integer productCount;
    @ApiModelProperty("商品重量")
    private String productWeight;
    @ApiModelProperty("商品状态")
    private String productStatus;

    @ApiModelProperty(value = "item id")
    private String itemId;
    @ApiModelProperty(value = "采购 id")
    private String purchaseId;
}
