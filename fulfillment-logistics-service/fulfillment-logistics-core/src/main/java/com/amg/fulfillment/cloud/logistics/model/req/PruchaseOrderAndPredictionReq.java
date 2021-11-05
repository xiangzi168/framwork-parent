package com.amg.fulfillment.cloud.logistics.model.req;

import com.amg.fulfillment.cloud.logistics.api.enumeration.PurchasePackagePredictionTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class PruchaseOrderAndPredictionReq {

    @ApiModelProperty(value = "渠道订单号",required = true)
    @NotEmpty
    private String channelOrderId;
    @ApiModelProperty("是否预报 0 未预报 1 已预报 ")
    private Integer prediction = PurchasePackagePredictionTypeEnum.YES.getType();
    @ApiModelProperty("物理公司")
    private String expressCompanyName = "测试物流公司";
    @ApiModelProperty("物理code")
    private String expressCompanyCode = "TEST";
    @ApiModelProperty("运单号")
    private String expressBillNo;
    @NotNull
    @Size(min=1)
    @ApiModelProperty(value = "详情",required = true)
    private List<Detail> list;

    @Data
    public static class Detail {

        @ApiModelProperty(value = "itemId",required = true)
        @NotEmpty
        private String itemId;
        @ApiModelProperty(value = "purchaseId",required = true)
        @NotEmpty
        private String purchaseId;
        @ApiModelProperty(value = "销售订单号",required = true)
        @NotEmpty
        private String salesOrderId;
        @ApiModelProperty(value = "sku",required = true)
        @NotEmpty
        private String sku;
        @ApiModelProperty("商品名称")
        private String productName;
        @ApiModelProperty("商品图片")
        private String productImg;
        @ApiModelProperty("商品属性")
        private String productAttribute;
    }

}
