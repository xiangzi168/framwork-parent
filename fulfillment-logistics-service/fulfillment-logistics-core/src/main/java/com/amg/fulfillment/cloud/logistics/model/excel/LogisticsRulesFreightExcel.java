package com.amg.fulfillment.cloud.logistics.model.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by Seraph on 2021/5/31
 */

@Data
public class LogisticsRulesFreightExcel {

    @Excel(name = "规则名称", orderNum = "0")
    private String name;
    @Excel(name = "物流商", orderNum = "1")
    private String logisticsName;
    @Excel(name = "渠道名称", orderNum = "2")
    private String channelName;
    @Excel(name = "国家", orderNum = "3")
    private String country;
    @Excel(name = "起结重量 (g)", orderNum = "4")
    private BigDecimal startWeight;
    @Excel(name = "截止重量（g）", orderNum = "5")
    private BigDecimal endWeight;
    @Excel(name = "挂号费（元）", orderNum = "6")
    private BigDecimal pegistrationMoney;
    @Excel(name = "单位价格（元/kg）", orderNum = "7")
    private BigDecimal unitPrice;
    @Excel(name = "时效最早天数", orderNum = "8")
    private Integer earliestPrescriptionDays;
    @Excel(name = "时效最晚天数", orderNum = "9")
    private Integer latestPrescriptionDays;
    @Excel(name = "是否禁用", orderNum = "10")
    private String isDisable;
}
