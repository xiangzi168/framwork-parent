package com.amg.fulfillment.cloud.logistics.dto.depository;

import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;


/**
 * @author Tom
 * @date 2021-04-29-18:39
 */
@Data
public class DepositorySaleOrderDto {

    @NotBlank(message = "仓库代码不能为null")
    @ApiModelProperty(name = "仓库代码")
    private String depositoryCode = Constant.DEPOSITORY_WB;
    @NotBlank(message = "销售id不能为null")
    @ApiModelProperty(name = "销售id")
    private String saleOrderId;
    @ApiModelProperty(name = "备注")
    private String remark;
    @Valid
    @NotNull(message = "商品详情不能为null")
    @Size(min = 1,message = "商品详情至少有一项")
    @ApiModelProperty(name = "商品详情")
    private List<ProductDto> productDtos;

}
