package com.amg.fulfillment.cloud.logistics.model.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * Created by Seraph on 2021/6/1
 */

@Data
public class Delivery1688PackageExcel {


    @Excel(name = "包裹单号", orderNum = "0")
    private String logisticsOrderNo;
    @Excel(name = "物流商单号", orderNum = "1")
    private String logisticsWayBillNo;
    @Excel(name = "是否有效", orderNum = "2")
    private String isValid;
    @Excel(name = "跟踪号", orderNum = "3")
    private String logisticsTrackingCode;
    @Excel(name = "物流商", orderNum = "4")
    private String logisticsName;
    @Excel(name = "物流渠道", orderNum = "5")
    private String channelName;
    @Excel(name = "国家", orderNum = "6")
    private String logisticsArea;
    @Excel(name = "出库状态", orderNum = "7")
    private String deliveryStatus;
    @Excel(name = "最新物流节点", orderNum = "8")
    private String logisticsNode;
    @Excel(name = "用户订单号", orderNum = "9")
    private String salesOrderId;
    @Excel(name = "报错原因", orderNum = "10")
    private String errorInfo;
    @Excel(name = "创建时间", orderNum = "11")
    private String createTime;
    @Excel(name = "出库时间", orderNum = "12")
    private String deliveryTime;
    @Excel(name = "签收时间", orderNum = "13")
    private String receivingGoodTime;
    @Excel(name = "最新更新时间", orderNum = "14")
    private String updateTime;
    @Excel(name = "物流商揽收时间", orderNum = "15")
    private String acceptTime;        //物流商揽收时间
    @Excel(name = "是否通知用户", orderNum = "16")
    private String noticeUser;
}
