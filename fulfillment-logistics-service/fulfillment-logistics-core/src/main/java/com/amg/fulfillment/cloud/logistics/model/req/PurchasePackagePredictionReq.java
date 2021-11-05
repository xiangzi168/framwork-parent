package com.amg.fulfillment.cloud.logistics.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by Seraph on 2021/5/12
 */

@Data
@ApiModel("PurchasePackagePredictionReq")
public class PurchasePackagePredictionReq {

    @ApiModelProperty(value = "采购包裹 id")
    private List<Long> idList;
}
