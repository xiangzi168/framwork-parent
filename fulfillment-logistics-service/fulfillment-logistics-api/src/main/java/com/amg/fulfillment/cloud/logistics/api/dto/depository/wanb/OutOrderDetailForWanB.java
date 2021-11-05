package com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Tom
 * @date 2021-04-15-9:49
 */
@ApiModel("出库订单详情")
@Data
public class OutOrderDetailForWanB {
    @ApiModelProperty(name = "出库订单号")
    private String RefId;
    @ApiModelProperty(name = "客户代码")
    private String AccountNo;
    @ApiModelProperty(name = "销售订单号")
    private String SalesOrderId;
    @ApiModelProperty(name = "仓库代码")
    private String WarehouseCode;
    @ApiModelProperty(name = "收件⼈地址")
    private com.amg.fulfillment.cloud.logistics.api.dto.depository.Address Address;
    @ApiModelProperty(name = "状态:Created: 已创建 Packed: 已打包Shipped: 已出库Cancelled: 已取消Failed: 出货失败")
    private String Status;
    @ApiModelProperty(name = "创建时间")
    private String CreateOn;
    @ApiModelProperty(name = "处理时间")
    private String PackOn;
    @ApiModelProperty(name = "物流信息")
    private DispatchOrderShippingResponse Shipping;
    @ApiModelProperty(name = "产品统计")
    private List<DispatchOrderProduct> Products;
    @ApiModelProperty(name = "产品明细")
    private List<DispatchOrderPackItem> items;
    @ApiModelProperty(name = "包裹打包信息")
    private DispatchOrderPacking Packing;
    @ApiModelProperty(name = "包裹出库信息")
    private DispatchOrderShipOutInfo ShipOutInfo;
    @ApiModelProperty(name = "包裹出货失败原因")
    private String PackFailReason;
    @ApiModelProperty(name = "备注")
    private String Notes;

    @Data
    public static class DispatchOrderShippingResponse {
        @ApiModelProperty(name = "物流渠道 Id")
        private String ServiceId;
        @ApiModelProperty(name = "跟踪号")
        private String TrackingNumber;
        @ApiModelProperty(name = "包裹⾯单地址")
        private String LabelUrl;
        @ApiModelProperty(name = "分区码")
        private String Zone;
    }

    @Data
    public static class DispatchOrderProduct {
        @ApiModelProperty(name = "SKU")
        private String SKU;
        @ApiModelProperty(name = "应出数量")
        private String Quantity;
        @ApiModelProperty(name = "打包数量")
        private String PackQuantity;
    }

    @Data
    public static class DispatchOrderPackItem {
        @ApiModelProperty(name = "SKU")
        private String SKU;
        @ApiModelProperty(name = "库存流⽔号")
        private String GlobalStockItemId;
        @ApiModelProperty(name = "设备号")
        private String GlobalEquipmentId;
    }

    @Data
    public static class DispatchOrderPacking {
        @ApiModelProperty(name = "重量")
        private Double WeightInKg;
        @ApiModelProperty(name = "尺⼨")
        private String Size;
        @ApiModelProperty(name = "包装材料")
        private String MaterialId;
    }

    @Data
    public static class DispatchOrderShipOutInfo {
        @ApiModelProperty(name = "出库时间")
        private Double ShipOn;
        @ApiModelProperty(name = "出库箱号/袋号")
        private String ContainerId;
    }




}
