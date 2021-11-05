package com.amg.fulfillment.cloud.logistics.model.req;

import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Tom
 * @date 2021-04-14-18:25
 */
@ApiModel("万邦仓库查询对象")
@Data
public class InventorySearchReq {
    @NotBlank(message = "sku不能为null")
    private String sku;
    private String warehouseCode = Constant.DEPOSITORY_WAREHOUSECODE;
    private String depositoryCode = Constant.DEPOSITORY_WB;
    private String type;
}
