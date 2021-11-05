package com.amg.fulfillment.cloud.logistics.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @ClassName LogisticsPackageMergeStatusDO
 * @Description 包裹信息和状态信息对象
 * @Author qh
 * @Date 2021/7/28 18:33
 **/
public class LogisticsPackageMergeStatusDO extends LogisticsPurchasePackageDO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @ApiModelProperty(value = "物流编号")
    private String logisticsId;

    @ApiModelProperty(value = "运单号")
    private String mailNo;

    @ApiModelProperty(value = "是否发货     1 已经  0 还未")
    private Boolean consign;

    @ApiModelProperty(value = "发货时间")
    private Date consignTime;

    @ApiModelProperty(value = "是否揽收    1 已经  0 还未")
    private Boolean accept;

    @ApiModelProperty(value = "揽收时间")
    private Date acceptTime;

    @ApiModelProperty(value = "是否运输中 1 已经  0 还未")
    private Boolean transport;

    @ApiModelProperty(value = "运输开始时间")
    private Date transportTime;

    @ApiModelProperty(value = "是否派送中 1已经 0还未")
    private Boolean delivering;

    @ApiModelProperty(value = "派送开始时间")
    private Date deliveringTime;

    @ApiModelProperty(value = "是否签收 1已经签收 0还未")
    private Boolean sign;

    @ApiModelProperty(value = "签收时间")
    private Date signTime;

    @ApiModelProperty("物流状态")
    private Integer logisticsStatus;

    public Integer getLogisticsStatus() {
        return logisticsStatus;
    }

    public void setLogisticsStatus(Integer logisticsStatus) {
        this.logisticsStatus = logisticsStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getLogisticsId() {
        return logisticsId;
    }

    public void setLogisticsId(String logisticsId) {
        this.logisticsId = logisticsId;
    }
    public String getMailNo() {
        return mailNo;
    }

    public void setMailNo(String mailNo) {
        this.mailNo = mailNo;
    }
    public Boolean getConsign() {
        return consign;
    }

    public void setConsign(Boolean consign) {
        this.consign = consign;
    }
    public Date getConsignTime() {
        return consignTime;
    }

    public void setConsignTime(Date consignTime) {
        this.consignTime = consignTime;
    }
    public Boolean getAccept() {
        return accept;
    }

    public void setAccept(Boolean accept) {
        this.accept = accept;
    }
    public Date getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(Date acceptTime) {
        this.acceptTime = acceptTime;
    }
    public Boolean getTransport() {
        return transport;
    }

    public void setTransport(Boolean transport) {
        this.transport = transport;
    }
    public Date getTransportTime() {
        return transportTime;
    }

    public void setTransportTime(Date transportTime) {
        this.transportTime = transportTime;
    }
    public Boolean getDelivering() {
        return delivering;
    }

    public void setDelivering(Boolean delivering) {
        this.delivering = delivering;
    }
    public Date getDeliveringTime() {
        return deliveringTime;
    }

    public void setDeliveringTime(Date deliveringTime) {
        this.deliveringTime = deliveringTime;
    }
    public Boolean getSign() {
        return sign;
    }

    public void setSign(Boolean sign) {
        this.sign = sign;
    }
    public Date getSignTime() {
        return signTime;
    }

    public void setSignTime(Date signTime) {
        this.signTime = signTime;
    }

}
