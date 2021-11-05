package com.amg.fulfillment.cloud.logistics.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Created by Seraph on 2021/5/31
 */

@Data
@ApiModel(value = "DeliveryPackageDeliveredCreateReq")
public class DeliveryPackageDeliveredCreateReq {

    @NotBlank(message = "物流商 code 不能为null")
    @ApiModelProperty(value = "物流商 code 不能为null")
    private String logisticsCode;
    @NotBlank(message = "渠道 code 不能为null")
    @ApiModelProperty(value = "渠道 code 不能为null")
    private String channelCode;
    @NotBlank(message = "销售订单 id 不能为null")
    @ApiModelProperty(value = "销售订单 id 不能为null")
    private String salesOrderId;
    @Valid
    @NotEmpty(message = "item id List 不能为null")
    @Size(min = 1,message = "List至少有一项")
    @ApiModelProperty(value = "item id List")
    private List<ItemAndBillNoReq> itemIdList;

    @Data
    public static class ItemAndBillNoReq{
        @NotBlank(message = "itemId 不能为null")
        @ApiModelProperty(value = "itemId")
        private String itemId;
        @NotBlank(message = "运单号 不能为null")
        @ApiModelProperty(value = "运单号")
        private String logisticsWayBillNo;
    }
}
