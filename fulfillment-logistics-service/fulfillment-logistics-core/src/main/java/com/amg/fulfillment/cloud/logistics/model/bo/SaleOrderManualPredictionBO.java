package com.amg.fulfillment.cloud.logistics.model.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SaleOrderManualPredictionBO {

    @ApiModelProperty(value = "t_logistics_purchase_package_error_product表id")
    private Long errorId;

    @ApiModelProperty(value = "销售订单号")
    private String saleOrder;

    @ApiModelProperty(value = "item_id")
    private String itemId;

    @ApiModelProperty(value = "采购id")
    private String purchaseId;

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "销售订单状态")
    private Integer saleStatus;

    @ApiModelProperty(value = "预报仓库被通知状态")
    private Integer depositoryStatus;

    @ApiModelProperty(value = "推导出的状态")
    private Integer logicStatus;

   @ApiModelProperty(value = "c错误类型")
    private String errorType;
    @ApiModelProperty(value = "推导出的排序")
   private Integer logicLevel;

}
