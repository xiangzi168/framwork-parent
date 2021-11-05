package com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb;

import com.amg.fulfillment.cloud.logistics.api.dto.depository.Address;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DepositoryProcessDetailForWanB extends AbstractDepositoryResponseForWanB<DepositoryProcessDetailForWanB> {

    private OutOrderDetail data;

    @Data
    public static class OutOrderDetail {
        @ApiModelProperty("出库订单号")
        @JsonProperty(value = "RefId")
        private String refId;
        @ApiModelProperty("客户代码")
        @JsonProperty(value = "AccountNo")
        private String accountNo;
        @ApiModelProperty("销售订单号")
        @JsonProperty(value = "SalesOrderId")
        private String salesOrderId;
        @ApiModelProperty("仓库代码")
        @JsonProperty(value = "WarehouseCode")
        private String warehouseCode;
        @ApiModelProperty("收件⼈地址")
        @JsonProperty(value = "Address")
        private Address address;
        @ApiModelProperty("状态 Created: 已创建 Packed: 已打包 Shipped: 已出库 Cancelled: 已取消 Failed: 出货失败")
        @JsonProperty(value = "Status")
        private String status;
        @ApiModelProperty("创建时间")
        @JsonProperty(value = "CreateOn")
        private String createOn;
        @ApiModelProperty("处理时间")
        @JsonProperty(value = "PackOn")
        private String packOn;
        @ApiModelProperty("物流信息")
        @JsonProperty(value = "Shipping")
        private String shipping;
        @ApiModelProperty("产品统计")
        @JsonProperty(value = "Products")
        private List<DispatchOrderProduct> products;
        @ApiModelProperty("产品明细")
        @JsonProperty(value = "Items")
        private List<DispatchOrderPackItem> items;
        @ApiModelProperty("包裹打包信息")
        @JsonProperty(value = "Packing")
        private DispatchOrderPacking packing;
        @ApiModelProperty("包裹出库信息")
        @JsonProperty(value = "ShipOutInfo")
        private DispatchOrderShipOutInfo shipOutInfo;
        @ApiModelProperty("包裹出货失败原因")
        @JsonProperty(value = "PackFailReason")
        private String packFailReason;
        @ApiModelProperty("备注")
        @JsonProperty(value = "Notes")
        private String notes;
    }

    @Data
    public static class DispatchOrderPackItem {
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

    @Data
    public static class DispatchOrderProduct {
        @ApiModelProperty("SKU")
        @JsonProperty(value = "SKU")
        private String sku;
        @ApiModelProperty("应出数量")
        @JsonProperty(value = "Quantity")
        private Integer quantity;
        @ApiModelProperty("打包数量")
        @JsonProperty(value = "PackQuantity")
        private Integer packQuantity;
    }

    @Data
    public static class DispatchOrderPacking {
        @ApiModelProperty("重量")
        @JsonProperty(value = "WeightInKg")
        private String weightInKg;
        @ApiModelProperty("尺⼨")
        @JsonProperty(value = "Size")
        private Size size;
        @ApiModelProperty("包装材料")
        @JsonProperty(value = "MaterialId")
        private String materialId;
    }

    @Data
    public static class DispatchOrderShipOutInfo {
        @ApiModelProperty("出库时间")
        @JsonProperty(value = "ShipOn")
        private String shipOn;
        @ApiModelProperty("出库箱号/袋号")
        @JsonProperty(value = "ContainerId")
        private String containerId;
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
