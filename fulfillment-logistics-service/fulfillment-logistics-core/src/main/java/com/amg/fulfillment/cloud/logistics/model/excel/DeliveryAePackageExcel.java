package com.amg.fulfillment.cloud.logistics.model.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * Created by Seraph on 2021/6/1
 */

@Data
public class DeliveryAePackageExcel {

    @Excel(name = "物流单号", orderNum = "0")
    private String logisticsOrderNo;
    @Excel(name = "跟踪号", orderNum = "1")
    private String logisticsTrackingCode;
    @Excel(name = "物流渠道", orderNum = "2")
    private String channelName;
    @Excel(name = "最新节点", orderNum = "3")
    private String logisticsNode;
    @Excel(name = "关联渠道订单号", orderNum = "4")
    private String channelOrderId;
    @Excel(name = "创建时间", orderNum = "5")
    private String createTime;
    @Excel(name = "签收时间", orderNum = "6")
    private String receivingGoodTime;
    @Excel(name = "更新时间", orderNum = "7")
    private String updateTime;

}
