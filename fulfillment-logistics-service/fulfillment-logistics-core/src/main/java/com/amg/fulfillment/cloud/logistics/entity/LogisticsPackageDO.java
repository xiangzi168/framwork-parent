package com.amg.fulfillment.cloud.logistics.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Seraph on 2021/5/11
 */

@Data
@TableName("t_logistics_package")
public class LogisticsPackageDO implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId
    private Long id;
    private Integer type;       //包裹类型  1 境外发货单  2 AE 发货单 4 CJ单
    @TableField(value = "purchase_channel")
    private Integer purchaseChannel;       //采购渠道  1.1688    2. AE     3.备货 4.CJ单
    @TableField(value = "sales_order_id")
    private String salesOrderId;     //关联销售订单
    @TableField(value = "channel_order_id")
    private String channelOrderId;     //关联渠道订单
    @TableField(value = "delivery_package_type")
    private String deliveryPackageType;        //发货包裹类型 包裹类型 DOC:文件 SPX:包裹
    @TableField(value = "delivery_status")
    private Integer deliveryStatus;       //出库状态     0 初始状态 30已制单 31制单失败 40发货中 41已发货 42取消发货 43发货失败 45推送失败 -1异常
    @TableField(value = "logistics_name")
    private String logisticsName;       //物流名称
    @TableField(value = "logistics_code")
    private String logisticsCode;       //物流编号
    @TableField(value = "logistics_channel")
    private String logisticsChannel;       //物流渠道
    @TableField(value = "logistics_order_no")
    private String logisticsOrderNo;     //物流订单号
    @TableField(value = "logistics_tracking_code")
    private String logisticsTrackingCode;       //物流跟踪单号
    @TableField(value = "logistics_channel_tracking_code")
    private String logisticsChannelTrackingCode;
    @TableField(value = "logistics_process_code")
    private String logisticsProcessCode;        //物流包裹处理号
    @TableField(value = "logistics_idnex_number")
    private String logisticsIdnexNumber;        //物流包裹检索号
    private String logisticsLabelUrl;        //物流面单地址
    @TableField(value = "logistics_way_bill_no")
    private String logisticsWayBillNo;        //物流运单号
    @TableField(value = "logistics_status")
    private Integer logisticsStatus;     //物流状态      DeliveryPackageLogisticsStatusEnum
    @TableField(value = "logistics_channel_status")
    private String logisticsChannelStatus;     //物流渠道状态
    @TableField(value = "logistics_node")
    private String logisticsNode;     //物流最新节点(内部节点)
    private String logisticsNodeEn; // 物流最新节点(原始node)
    @TableField(value = "logistics_area")
    private String logisticsArea;     //物流收货国家
    @TableField(value = "logistics_cost")
    private BigDecimal logisticsCost;       //物流费用
    @TableField(value = "logistics_cost_currency")
    private String logisticsCostCurrency;       //物流费用币种
    @TableField(value = "logistics_weight")
    private BigDecimal logisticsWeight;        //物流单重量
    @TableField(value = "logistics_remark")
    private String logisticsRemark;        //物流单备注
    private Integer battery;        //是否带电  是否带电池 1：是 2：否
    @TableField(value = "battery_type")
    private Integer batteryType;        //电池类型  1:不带电  2:带电 3:纯电池
    @TableField(value = "package_info")
    private String packageInfo;     //包裹信息
    @TableField(value = "error_info")
    private String errorInfo;       //错误信息
    @TableField(value = "cancel_remark")
    private String cancelRemark;        //取消发货备注
    @TableField(value = "delivery_time")
    private Date deliveryTime;        //出库时间
    @TableField(value = "receiving_good_time")
    private Date receivingGoodTime;     //收货(签收)时间
    @TableField(value = "is_valid")
    private Integer isValid;       //是否有效   0 否  1 是
    @TableLogic
    @TableField(value = "is_deleted")
    private Integer isDeleted;      //是否删除   0 否   1 是
    @TableField(value = "create_time")
    private Date createTime;        //创建时间
    @TableField(value = "create_by")
    private String createBy;       //创建人
    @TableField(value = "update_time")
    private Date updateTime;        //最后修改时间
    @TableField(value = "update_by")
    private String updateBy;       //最后修改人
    private Double packageLength;       //最后修改人
    private Double packageWidth;       //最后修改人
    private Double packageHeight;       //最后修改人
    private String packageUnit;       //最后修改人
    @TableField(exist = false)
    private String channelCode;
    @TableField(exist = false)
    private String channelName;
    @TableField(value = "logistics_received")
    private Boolean logisticsReceived;        //物流商是否已揽收 0：未揽收 1：已揽收
    @TableField(value = "accept_time")
    private Date acceptTime;        //物流商揽收时间
    @TableField(value = "cj_logistics_status")
    private String cjLogisticsStatus;        //cj物流状态信息
    private Integer noticeUser;        //通知用户
    private Integer handleTrackingCode;        //人工处理单号：0 未处理 1 人工已处理
}
