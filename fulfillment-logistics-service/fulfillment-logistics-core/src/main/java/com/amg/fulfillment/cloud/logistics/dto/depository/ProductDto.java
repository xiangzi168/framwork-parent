package com.amg.fulfillment.cloud.logistics.dto.depository;

import com.amg.fulfillment.cloud.logistics.api.dto.depository.ProductVariant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tom
 * @date 2021-04-12-16:55
 */
@ApiModel("产品信息")
@Data
public class ProductDto {
    @ApiModelProperty(name = "产品id")
    private String productId;
    @ApiModelProperty(name = "产品名称")
    private String productName;
    @NotBlank(message = "sku 不能为null")
    @ApiModelProperty(name = "sku")
    private String sku;
    @ApiModelProperty(name = "检测类型")
    private List<String> checkTypes;
    @NotNull(message = "预计收货总数量不能为null")
    @ApiModelProperty(name = "quantity 预计收货总数量")
    private Integer quantity;
    @ApiModelProperty(name = "销售订单号")
    private String salesOrderId;

    @ApiModelProperty(name = "产品id")
    private List<ProductVariant> variants;
    @ApiModelProperty(name = "图⽚地址 jpg 或者 png 格式图⽚地址不⽀持 WebP 等非常规格式")
    private List<String> imageUrls;
    @ApiModelProperty(name = "重量")
    private Double weightInKg;


}
