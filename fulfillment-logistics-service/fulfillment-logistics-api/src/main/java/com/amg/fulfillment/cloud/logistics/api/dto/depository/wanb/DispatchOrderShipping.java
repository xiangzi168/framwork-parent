package com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * @author Tom
 * @date 2021-04-15-10:00
 */
@ApiModel(value = "出库订单物流信息")
@Data
@Builder
public class DispatchOrderShipping {
//    @NonNull
    @ApiModelProperty(name = "物流渠道 Id")
    private String ServiceId;
//    @NonNull
    @ApiModelProperty(name = "跟踪号")
    private String TrackingNumber;
//    @NonNull
    @ApiModelProperty(name = "包裹⾯单地址")
    private String LabelUrl;
    @ApiModelProperty(name = "分区码")
    private String Zone;
}
