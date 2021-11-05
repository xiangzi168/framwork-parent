package com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

/**
 * @author Tom
 * @date 2021-04-08-19:53
 */
@ApiModel(value = "产品信息")
@Data
@Builder
public class PurchaseOrderProductContentEditRequest {
    @ApiModelProperty(name = "采购订单产品及质检要求")
    private List<PurchaseOrderProductEditRequest> Products;
    @ApiModelProperty(name = "采购订单产品及销售订单信息，临时采购单必填，备货采购单留空")
    private List<PurchaseOrderItemEditRequest> Items;


    @ApiModel(value = "采购订单产品及质检要求")
    @Data
    @Builder
    public static class PurchaseOrderProductEditRequest {
//        @NonNull
        @ApiModelProperty(name = "sku")
        private String SKU;
        @ApiModelProperty(name = "质检要求:Quality:基础质检，颜⾊，尺⼨，无破损等;Photo:要求拍照;" +
                "GlobalTradeItemNo:要求扫描商品条码;GlobalEquipmentId:要求录入商品唯⼀条码，如⼿机的 IME")
        private List<String> CheckTypes;
//        @NonNull
        @ApiModelProperty(name = "预计收货总数量，临时采购单留空或者填任意数字，系统会忽略传入值，并⾃动根据Items统计此数据，备货采购单必须填写正整数")
        private Integer Quantity;
    }


    @ApiModel(value = "产品及销售订单信息，临时采购单必填，备货采购单留空")
    @Data
    @Builder
    public static class PurchaseOrderItemEditRequest {
        @ApiModelProperty(name = "Item Id")
        private String Id;
        @ApiModelProperty(name = "SKU")
        private String SKU;
        @ApiModelProperty(name = "销售订单号")
        private String SalesOrderId;
    }

}
