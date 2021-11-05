package com.amg.fulfillment.cloud.logistics.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Seraph on 2021/5/24
 */

@Data
@ApiModel(value = "LogisticsLabelCategoryVO")
public class LogisticsLabelCategoryVO {

    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("标签类目 code")
    private String categoryCode;
    @ApiModelProperty("标签类目名称")
    private String categoryName;
    @ApiModelProperty("标签类目等级")
    private String categoryLevel;
}
