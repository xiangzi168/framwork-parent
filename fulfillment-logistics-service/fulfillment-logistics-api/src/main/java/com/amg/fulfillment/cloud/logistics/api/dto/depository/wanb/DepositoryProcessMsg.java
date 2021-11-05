package com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Tom
 * @date 2021-04-23-17:24
 */
@Data
public class DepositoryProcessMsg {

    @ApiModelProperty("已出库")
    @JsonProperty(value = "IsShipped")
    private boolean isShipped;
    @ApiModelProperty("处理时间")
    @JsonProperty(value = "ProcessOn")
    private String processOn;
    @ApiModelProperty("出货重量")
    @JsonProperty(value = "WeightInKg")
    private String weightInKg;
    @ApiModelProperty("出货尺⼨")
    @JsonProperty(value = "Size")
    private String size;
    @ApiModelProperty("包装材料")
    @JsonProperty(value = "MaterialId")
    private String materialId;
    @ApiModelProperty("出库箱号")
    @JsonProperty(value = "BagNo")
    private String bagNo;
    @ApiModelProperty("产品信息")
    @JsonProperty(value = "Items")
    private List<DispatchOrderProcessItem> items;
    @ApiModelProperty("出货失败原因")
    @JsonProperty(value = "FailureReason")
    private String failureReason;

    @Data
    public static class DispatchOrderProcessItem {
        @ApiModelProperty("SKU")
        @JsonProperty(value = "SKU")
        private String sku;
        @ApiModelProperty("唯⼀库存标识")
        @JsonProperty(value = "GlobalStockItemId")
        private String globalStockItemId;
        @ApiModelProperty("产品的唯⼀识别码")
        @JsonProperty(value = "GlobalEquipmentId")
        private String globalEquipmentId;
    }

}
