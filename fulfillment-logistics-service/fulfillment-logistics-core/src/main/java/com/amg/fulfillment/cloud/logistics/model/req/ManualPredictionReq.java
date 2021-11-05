package com.amg.fulfillment.cloud.logistics.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel(value = "ManualPredictionReq")
public class ManualPredictionReq {
    @NotBlank(message = "运单号 不能为null")
    @ApiModelProperty("运单号")
    private String expressBillNo;
    @NotBlank(message = "快递公司 不能为null")
    @ApiModelProperty("快递公司")
    private String expressCompanyName;
    @ApiModelProperty("操作人id")
    private Long operationId;
    @ApiModelProperty("操作人")
    private String operationer;
    @NotNull(message = "渠道类型不能为null")
    @ApiModelProperty(value = "渠道类型  1 1688 5 拼多多 6淘宝 ",required = true)
    private Integer packageSourceType;
    @Valid
    @NotNull(message = "预报详细 不能为null")
    @ApiModelProperty("预报详细")
    private List<PredictionDetail> predictionDetails;
    @Valid
    @ApiModelProperty("预报详细")
    private List<SkuAmount> skus;

    @Data
    public static class PredictionDetail {
        private Long id;
        @NotBlank(message = "渠道订单号 不能为null")
        @ApiModelProperty("渠道订单号")
        private String channelOrderId;
        @ApiModelProperty("销售订单")
        private String salesOrderId;
        @NotBlank(message = "采购id 不能为null")
        @ApiModelProperty("采购id")
        private String purchaseId;
        @ApiModelProperty("")
        private String itemId;
        @NotBlank(message = "sku 不能为null")
        @ApiModelProperty("sku")
        private String sku;
        @NotBlank(message = "商品名称 不能为null")
        @ApiModelProperty("商品名称")
        private String productName;
        @ApiModelProperty("商品图片")
        private String productImg;
        @ApiModelProperty("商品属性")
        private String productAttribute;
        private Integer status; // 用于标识已经预报过得purchaseId状态
        private Long purchasePackageId;
    }


    @Data
    public static class SkuAmount{
        @ApiModelProperty("sku名称 ")
        @NotBlank(message = "sku名称 不能为null")
        private String name;
        @ApiModelProperty("数量")
        @NotNull(message = "数量 不能为null")
        private Integer amount;
    }

}
