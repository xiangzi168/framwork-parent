package com.amg.fulfillment.cloud.logistics.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Created by Seraph on 2021/5/24
 */

@Data
@TableName(value = "t_logistics_rules")
public class LogisticsRulesDO extends BaseDO {
    private static final long serialVersionUID = 1L;
    @TableId
    private Long id;
    private String name;
    @TableField(value = "logistics_code")
    private String logisticsCode;
    @TableField(value = "logistics_name")
    private String logisticsName;
    @TableField(value = "channel_code")
    private String channelCode;
    @TableField(value = "channel_name")
    private String channelName;
    private Integer level;
    private String  content;
    @TableField(value = "is_disable")
    private Integer isDisable;
    @TableField(value = "operational_behavior")
    private String operationalBehavior;


}
