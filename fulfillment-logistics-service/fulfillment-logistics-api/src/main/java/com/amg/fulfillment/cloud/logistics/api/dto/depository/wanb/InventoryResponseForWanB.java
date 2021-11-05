package com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb;

import io.swagger.annotations.ApiModel;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tom
 * @date 2021-04-13-15:03
 */
@ApiModel(value = "万邦仓库库存返回对象")
@Data
public class InventoryResponseForWanB extends AbstractResponseMsg<InventoryResponseForWanB> {

    private List<InventoryItem> Data = new ArrayList<>(8);

    @Data
    public static class InventoryItem {
        private String SKU;
        private String WarehouseCode;
        private String Type;
        private int TotalQuantity;
        private int FrozenQuantity;
        private int LockQuantity;
        private int AvailableQuantity;
        private String LastModifyOn;
    }

}
