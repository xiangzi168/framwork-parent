package com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb;

import com.amg.fulfillment.cloud.logistics.api.dto.depository.Address;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Tom
 * @date 2021-04-09-15:43
 */
@ApiModel(value = "推送出库订单对象")
@Data
@Builder
public class OutDepositoryOrderForWanB {

    @ApiModelProperty(name = "销售订单号")
    private String SalesOrderId;
    @NonNull
    @ApiModelProperty(name = "我司仓库代码")
    private String WarehouseCode;
    @NonNull
    @ApiModelProperty(name = "收件⼈地址信息")
    private Address Address;
    @NonNull
    @ApiModelProperty(name = "出库包裹物流信息")
    private DispatchOrderShipping Shipping;
    @NonNull
    @ApiModelProperty(name = "采购订单产品及质检要求")
    private List<DispatchOrderItem> Items;
    @NonNull
    @ApiModelProperty(name = "预计重量")
    private BigDecimal EstimatedWeightInKg;
    @ApiModelProperty(name = "备注")
    private String Notes;


//    @ApiModel(value = "出库订单物流信息")
//    @Data
//    @Builder
//    public static class DispatchOrderShipping {
//        @NonNull
//        @ApiModelProperty(name = "物流渠道 Id")
//        private String ServiceId;
//        @NonNull
//        @ApiModelProperty(name = "跟踪号")
//        private String TrackingNumber;
//        @NonNull
//        @ApiModelProperty(name = "包裹⾯单地址")
//        private String LabelUrl;
//        @ApiModelProperty(name = "分区码")
//        private String Zone;
//
//    }

    @ApiModel(value = "出库订单产品信息")
    @Data
    @Builder
    public static class DispatchOrderItem {
        @NonNull
        @ApiModelProperty(name = "SKU")
        private String SKU;
        @NonNull
        @ApiModelProperty(name = "数量")
        private Integer Quantity;
    }

}
