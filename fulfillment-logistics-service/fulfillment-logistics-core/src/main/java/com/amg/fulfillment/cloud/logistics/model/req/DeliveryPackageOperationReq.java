package com.amg.fulfillment.cloud.logistics.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by Seraph on 2021/5/27
 */

@Data
@ApiModel(value = "DeliveryPackageOperationReq")
public class DeliveryPackageOperationReq {

    @ApiModelProperty("运单号")
    private String logisticsWayBillNo;
    @ApiModelProperty("运单号集合")
    private List<String> logisticsWayBillNos;
    @ApiModelProperty("操作备注")
    private String operationRemark;
    @ApiModelProperty("id集合")
    private List<Long> idList;
}
