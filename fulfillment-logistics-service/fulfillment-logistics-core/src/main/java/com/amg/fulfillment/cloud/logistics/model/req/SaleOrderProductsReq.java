package com.amg.fulfillment.cloud.logistics.model.req;

import com.amg.fulfillment.cloud.logistics.api.dto.depository.ProductVariant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tom
 * @date 2021-05-14-15:55
 */

@Data
@ApiModel("销售订单商品详情")
public class SaleOrderProductsReq {
    @ApiModelProperty(name = "产品id")
    private String productId;
    @ApiModelProperty(name = "产品名称")
    private String productName;
    @NotBlank(message = "sku 不能为null")
    @ApiModelProperty(name = "sku")
    private String sku;
    @NotBlank(message = "spu 不能为null")
    @ApiModelProperty(name = "spu")
    private String spu;
    @NotBlank(message = "itemId 不能为null")
    @ApiModelProperty(name = "itemId")
    private String itemId;
    @NotBlank(message = "purchaseId 不能为null")
    @ApiModelProperty(name = "purchaseId")
    private String purchaseId;
    @ApiModelProperty(name = "检测类型")
    private List<String> checkTypes;
    @NotNull(message = "预计收货总数量不能为null")
    @ApiModelProperty(name = "quantity 预计收货总数量")
    private Integer quantity;
    @ApiModelProperty(name = "销售订单号")
    private String salesOrderId;

    @ApiModelProperty(name = "产品id")
    private List<ProductVariant> variants = new ArrayList<>();
    @ApiModelProperty(name = "图⽚地址 jpg 或者 png 格式图⽚地址不⽀持 WebP 等非常规格式")
    private List<String> imageUrls;
    @ApiModelProperty(name = "重量（g）")
    private BigDecimal weight;
    @ApiModelProperty(name = "skuChannel  1:临采1688、淘宝 2：AE 3:备货")
    private Integer skuChannel;
    @ApiModelProperty(name = "订单来源  HPIOS、HPANDROID、HPWEB....")
    private String orderFrom;
}
