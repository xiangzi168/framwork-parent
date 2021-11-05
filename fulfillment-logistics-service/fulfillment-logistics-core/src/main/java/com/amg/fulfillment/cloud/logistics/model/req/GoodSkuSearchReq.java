package com.amg.fulfillment.cloud.logistics.model.req;

import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.api.dto.depository.ProductVariant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class GoodSkuSearchReq {
    @ApiModelProperty(name = "仓库代码")
    private String depositoryCode = Constant.DEPOSITORY_WB;
    @NotBlank(message = "sku不能为null")
    @ApiModelProperty(name = "sku")
    private String sku;
}
