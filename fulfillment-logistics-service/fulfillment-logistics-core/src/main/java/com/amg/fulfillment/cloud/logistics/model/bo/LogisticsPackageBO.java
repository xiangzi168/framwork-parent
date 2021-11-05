package com.amg.fulfillment.cloud.logistics.model.bo;

import com.google.common.base.Objects;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class LogisticsPackageBO{
    private Long id;
    private Integer type;       //包裹类型  1 境外发货单  2 AE 发货单
    private Integer purchaseChannel;       //采购渠道  1.1688    2. AE     3.备货
    private String salesOrderId;     //关联销售订单
    private Integer deliveryStatus;       //出库状态     0 初始状态   30已制单  31制单失败     40发货中    41已发货 42取消发货 43发货失败    45推送失败
    private Integer logisticsStatus;       //物流状态  0 初始状态  10已创建物流单   20 已出库   30  已收货   50 已取消  99 创建物流单异常
    private String logisticsName;       //物流名称
    private String logisticsCode;       //物流编号
    private String logisticsChannel;       //物流渠道
    private Date deliveryTime;        //出库时间（仓库发出时间）
    private Date receivingGoodTime;     //收货时间(客户签收时间)
    private Integer isValid;       //是否有效   0 否  1 是
    private String logisticsNode;     //物流最新节点
    private String logisticsWayBillNo;     //物流运单号
    private String logisticsTrackingCode;     //物流追踪号
    private Integer logisticsReceived;     //是否揽收  0：未揽收 1：已揽收
    private Date acceptTime;     //揽收时间
    private String logisticsOrderNo;     //出库系统订单号
    private List<LogisticsPackageItemBO> items = new ArrayList<>();

    @Data
    public static class LogisticsPackageItemBO {
        private Long id;
        private Long packageId;
        private String sku;
        private String itemId;
        private String purchaseId;
        private Date createTime;        //出库时间
        private Integer status;
        private String statusStr;
        private Integer deliveryStatus;       //出库状态     0 初始状态   30已制单  31制单失败     40发货中    41已发货 42取消发货 43发货失败    45推送失败
        private Integer deliveryStatusStr;       //出库状态     0 初始状态   30已制单  31制单失败     40发货中    41已发货 42取消发货 43发货失败    45推送失败
        private Integer logisticsStatus;       //物流状态  0 初始状态  10已创建物流单   20 已出库   30  已收货   50 已取消  99 创建物流单异常
        private Integer logisticsStatusStr;       //物流状态  0 初始状态  10已创建物流单   20 已出库   30  已收货   50 已取消  99 创建物流单异常
        private String logisticsName;       //物流名称
        private String logisticsCode;       //物流编号
        private String logisticsChannel;       //物流渠道
        private Date deliveryTime;        //出库时间（仓库发出时间）
        private Date receivingGoodTime;     //收货时间(客户签收时间)
        private String logisticsNode;     //物流最新节点
        private String logisticsWayBillNo;     //物流运单号
        private Integer type;       //包裹类型  1 境外发货单  2 AE 发货单
        private Integer purchaseChannel;       //采购渠道  1.1688    2. AE     3.备货
        private String salesOrderId;     //关联销售订单
        private Integer isValid; // 是否有效 0 无效 1 有效
        private String logisticsTrackingCode;     //物流追踪号
        private Integer logisticsReceived;     //是否揽收  0：未揽收 1：已揽收
        private Date acceptTime;     //揽收时间
        private String logisticsOrderNo;     //出库系统订单号


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LogisticsPackageItemBO that = (LogisticsPackageItemBO) o;
            return Objects.equal(itemId, that.itemId);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(itemId);
        }
    }
}
