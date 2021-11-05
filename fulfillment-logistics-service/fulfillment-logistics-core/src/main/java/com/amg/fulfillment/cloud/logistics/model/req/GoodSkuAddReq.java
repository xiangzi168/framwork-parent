package com.amg.fulfillment.cloud.logistics.model.req;

import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.api.dto.depository.ProductVariant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;
@Data
public class GoodSkuAddReq {
    @ApiModelProperty(name = "仓库代码")
    private String depositoryCode = Constant.DEPOSITORY_WB;
    @NotBlank(message = "sku不能为null")
    @ApiModelProperty(name = "sku")
    private String sku;
    @NotBlank(message = "名称不能为null")
    @ApiModelProperty(name = "名称")
    private String name;
    @ApiModelProperty(name = "重量")
    private Double weight;
    @ApiModelProperty(name = "图片路径")
    private List<String> ImageUrls;
    @ApiModelProperty(name = "产品规格")
    private List<ProductVariant> variants;
}
