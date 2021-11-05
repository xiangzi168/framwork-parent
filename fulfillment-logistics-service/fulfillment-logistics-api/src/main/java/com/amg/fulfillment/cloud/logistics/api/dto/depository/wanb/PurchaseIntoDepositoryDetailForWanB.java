package com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PurchaseIntoDepositoryDetailForWanB extends AbstractDepositoryResponseForWanB<PurchaseIntoDepositoryDetailForWanB> {

    private PurchaseIntoDepositoryDetail data;

    @Data
    public static class PurchaseIntoDepositoryDetail{
        @ApiModelProperty("采购原因")
        @JsonProperty(value = "PurchaseReason")
        private String purchaseReason;
        @ApiModelProperty("销售订单号")
        @JsonProperty(value = "RefId")
        private String refId;
        @ApiModelProperty("⾃定义参考单号")
        @JsonProperty(value = "RefId1")
        private String refId1;
        @ApiModelProperty("⾃定义参考单号")
        @JsonProperty(value = "RefId2")
        private String refId2;
        @ApiModelProperty("⾃定义参考单号")
        @JsonProperty(value = "RefId3")
        private String refId3;
        @ApiModelProperty("客户代码")
        @JsonProperty(value = "AccountNo")
        private String accountNo;
        @ApiModelProperty("客户代码 Created: 已创建 Arrived: 已到仓 PartialReceived: 部分收货  Received: 已收货(完成)")
        @JsonProperty(value = "Status")
        private String status;
        @ApiModelProperty("仓库代码")
        @JsonProperty(value = "WarehouseCode")
        private String warehouseCode;
        @ApiModelProperty("产品及质检要求")
        @JsonProperty(value = "Products")
        private List<PurchaseOrderProduct> products;
        @ApiModelProperty("临时采购单产品明细信息--仅临时采购单有此数据")
        @JsonProperty(value = "Items")
        private List<PurchaseOrderItem> items;
        @ApiModelProperty("备货采购单产品明细信息---仅备货采购单收货后有此 数据")
        @JsonProperty(value = "ReplenishItems")
        private List<PurchaseOrderReplenishItem> replenishItems;
        @ApiModelProperty("物流单号与转运信息")
        @JsonProperty(value = "Expresses")
        private List<PurchaseOrderExpressInfo> expresses;
        @ApiModelProperty("创建时间 ")
        @JsonProperty(value = "CreateOn")
        private String createOn;
        @ApiModelProperty("(⾸个快递)到仓时间 ")
        @JsonProperty(value = "ArriveOn")
        private String arriveOn;
        @ApiModelProperty("收货时间 ")
        @JsonProperty(value = "ReceiveOn")
        private String receiveOn;
        @ApiModelProperty("有无异常")
        @JsonProperty(value = "HasIssue")
        private Boolean hasIssue;
        @ApiModelProperty("备注")
        @JsonProperty(value = "Notes")
        private String notes;
    }

    @Data
    public static class PurchaseOrderProduct {
        @ApiModelProperty("SKU")
        @JsonProperty(value = "SKU")
        private String sku;
        @ApiModelProperty("收货重量")
        @JsonProperty(value = "ActualWeightInKg")
        private String actualWeightInKg;
        @ApiModelProperty("收货尺⼨")
        @JsonProperty(value = "ActualSize")
        private Size actualSize;
        @ApiModelProperty("质检要求")
        @JsonProperty(value = "CheckTypes")
        private List<String> checkTypes;
        @ApiModelProperty("应到数量")
        @JsonProperty(value = "ExpectedQuantity")
        private Integer expectedQuantity;
        @ApiModelProperty("实到数量")
        @JsonProperty(value = "ReceivedQuantity")
        private Integer receivedQuantity;
        @ApiModelProperty("实到数量-无异常数")
        @JsonProperty(value = "OkQuantity")
        private Integer okQuantity;
        @ApiModelProperty("实到数量-异常件数")
        @JsonProperty(value = "NotOkQuantity")
        private Integer notOkQuantity;
    }

    @Data
    public static class PurchaseOrderItem {
        @ApiModelProperty("")
        @JsonProperty(value = "Id")
        private String id;
        @ApiModelProperty("SKU")
        @JsonProperty(value = "SKU")
        private String sku;
        @ApiModelProperty("")
        @JsonProperty(value = "SalesOrderId")
        private String salesOrderId;
        @ApiModelProperty("有无异常")
        @JsonProperty(value = "HasIssue")
        private Boolean hasIssue;
        @ApiModelProperty("异常类型")
        @JsonProperty(value = "IssueType")
        private String issueType;
        @ApiModelProperty("收货批次号")
        @JsonProperty(value = "ReceiveBatchNo")
        private String receiveBatchNo;

    }

    @Data
    public static class PurchaseOrderReplenishItem {
        @ApiModelProperty("SKU")
        @JsonProperty(value = "SKU")
        private String sku;
        @ApiModelProperty("")
        @JsonProperty(value = "Quantity")
        private Double quantity;
        @ApiModelProperty("")
        @JsonProperty(value = "HasIssue")
        private Boolean HasIssue;
        @ApiModelProperty("")
        @JsonProperty(value = "IssueType")
        private String issueType;
        @ApiModelProperty("")
        @JsonProperty(value = "BatchNo")
        private String batchNo;

    }

    @Data
    public static class PurchaseOrderExpressInfo {
        @ApiModelProperty("物流单号信息")
        @JsonProperty(value = "Express")
        private PurchaseOrderExpress express;
        @ApiModelProperty("物流转运信息")
        @JsonProperty(value = "ExpressTransit")
        private PurchaseOrderExpressTransit expressTransit;
    }

    @Data
    public static class PurchaseOrderExpress {
        @ApiModelProperty("物流快递单号")
        @JsonProperty(value = "ExpressId")
        private String expressId;
        @ApiModelProperty("快递公司")
        @JsonProperty(value = "ExpressVendorId")
        private String expressVendorId;
    }

    @Data
    public static class PurchaseOrderExpressTransit {
        @ApiModelProperty("始发省份")
        @JsonProperty(value = "ShipFromProvince")
        private String shipfromprovince;
        @ApiModelProperty("预计到仓时间")
        @JsonProperty(value = "ExpectedArriveOn")
        private String expectedarriveon;
        @ApiModelProperty("实际到仓时间")
        @JsonProperty(value = "ActualArriveOn")
        private String actualarriveon;

    }


    @Data
    public static class Size {
        @ApiModelProperty("")
        @JsonProperty(value = "Length")
        private Double length;
        @ApiModelProperty("")
        @JsonProperty(value = "Width")
        private Double width;
        @ApiModelProperty("")
        @JsonProperty(value = "Height")
        private Double height;
    }


}
