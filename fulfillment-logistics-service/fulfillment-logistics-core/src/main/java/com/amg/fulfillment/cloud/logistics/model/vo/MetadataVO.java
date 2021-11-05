package com.amg.fulfillment.cloud.logistics.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Seraph on 2021/5/26
 */

@Data
@ApiModel(value = "MetadataVO")
public class MetadataVO {

    @ApiModelProperty(value = "类目 code")
    private String categoryCode;
    @ApiModelProperty(value = "类目名称")
    private String categoryName;
    @ApiModelProperty(value = "类目等级")
    private String categoryLevel;
    @ApiModelProperty(value = "异常信息")
    private String errorMsg;
}
