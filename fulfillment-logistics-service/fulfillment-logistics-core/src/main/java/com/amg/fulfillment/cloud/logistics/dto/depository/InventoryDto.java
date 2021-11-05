package com.amg.fulfillment.cloud.logistics.dto.depository;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;

/**
 * @author Tom
 * @date 2021-04-14-18:04
 */
@ApiModel("库存实体类")
@Data
@Builder
public class InventoryDto {
    private String depositoryCode;
    private String sku;
    private String warehouseCode;
    private String type;
    private int totalQuantity;
    private int frozenQuantity;
    private int lockQuantity;
    private int availableQuantity;
    private String lastModifyDate;
}
