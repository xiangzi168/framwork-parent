package com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Tom
 * @date 2021-04-23-18:13
 */
@Data
public class PurchaseIntoDepositoryMsg {
    @ApiModelProperty("回传动作  ReceiveFinish  回传数据并且收货完结  Receive 仅回传当前批次数据，采购单未未完结  Finish 收货完成")
    @JsonProperty(value = "FeedbackAction")
    private String feedbackAction;
    @ApiModelProperty("采购原因")
    @JsonProperty(value = "PurchaseReason")
    private String purchaseReason;
    @ApiModelProperty("收货批次号")
    @JsonProperty(value = "BatchNo")
    private String batchNo;
    @ApiModelProperty("快递单号")
    @JsonProperty(value = "ExpressId")
    private String expressId;
    @ApiModelProperty("入仓时间 ")
    @JsonProperty(value = "ReceiveOn")
    private String receiveOn;
    @ApiModelProperty("快递包裹图⽚地址")
    @JsonProperty(value = "ExpressImageUrls")
    private List<String> expressImageUrls;
    @ApiModelProperty("临时采购单产品明细信息")
    @JsonProperty(value = "Items")
    private List<PurchaseOrderReceiveItemFeedback> items;
    @ApiModelProperty("备货采购单产品明细信息")
    @JsonProperty(value = "ReplenishItems")
    private List<PurchaseOrderReplenishItemFeedback> replenishItems;

    @Data
    public static class PurchaseOrderReceiveItemFeedback {
        @ApiModelProperty("")
        @JsonProperty(value = "Id")
        private String id;
        @ApiModelProperty("SKU")
        @JsonProperty(value = "SKU")
        private String sku;
        @ApiModelProperty("")
        @JsonProperty(value = "SalesOrderId")
        private String salesOrderId;
        @ApiModelProperty("")
        @JsonProperty(value = "WeightInKg")
        private Double weightInKg;
        @ApiModelProperty("")
        @JsonProperty(value = "Size")
        private Size size;
        @ApiModelProperty("")
        @JsonProperty(value = "Characters")
        private String characters;
        @ApiModelProperty("")
        @JsonProperty(value = "ImageUrls")
        private List<String> imageUrls;
        @ApiModelProperty("")
        @JsonProperty(value = "IsReceived")
        private boolean isReceived;
        @ApiModelProperty("")
        @JsonProperty(value = "IssueType")
        private String issueType;
        @ApiModelProperty("")
        @JsonProperty(value = "IssueNote")
        private String issueNote;

    }

    @Data
    public static class PurchaseOrderReplenishItemFeedback {
        @ApiModelProperty("SKU")
        @JsonProperty(value = "SKU")
        private String sku;
        @ApiModelProperty("")
        @JsonProperty(value = "WeightInKg")
        private Double weightInKg;
        @ApiModelProperty("")
        @JsonProperty(value = "Size")
        private Size size;
        @ApiModelProperty("")
        @JsonProperty(value = "Characters")
        private String characters;
        @ApiModelProperty("")
        @JsonProperty(value = "Quantity")
        private String quantity;
        @ApiModelProperty("")
        @JsonProperty(value = "ImageUrls")
        private List<String> imageUrls;
        @ApiModelProperty("")
        @JsonProperty(value = "IsReceived")
        private String isReceived;
        @ApiModelProperty("")
        @JsonProperty(value = "IssueType")
        private String issueType;
        @ApiModelProperty("")
        @JsonProperty(value = "IssueNote")
        private String issueNote;
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
