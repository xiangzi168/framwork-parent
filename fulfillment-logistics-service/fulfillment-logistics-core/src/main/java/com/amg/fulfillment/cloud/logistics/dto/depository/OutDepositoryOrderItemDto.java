package com.amg.fulfillment.cloud.logistics.dto.depository;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Tom
 * @date 2021-04-13-17:32
 */
@ApiModel(value = "出库订单详细子项")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutDepositoryOrderItemDto {
    @ApiModelProperty(name = "sku码")
    @NotBlank(message = "出库订单详细-sku不能为null")
    private String sku;

    @ApiModelProperty(name = "数量")
    @NotNull(message = "出库订单详细-数量不能为null")
    private Integer quantity;
}
