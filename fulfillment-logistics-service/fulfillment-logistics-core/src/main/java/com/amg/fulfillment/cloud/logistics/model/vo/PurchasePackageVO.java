package com.amg.fulfillment.cloud.logistics.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * Created by Seraph on 2021/5/11
 */

@Data
@ApiModel("PurchasePackageVO")
public class PurchasePackageVO {


    private Long id;
    @ApiModelProperty("包裹单号")
    private String packageNo;  // 包裹单号;
    @ApiModelProperty("包裹状态  取1688实际值")
    private String status;
    @ApiModelProperty("异常状态   0 无异常   1异常待处理   2异常已处理")
    private Integer label;
    @ApiModelProperty("预报状态   0 未预报   1 已预报 ")
    private Integer prediction;
    @ApiModelProperty("预报失败原因")
    private String predictionFailReason;
    @ApiModelProperty("入库状态  0 未入库  1 已入库")
    private Integer warehousing;
    @ApiModelProperty("快递公司名称")
    private String expressCompanyName;
    @ApiModelProperty("快递公司编号")
    private String expressCompanyCode;
    @ApiModelProperty("快递单号")
    private String expressBillNo;
    @ApiModelProperty("渠道订单(采购单号)")
    private String channelOrderId;
    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
    @ApiModelProperty("入库时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date receivingGoodTime;
    @ApiModelProperty("商品列表")
    private List<PurchasePackageProductVO> productList;
    @ApiModelProperty("预报时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date predictionTime;
    @ApiModelProperty("添加包裹类型 1: 程序添加 2：人工添加 ")
    private Integer typeAdd;
    @ApiModelProperty("物流状态")
    private Integer logisticsStatus;
    @ApiModelProperty("揽收时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date acceptTime;
    @ApiModelProperty("签收时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date signTime;
    @ApiModelProperty("包裹源头类型")
    private Integer packageSourceType;//包裹源头类型 标明在哪里加入的包裹 5拼多多 6淘宝
    @ApiModelProperty("是否有效")
    private Boolean valid; //true有效 false无效

}
