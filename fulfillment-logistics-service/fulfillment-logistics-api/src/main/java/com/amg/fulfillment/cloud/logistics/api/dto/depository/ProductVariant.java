package com.amg.fulfillment.cloud.logistics.api.dto.depository;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;

/**
 * @author Tom
 * @date 2021-04-13-15:59
 */
@ApiModel(value = "产品规格")
@Data
public class ProductVariant {
    @NonNull
    @ApiModelProperty(name = "尺⼨")
    private String name;
    @NonNull
    @ApiModelProperty(name = "值：如M")
    private String value;
}
