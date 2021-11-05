package com.amg.fulfillment.cloud.logistics.model.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by Seraph on 2021/5/31
 */

@Data
public class LogisticsRecommendChannelReq {

    @ApiModelProperty(value = "物流商 code")
    private String logisticsCode;
    @ApiModelProperty(value = "物流包裹单 id 集合")
    private List<Long> logisticsPackageIdList;
    @ApiModelProperty(value = "item Id 集合")
    private List<String> itemIdList;
}
