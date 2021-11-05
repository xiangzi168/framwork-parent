package com.amg.fulfillment.cloud.logistics.model.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Seraph on 2021/5/13
 */

@Data
@ApiModel(value = "DeliveryPackageReq")
public class DeliveryPackageReq extends BaseReq implements Serializable {
    @ApiModelProperty("包裹类型  1 境外发货单  2 AE 发货单 4 CJ发货单")
    private Integer type = 1;
    @ApiModelProperty("物流包裹单号")
    private String logisticsOrderNo;
    @ApiModelProperty("物流商处理号")
    private String logisticsProcessCode;
    @ApiModelProperty("物流商单号")
    private String logisticsWayBillNo;
    @ApiModelProperty("跟踪单号")
    private String logisticsTrackingCode;
    @ApiModelProperty("销售订单号")
    private String salesOrderId;
    @ApiModelProperty("渠道订单号")
    private String channelOrderId;
    @ApiModelProperty("物流商 code")
    private String logisticsCode;
    @ApiModelProperty("渠道 code")
    private String channelCode;
    @ApiModelProperty("物流商")
    private String logisticsProvider;
    @ApiModelProperty("物流渠道")
    private String logisticsChannel;
    @ApiModelProperty("最新物流节点")
    private String logisticsLastNode;
    @ApiModelProperty("国家")
    private String country;
    @ApiModelProperty("是否有效   0 否  1 是")
    private Integer isValid;
    @ApiModelProperty("出库状态  0 初始状态   1已制单     2推送失败    3发货中    4发货失败    5取消发货     6已发货")
    private Integer deliveryStatus;
    @ApiModelProperty("开始收货(签收)时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startReceivingGoodTime;
    @ApiModelProperty("结束收货(签收)时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date endReceivingGoodTime;
    @ApiModelProperty("开始出库时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startDeliveryTime;
    @ApiModelProperty("结束出库时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date endDeliveryTime;
    @ApiModelProperty("开始最近更新时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startUpdateTime;
    @ApiModelProperty("结束最近更新时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date endUpdateTime;
    @ApiModelProperty("物流商是否现实已揽收 0：未揽收 1：已揽收")
    private Integer logisticsReceived;
    @ApiModelProperty("cj物流状态信息")
    private String cjLogisticsStatus;
    @ApiModelProperty("是否通知用户 0:未通知 1：通知")
    private Integer noticeUser;

}
