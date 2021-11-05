package com.amg.fulfillment.cloud.logistics.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Seraph on 2021/5/24
 */

@Data
@ApiModel("ProductDetailVO")
public class ProductDetailVO {

    private String sku;
    @ApiModelProperty(value = "商品名称")
    private String productName;
    @ApiModelProperty(value = "商品图片")
    private String productImg;
    @ApiModelProperty(value = "商品属性")
    private String productAttribute;
}
