package com.amg.fulfillment.cloud.logistics.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Seraph on 2021/5/24
 */

@Data
@ApiModel(value = "LogisticsLabelReq")
public class LogisticsLabelReq extends BaseReq{

    @ApiModelProperty(value = "标签名称")
    private String name;
}
