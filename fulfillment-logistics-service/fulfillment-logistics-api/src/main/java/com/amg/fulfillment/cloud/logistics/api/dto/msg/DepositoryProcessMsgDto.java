package com.amg.fulfillment.cloud.logistics.api.dto.msg;

import com.amg.fulfillment.cloud.logistics.api.dto.depository.Size;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * @author Tom
 * @date 2021-04-23-17:24
 */
@Data
public class DepositoryProcessMsgDto {

    @ApiModelProperty("出库单号")
    private String dispatchOrderId;
    @ApiModelProperty("已出库")
    private boolean isShipped;
    @ApiModelProperty("处理时间")
    private String processOn;
    @ApiModelProperty("出货重量")
    private String weightInKg;
    @ApiModelProperty("出货尺⼨")
    private Size size;
    @ApiModelProperty("包装材料")
    private String materialId;
    @ApiModelProperty("出库箱号")
    private String bagNo;
    @ApiModelProperty("产品信息")
    private List<DispatchOrderProcessItem> items;
    @ApiModelProperty("出货失败原因")
    private String failureReason;

    public Size getSize() {
        if (Objects.isNull(size)) {
            return new Size();
        }
        return size;
    }

    @Data
    public static class DispatchOrderProcessItem {
        @ApiModelProperty("SKU")
        private String sku;
        @ApiModelProperty("唯⼀库存标识")
        private String globalStockItemId;
        @ApiModelProperty("产品的唯⼀识别码")
        private String globalEquipmentId;
    }

}
