package com.amg.fulfillment.cloud.logistics.model.req;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Seraph on 2021/6/2
 */

@Data
public class LogisticsRulesFreightImportReq implements Serializable {

    @NotNull
    @Excel(name = "规则名称（必填）",orderNum = "0")
    private String name;

    @NotNull
    @Excel(name = "物流商（必填）", orderNum = "1")
    private String logistics;

    @NotNull
    @Excel(name = "渠道名称（必填）",orderNum = "2")
    private String channel;

    @NotNull
    @Excel(name = "国家（必填）",orderNum = "3")
    private String country;
    @NotNull
    @Excel(name = "国家代码（必填）",orderNum = "4")
    private String countryAbbre;
    @NotNull
    @Excel(name = "起始重量（必填）",orderNum = "5")
    private BigDecimal startWeight;

    @NotNull
    @Excel(name = "截止重量（必填）",orderNum = "6")
    private BigDecimal endWeight;

    @NotNull
    @Excel(name = "单位价格（必填）",orderNum = "7")
    private BigDecimal unitPrice;

    @NotNull
    @Excel(name = "挂号费（必填）",orderNum = "8")
    private BigDecimal pegistrationMoney;

    @Excel(name = "时效最早天数",orderNum = "9")
    private Integer earliestPrescriptionDays;

    @Excel(name = "时效最晚天数",orderNum = "10")
    private Integer latestPrescriptionDays;

    @Excel(name = "是否禁用",orderNum = "11")
    private String isDisable;

    @Excel(name = "异常原因",orderNum = "12")
    private String errorMsg;
}
