package com.amg.fulfillment.cloud.logistics.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by Seraph on 2021/5/26
 */

@Data
@ApiModel(value = "MetadataReq")
public class MetadataReq {

    @ApiModelProperty(value = "类目 code 集合")
    private List<String> categoryCodeList;
}
