package com.amg.fulfillment.cloud.logistics.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by Seraph on 2021/5/12
 */

@Data
@ApiModel("PurchasePackageErrorHandleReq")
public class PurchasePackageErrorHandleReq {

    @NotNull(message = "采购包裹商品 id 不能为空")
    @ApiModelProperty("采购包裹商品 id")
    private Long id;
    @NotNull(message = "异常处理状态 不能为空")
    @ApiModelProperty("异常处理   1异常待处理  2 已重新采购  3已直接入库   4已作废处理")
    private Integer errorHandle;
    @NotNull(message = "渠道类型 不能为空")
    @ApiModelProperty("渠道类型   1 1688  5 PDD  6 TAOBAO ")
    private Integer channelType;
}
