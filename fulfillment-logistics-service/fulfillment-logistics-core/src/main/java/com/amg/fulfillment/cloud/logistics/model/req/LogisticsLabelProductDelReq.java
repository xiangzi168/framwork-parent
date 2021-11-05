package com.amg.fulfillment.cloud.logistics.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Created by Seraph on 2021/5/24
 */

@Data
@ApiModel(value = "LogisticsLabelProductDelReq")
public class LogisticsLabelProductDelReq {


    @NotNull(message = "物流标签商品 id 不能为空")
    @ApiModelProperty(value = "物流标签商品 id")
    private Long id;
}
