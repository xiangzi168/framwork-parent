package com.amg.fulfillment.cloud.logistics.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * Created by Seraph on 2021/5/24
 */

@Data
@ApiModel(value = "LogisticsLabelDelReq")
public class LogisticsLabelDelReq {

    @Size(min = 1)
    @ApiModelProperty("需要删除的物流标签 id")
    private List<Long> idList;
}
