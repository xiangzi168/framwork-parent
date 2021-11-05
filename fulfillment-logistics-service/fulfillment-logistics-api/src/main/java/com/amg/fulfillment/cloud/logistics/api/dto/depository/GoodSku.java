package com.amg.fulfillment.cloud.logistics.api.dto.depository;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Tom
 * @date 2021-04-13-14:32
 */
@ApiModel(value = "SKU对象")
@Data
public class GoodSku {
    @ApiModelProperty(name = "仓库代码")
    private String depositoryCode;
    @ApiModelProperty(name = "sku")
    private String sku;
    @ApiModelProperty(name = "名称")
    private String name;
    @ApiModelProperty(name = "重量")
    private Double weightInKg;
    @ApiModelProperty(name = "图片路径")
    private List<String> ImageUrls;
    @ApiModelProperty(name = "产品规格")
    private List<ProductVariant> variants;
    @ApiModelProperty(name = "产品规格")
    private String createDate;

    private Object sourceData;
}
