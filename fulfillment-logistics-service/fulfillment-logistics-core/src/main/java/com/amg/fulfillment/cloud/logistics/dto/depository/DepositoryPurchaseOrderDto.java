package com.amg.fulfillment.cloud.logistics.dto.depository;

import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author Tom
 * @date 2021-04-12-16:35
 */
@ApiModel(value = "仓库采购订单")
@Data
public class DepositoryPurchaseOrderDto {
    @NotBlank(message = "订单id 不能为null")
    @ApiModelProperty(name = "订单id")
    private String orderId;
    @ApiModelProperty(name = "采购id")
    private String purchaseOrderId;
    @ApiModelProperty(name = "是否由外界传入采购订单，1：是 2：否 如果选择1 ，purchaseOrderId，否则还是由程序生产")
    private int producePurchaseOrderId;
    @ApiModelProperty(name = "第三方仓库具体仓库号 万邦：SZ")
    private String warehouseCode = Constant.DEPOSITORY_WAREHOUSECODE;
    @ApiModelProperty(name = "仓库id")
    private String depositoryId;
    @ApiModelProperty(name = "在本系统的仓库码 WB:万邦")
    private String depositoryCode = Constant.DEPOSITORY_WB;
    @ApiModelProperty(name = "仓库名称")
    private String depositoryName;
    @ApiModelProperty(name = "采购方法")
    private String purchaseWay = Constant.PURCHASE_WAY_SALE;
    @ApiModelProperty(name = "预计采购总数")
    private int expectTotal;
    @ApiModelProperty(name = "实际采购总数")
    private int purchaseRealTotal;
    @ApiModelProperty(name = "采购订单状态:1：已下单:2：采购中:3：完成")
    private int purchaseStatus;
    @Valid
    @NotNull(message = "products 采购产品信息不能为null")
    @Size(min = 1,message = "products 采购产品信息至少有一项")
    @ApiModelProperty(name = "采购产品信息")
    private List<ProductDto> products;
    @Valid
    @NotNull(message = "expresses 采购订单物流信息不能为null")
    @Size(min = 1,message = "expresses 采购订单物流信息至少有一项")
    @ApiModelProperty(name = "采购订单物流信息")
    private List<ExpressDto> expresses;
    @ApiModelProperty(name = "采购备注")
    private String remark;
}
