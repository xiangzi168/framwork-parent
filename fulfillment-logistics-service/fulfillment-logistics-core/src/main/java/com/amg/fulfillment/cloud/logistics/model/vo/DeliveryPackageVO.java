package com.amg.fulfillment.cloud.logistics.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Seraph on 2021/5/13
 */

@Data
@ApiModel(value = "DeliveryPackageVO")
public class
DeliveryPackageVO {

    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("发货单包裹类型` 1 境外发货单  2 AE 发货单")
    private Integer type;
    @ApiModelProperty("关联销售订单")
    private String salesOrderId;     //关联销售订单
    @ApiModelProperty("关联渠道订单")
    private String channelOrderId;     //关联渠道订单
    @ApiModelProperty("出库状态   0 初始状态   30已制单  31制单失败     40发货中    41已发货 42取消发货 43发货失败    45推送失败  -1异常")
    private Integer deliveryStatus;
    @ApiModelProperty("物流商 code")
    private String logisticsCode;       //物流商 code
    @ApiModelProperty("物流名称")
    private String logisticsName;       //物流名称
    @ApiModelProperty("物流渠道 code")
    private String channelCode;
    @ApiModelProperty("物流渠道名称")
    private String channelName;
    @ApiModelProperty("物流包裹单号")
    private String logisticsOrderNo;
    @ApiModelProperty("物流跟踪单号")
    private String logisticsTrackingCode;
    @ApiModelProperty("物流跟踪单号")
    private String logisticsProcessCode;
    @ApiModelProperty("物流商运单号")
    private String logisticsWayBillNo;
    @ApiModelProperty("物流状态  0 初始状态  10已创建物流单   20 已出库   30  已收货   50 已取消  99 创建物流单异常")
    private String logisticsStatus;     //物流状态
    @ApiModelProperty("物流费用")
    private BigDecimal logisticsCost;       //物流费用
    @ApiModelProperty("物流费用币种")
    private String logisticsCostCurrency;       //物流费用币种
    @ApiModelProperty("物流最新节点")
    private String logisticsNode;
    @ApiModelProperty("物流收货国家")
    private String logisticsArea;
    @ApiModelProperty("包裹信息")
    private String packageInfo;     //包裹信息
    @ApiModelProperty("取消包裹原因")
    private String cancelRemark;
    @ApiModelProperty("报错原因")
    private String errorInfo;
    @ApiModelProperty("是否有效")
    private Integer isValid;
    @ApiModelProperty("出库时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date deliveryTime;
    @ApiModelProperty("收货时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date receivingGoodTime;     //收货时间
    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;        //创建时间
    @ApiModelProperty("最后修改时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;        //最后修改时间
    @ApiModelProperty("物流揽收时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date acceptTime;        //物流商揽收时间
    @ApiModelProperty(value = "是否揽收 true揽收 false未揽收")
    private Boolean logisticsReceived;
    @ApiModelProperty("物流轨迹")
    private List<LogisticsPackageTrackVO> trackList;       //物流轨迹
    @ApiModelProperty("商品信息")
    private List<LogisticsProductVO> productList;       //商品信息
    @ApiModelProperty(value = "cj物流状态信息")
    private String cjLogisticsStatus;        //cj物流状态信息
    @ApiModelProperty("是否通知用户 0:未通知 1：通知")
    private Integer noticeUser;
    private String createBy;       //创建人
    private String updateBy;       //最后修改人

}
