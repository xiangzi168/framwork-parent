package com.amg.fulfillment.cloud.logistics.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by Seraph on 2021/5/25
 */

@Data
@TableName(value = "t_logistics_rules_freight")
public class LogisticsRulesFreightDO extends BaseDO{
    private static final long serialVersionUID = 1L;
    @TableId
    private Long id;
    private String name;
    @TableField(value = "logistics_code")
    private String logisticsCode;
    @TableField(value = "channel_code")
    private String channelCode;
    private String country;
    @TableField(value = "country_abbre")
    private String countryAbbre;
    @TableField(value = "start_weight")
    private BigDecimal startWeight;
    @TableField(value = "end_weight")
    private BigDecimal endWeight;
    @TableField(value = "pegistration_money")
    private BigDecimal pegistrationMoney;
    @TableField(value = "unit_price")
    private BigDecimal unitPrice;
    @TableField(value = "earliest_prescription_days")
    private Integer earliestPrescriptionDays;
    @TableField(value = "latest_prescription_days")
    private Integer latestPrescriptionDays;
    @TableField(value = "is_disable")
    private Integer isDisable;

    @TableField(exist = false)
    private String logisticsName;
    @TableField(exist = false)
    private String channelName;
}
