package com.amg.fulfillment.cloud.logistics.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * Created by Seraph on 2021/5/11
 */

@Data
@ApiModel("PurchasePackageProductResultVO")
public class PurchasePackageProductVO {

    @ApiModelProperty("采购包裹商品 id")
    private Long id;
    @ApiModelProperty("采购 id")
    private String purchaseId;
    @ApiModelProperty("sku")
    private String sku;
    @ApiModelProperty("商品名称")
    private String productName;
    @ApiModelProperty("商品属性")
    private String productAttribute;
    @ApiModelProperty("商品数量")
    private Integer productCount;
    @ApiModelProperty("商品重量")
    private String productWeight;
    @ApiModelProperty(value = "商品图片")
    private String productImg;
    @ApiModelProperty("商品状态")
    private String productStatus;
    @ApiModelProperty("异常采购 id")
    private String errorPurchaseId;
    @ApiModelProperty("异常类型")
    private String errorType;
    @ApiModelProperty("异常处理类型")
    private String errorHandle;
    @ApiModelProperty("异常信息")
    private String errorMessage;
    @ApiModelProperty("仓库图片")
    private String errorImg;
    @ApiModelProperty("退款状态")
    private String refundStatus;
    @ApiModelProperty(value = "spu")
    private String spu;
    @ApiModelProperty(value = "errorId,t_logistics_purchase_package_error_product表主键")
    private Long errorId;
    @ApiModelProperty("备注")
    private String remarks;
}
