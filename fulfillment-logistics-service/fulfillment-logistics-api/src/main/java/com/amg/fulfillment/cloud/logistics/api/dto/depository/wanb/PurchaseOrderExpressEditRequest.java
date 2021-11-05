package com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.Date;

/**
 * @author Tom
 * @date 2021-04-09-10:54
 */
@ApiModel(value = "采购订单物流信息")
@Data
@Builder
public class PurchaseOrderExpressEditRequest {
    @NonNull
    @ApiModelProperty(name = "快递单号")
    private String ExpressId;
    @ApiModelProperty(name = "快递公司标识")
    private String ExpressVendorId;
    @ApiModelProperty(name = "预计到仓⽇期")
    private Date ExpectedArriveOn;
    @ApiModelProperty(name = "发货省份")
    private String ShipFromProvince;
}
