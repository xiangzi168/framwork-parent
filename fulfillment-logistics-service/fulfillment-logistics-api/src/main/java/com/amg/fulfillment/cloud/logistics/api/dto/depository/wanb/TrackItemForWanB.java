package com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb;

import com.amg.fulfillment.cloud.logistics.api.dto.depository.ProductVariant;
import io.swagger.annotations.ApiModelProperty;
import lombok.NonNull;

import java.util.List;

/**
 * @author Tom
 * @date 2021-04-09-16:07
 */
public class TrackItemForWanB {
    @NonNull
    @ApiModelProperty(name = "名称")
    private String Name;
    @ApiModelProperty(name = "产品规格")
    private List<ProductVariant> Variants;
    @ApiModelProperty(name = "图⽚地址")
    private List<String> ImageUrls;
    @NonNull
    @ApiModelProperty(name = "重量(kg)")
    private Double WeightInKg;

}
