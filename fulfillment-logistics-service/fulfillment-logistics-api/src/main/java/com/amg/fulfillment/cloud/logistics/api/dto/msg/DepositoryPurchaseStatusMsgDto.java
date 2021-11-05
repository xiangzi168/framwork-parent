package com.amg.fulfillment.cloud.logistics.api.dto.msg;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author Tom
 * @date 2021-05-22-15:31
 */
@Data
public class DepositoryPurchaseStatusMsgDto {
    // itemId
    private String itemId;
    // 采购id
    private String purchaseId;
    // 销售id
    private String salesOrderId;
    // 采购品状态：10待入库 20待处理 21已处理  22已入库  30已制单 31制单失败 40发货中 41已发货 42取消发货 43 发货失败 44 已收货")
    private Integer status;
    // 通知时间
    private Date noticeTime;
    // 重量（g）
    private Double weight;
}
