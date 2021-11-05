package com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Tom
 * @date 2021-04-08-19:50
 */
@ApiModel(value = "万邦推送备货采购订单接⼝")
@Data
@Builder
public class PurchaseOrderForWanB {

    @ApiModelProperty(name = "默认为临时采购：SalesOrderFulfillment，备货采购：StockReplenishment")
    private String PurchaseReason;
    @NotNull
    @ApiModelProperty(name = "仓库代码 ")
    private String WarehouseCode;
    @ApiModelProperty(name = "备注")
    private String Notes;
    @ApiModelProperty(name = "⾃定义参考单号 1")
    private String RefId1;
    @ApiModelProperty(name = "⾃定义参考单号 2")
    private String RefId2;
    @ApiModelProperty(name = "⾃定义参考单号 3")
    private String RefId3;
    @NotNull
    @ApiModelProperty(name = "产品信息")
    private PurchaseOrderProductContentEditRequest ProductContent;
    @ApiModelProperty(name = "采购订单物流信息")
    private List<PurchaseOrderExpressEditRequest> Expresses;

}