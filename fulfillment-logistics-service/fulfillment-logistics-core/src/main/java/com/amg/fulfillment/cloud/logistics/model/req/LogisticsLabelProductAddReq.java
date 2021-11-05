package com.amg.fulfillment.cloud.logistics.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Seraph on 2021/5/24
 */

@Data
@ApiModel(value = "LogisticsLabelProductAddReq")
public class LogisticsLabelProductAddReq {

    @ApiModelProperty("商品sku")
    private String sku;
}
