package com.amg.fulfillment.cloud.logistics.model.bo;

import java.util.Date;

/**
 * @ClassName OrderLogisticsTracingModel
 * @Description 1688物流详情对象
 * @Author qh
 * @Date 2021/7/27 18:53
 **/
public class OrderLogisticsTracingModel {
    /**
     * 物流状态
     */
    String statusChanged;
    /**
     * 物流单号
     */
    String logisticsId;
    /**
     * 运单号
     */
    String mailNo;
    /**
     * 状态改变时间
     */
    Date changeTime;
    public String getStatusChanged() {
        return statusChanged;
    }

    public void setStatusChanged(String statusChanged) {
        this.statusChanged = statusChanged;
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

    public Date getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(Date changeTime) {
        this.changeTime = changeTime;
    }
}
