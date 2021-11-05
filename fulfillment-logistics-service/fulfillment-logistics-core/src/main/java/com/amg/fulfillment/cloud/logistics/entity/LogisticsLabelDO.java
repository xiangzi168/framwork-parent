package com.amg.fulfillment.cloud.logistics.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Created by Seraph on 2021/5/24
 */

@Data
@TableName(value = "t_logistics_label")
public class LogisticsLabelDO extends BaseDO {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    @TableField(value = "operational_behavior")
    private String operationalBehavior;
}
