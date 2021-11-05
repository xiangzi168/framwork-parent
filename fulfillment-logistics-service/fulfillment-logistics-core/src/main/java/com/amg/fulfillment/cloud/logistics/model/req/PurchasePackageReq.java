package com.amg.fulfillment.cloud.logistics.model.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Seraph on 2021/5/12
 */

@Data
@ApiModel("PurchasePackageReq")
public class PurchasePackageReq extends BaseReq implements Serializable {

    private Long id;
    @ApiModelProperty("包裹单号")
    private String packageNo;
    @ApiModelProperty("物流状态 1发货 2揽收 3运输 4派送 5签收")
    private Integer logisticsStatus;
    @ApiModelProperty("包裹标签  0 无异常   1异常待处理   2异常已处理")
    private Integer label;
    @ApiModelProperty("是否入库 0 未预报   1 已预报")
    private Integer prediction;
    @ApiModelProperty("是否预报   0 未入库  1 已入库")
    private Integer warehousing;
    @ApiModelProperty("快递名称")
    private String expressName;
    @ApiModelProperty("快递code")
    private String expressCode;
     @ApiModelProperty("快递单号")
    private String expressBillNo;
    @ApiModelProperty(value = "入库开始时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startWarehousingTime;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @ApiModelProperty(value = "入库结束时间")
    private Date endWarehousingTime;
    @ApiModelProperty(value = " 添加包裹类型 1: 程序添加 2：人工添加")
    private Integer typeAdd;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @ApiModelProperty(value = "签收开始时间")
    private Date startSignTime;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @ApiModelProperty(value = "签收结束时间")
    private Date endSignTime;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @ApiModelProperty(value = "揽收开始时间")
    private Date startAcceptTime;
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @ApiModelProperty(value = "揽收结束时间")
    private Date endAcceptTime;
    @ApiModelProperty("渠道类型  其他渠道类型值:5")
    private Integer packageSourceType;
    @ApiModelProperty("渠道订单号")
    private String channelOrderId;
    @ApiModelProperty("是否有效")
    private Boolean valid; //true有效 false无效
}
