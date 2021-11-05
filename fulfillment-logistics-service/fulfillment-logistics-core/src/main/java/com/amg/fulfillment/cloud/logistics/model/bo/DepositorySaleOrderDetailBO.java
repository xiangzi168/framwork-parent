package com.amg.fulfillment.cloud.logistics.model.bo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class DepositorySaleOrderDetailBO {

    private Long id;
    // 销售id
    private Long saleId;
    // 销售订单号
    private String saleOrder;
    // item_id
    private String itemId;
    // 采购id
    private String purchaseId;
    // purchase_package表主键
    private Long purchasePackageId;
    //入库状态：10 未入库 22 已入库
    private Integer receiveState;
    // 状态(仓库状态)  0 初始状态   30已制单  31制单失败     40发货中    41已发货 42取消发货 43发货失败    45推送失败
    private Integer status;
    // 状态描述
    private String statusStr;
    // 创建时间
    private Date createTime;
    // 到仓时间
    private String receiveTime;
    // 重量
    private BigDecimal weight;
    //渠道订单 id
    private String channelOrderId;
    //物流状态  0 初始状态  10已创建物流单   20 已出库   30  已收货   50 已取消  99 创建物流单异常
    private Integer logisticsStatus;
    //物流状态描述
    private String logisticsStatusStr;
    //仓库状态  0 初始状态   30已制单  31制单失败     40发货中    41已发货 42取消发货 43发货失败    45推送失败
    private Integer depositoryStatus;
    //仓库状态描述
    private String depositoryStatusStr;
    // 签收时间 (用户签收时间)
    private Date receivingGoodTime;
    // 出库时间 (仓库发出时间）
    private Date deliveryTime;
    //物流渠道 包裹类型  1 境外发货单  2 AE 发货单
    private Integer type;
    //揽收时间
    private Date acceptTime;
    // 物流屬性针对出库
    private List<LogisticsProperty> logisticsProperties;
    // 物流属性对采购
    private List<LogisticsPropertyForPurchase> logisticsPropertyForPurchases;

    @Data
    public static class LogisticsProperty{
        // 物流Code
        private String logisticsCode;
        // 物流名称
        private String logisticsName;
        // 物流订单号
        private String logisticsOrderNo;
        // 物流节点
        private String logisticsNode;
        // 运单号
        private String logisticsWayBillNo;
        // 运单号有效标识 是否有效 0 无效 1 有效
        private String valid;
        // 跟踪号
        private String logisticsTrackingCode;
        // 是否揽收  0：未揽收 1：已揽收
        private Integer logisticsReceived;
    }

    @Data
    public static class LogisticsPropertyForPurchase{
        // 系统记录的包裹单号
        private String packageNo;
        // 物流Code
        private String expressCompanyCode;
        // 物流名称
        private String expressCompanyName;
        // 物流编号
        private String expressCode;
        // 运单号
        private String expressBillNo;
        // 渠道订单号
        private String channelOrderId;
        // 发货时间 (1688发货时间)
        private Date consignTime;
        // 签收时间 (1688物流的签收时间)
        private Date signTime;
        // 入库时间 (仓库 入库时间)
        private Date receivingGoodTime;
        // 揽收时间(1688揽收时间)
        private Date acceptTime;
    }
}

