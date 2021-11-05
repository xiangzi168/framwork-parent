package com.amg.fulfillment.cloud.logistics.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Created by Seraph on 2021/5/24
 */

@Data
@TableName(value = "t_logistics_label_product")
public class LogisticsLabelProductDO extends BaseDO {
    private static final long serialVersionUID = 1L;
    @TableId
    private Long id;
    private String sku;
    @TableField(value = "product_name")
    private String productName;
    @TableField(value = "product_img")
    private String productImg;
    @TableField(value = "product_attribute")
    private String productAttribute;
    @TableField(value = "operational_behavior")
    private String operationalBehavior;
}
