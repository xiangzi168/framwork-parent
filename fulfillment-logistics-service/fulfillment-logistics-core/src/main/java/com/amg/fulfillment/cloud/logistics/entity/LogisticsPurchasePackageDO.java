package com.amg.fulfillment.cloud.logistics.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Seraph on 2021/5/11
 */

@Data
@TableName("t_logistics_purchase_package")
@ApiModel(value="LogisticsPurchasePackageDO对象", description="物流采购包裹表")
public class LogisticsPurchasePackageDO implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId
    private Long id;
    private String packageNo;  // 包裹单号
    private String status;     //包裹状态    取1688实际值
    private Integer label;      //包裹标签   0 无异常   1异常待处理   2异常已处理
    private Integer prediction;     //是否预报   0 未预报   1 已预报
    private String predictionFailReason;     //预报失败原因
    private Integer warehousing;        //是否入库  10 未入库  22 已入库
    @TableField(value = "express_company_name")
    private String expressCompanyName;     //快递公司名称
    @TableField(value = "express_company_code")
    private String expressCompanyCode;     //快递公司编号
    @TableField(value = "express_code")
    private String expressCode;     //物流编号
    @TableField(value = "express_bill_no")
    private String expressBillNo;       //物流公司运单号
    @TableField(value = "old_express_bill_no")
    private String oldExpressBillNo;       //物流公司运单号
    @TableField(value = "expected_arrive")
    private Date expectedArrive;        //预计到仓⽇期
    @TableField(value = "ship_from_province")
    private String shipFromProvince;        //发货省份
    @TableField(value = "channel_order_id")
    private String channelOrderId;     //渠道订单id
    @TableField(value = "deliver_good_time")
    private Date deliverGoodTime;       //发货时间
    @TableField(value = "receiving_good_time")
    private Date receivingGoodTime;     //收货时间
    private String remark;      //采购备注

    @ApiModelProperty(value = "入库通知反馈行为")
    private String warehousingFeedbackAction;

    @ApiModelProperty(value = "入库通知采购原因")
    private String warehousingPurchaseReason;

    @ApiModelProperty(value = "入库通知物流号")
    private String warehousingExpressId;

    @ApiModelProperty(value = "入库通知物流到达时间")
    private Date warehousingExpressReceiveTime;

    @ApiModelProperty(value = "入库通知物流图片")
    private String warehousingExpressImage;

    @TableField(value = "create_time")
    private Date createTime;        //创建时间
    private String createBy;
    @TableField(value = "update_time")
    private Date updateTime;        //最后修改时间
    private String updateBy;
    private Date predictionTime; //预报时间
    // 添加包裹类型 1: 程序添加 2：人工添加
    private Integer typeAdd;
    @TableField(value = "package_source_type")
    private Integer packageSourceType;//包裹源头类型 标明在哪里加入的包裹 5拼多多 6淘宝
    @TableField(value = "valid")
    private Boolean valid; //true有效 false无效
}
