package com.amg.fulfillment.cloud.logistics.dto.depository;

import cn.hutool.core.util.StrUtil;
import com.amg.fulfillment.cloud.logistics.api.exception.ExceptionConstant;
import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Tom
 * @date 2021-04-13-10:16
 */
@ApiModel(value = "出库订单")
@Data
public class OutDepositoryOrderDto {

    @NotBlank(message = "订单id不能是null")
    @ApiModelProperty(name = "订单id")
    private String orderId;
    @ApiModelProperty(name = "销售id")
    private String salesOrderId;
    @ApiModelProperty(name = "发货订单id")
    private String dispatchOrderId;
    @ApiModelProperty(name = "发货订单id是否由外界传入：1：是 2 否；如果选择1 ，dispatchOrderId必填，否则还是由程序生产")
    private int produceDispatchOrder = Constant.NO;
    @Valid
    @NotNull(message = "出库订单子项详情不能为null")
    @Size(min = 1,message = "出库订单子项详情至少有一项")
    @ApiModelProperty(name = "出库订单子项详情")
    private List<OutDepositoryOrderItemDto> orderItems;
    @ApiModelProperty(name = "第三方仓库代码 万邦:SZ")
    private String warehouseCode = Constant.DEPOSITORY_WAREHOUSECODE;
    @ApiModelProperty(name = "在本系统的仓库码 WB:万邦")
    private String depositoryCode = Constant.DEPOSITORY_WB;
    @Valid
    @NotNull(message = "地址不能为null")
    @ApiModelProperty(name = "地址")
    private AddressDto address;
    @Valid
    @NotNull(message = "出库包裹物流信息不能为null")
    @ApiModelProperty(name = "出库包裹物流信息")
    private ExpressDto express;
    @ApiModelProperty(name = "备注")
    private String remark;
    @NotNull(message = "预估重量不能为null")
    @ApiModelProperty(name = "预估重量")
    private BigDecimal estimatedWeight;
    @ApiModelProperty(name = "实际重量")
    private Double realWeight;

}
