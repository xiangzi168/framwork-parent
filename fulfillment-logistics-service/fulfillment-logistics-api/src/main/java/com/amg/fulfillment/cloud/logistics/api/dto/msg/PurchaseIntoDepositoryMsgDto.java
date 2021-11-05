package com.amg.fulfillment.cloud.logistics.api.dto.msg;

import com.amg.fulfillment.cloud.logistics.api.dto.depository.Size;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author Tom
 * @date 2021-05-17-19:55
 */
@Data
@ApiModel("采购订单到仓库通知")
public class PurchaseIntoDepositoryMsgDto {
    @ApiModelProperty("采购订单号")
    private String purchaseOrderId;
    @ApiModelProperty("回传动作  ReceiveFinish  回传数据并且收货完结  Receive 仅回传当前批次数据，采购单未未完结  Finish 收货完成")
    private String feedbackAction;
    @ApiModelProperty("采购原因")
    private String purchaseReason;
    @ApiModelProperty("收货批次号")
    private String batchNo;
    @ApiModelProperty("快递单号")
    private String expressId;
    @ApiModelProperty("入仓时间 ")
    private String receiveOn;
    @ApiModelProperty("快递包裹图⽚地址")
    private List<String> expressImageUrls;
    @ApiModelProperty("临时采购单产品明细信息")
    private List<PurchaseOrderReceiveItemFeedback> items;
    @ApiModelProperty("备货采购单产品明细信息")
    private List<PurchaseOrderReplenishItemFeedback> replenishItems;

    @Data
    public static class PurchaseOrderReceiveItemFeedback {
        @ApiModelProperty("")
        private String id;
        @ApiModelProperty("SKU")
        private String sku;
        @ApiModelProperty("")
        private String salesOrderId;
        @ApiModelProperty("")
        private Double weightInKg;
        @ApiModelProperty("")
        private Size size;
        @ApiModelProperty("")
        private String characters;
        @ApiModelProperty("")
        private List<String> imageUrls;
        @ApiModelProperty("")
        private boolean received;
        @ApiModelProperty("异常类型：None 无异常 Quantity 未收到货 Variant 货不对版，如颜⾊，尺⼨不对等  Quality 质量问题，破损  Handle 对异常已处理（非仓库状态）")
        private String issueType;
        @ApiModelProperty("")
        private String issueNote;
        @ApiModelProperty("count  用于记录仓库回传数据的个数，本系统统计存放")
        private int count;
    }

    @Data
    public static class PurchaseOrderReplenishItemFeedback {
        @ApiModelProperty("SKU")
        private String sku;
        @ApiModelProperty("")
        private Double weightInKg;
        @ApiModelProperty("")
        private Size size;
        @ApiModelProperty("")
        private String characters;
        @ApiModelProperty("")
        private String quantity;
        @ApiModelProperty("")
        private List<String> imageUrls;
        @ApiModelProperty("")
        private boolean received;
        @ApiModelProperty("")
        private String issueType;
        @ApiModelProperty("")
        private String issueNote;

    }

}
