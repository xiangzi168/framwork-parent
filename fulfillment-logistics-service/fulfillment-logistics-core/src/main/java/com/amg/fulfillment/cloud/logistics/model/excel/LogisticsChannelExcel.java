package com.amg.fulfillment.cloud.logistics.model.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;


/**
 * Created by Seraph on 2021/5/31
 */

@Data
public class LogisticsChannelExcel {

    @Excel(name = "物流商", orderNum = "0")
    private String logisticsName;
    @Excel(name = "物流商代码", orderNum = "1")
    private String logisticsCode;
    @Excel(name = "物流渠道", orderNum = "2")
    private String channelName;
    @Excel(name = "渠道代码", orderNum = "3")
    private String channelCode;
    @Excel(name = "是否禁用", orderNum = "4")
    private String isDisable;
    @Excel(name = "操作记录", orderNum = "5")
    private String operationalBehavior;
    @Excel(name = "最后修改人", orderNum = "6")
    private String updateBy;
    @Excel(name = "最后修改时间", orderNum = "7")
    private String updateTime;
}
