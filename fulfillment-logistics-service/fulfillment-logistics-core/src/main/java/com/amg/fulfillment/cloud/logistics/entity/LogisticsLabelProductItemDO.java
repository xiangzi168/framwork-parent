package com.amg.fulfillment.cloud.logistics.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Created by Seraph on 2021/5/26
 */

@Data
@TableName(value = "t_logistics_label_product_item")
public class LogisticsLabelProductItemDO extends BaseDO{
    private static final long serialVersionUID = 1L;
    @TableId
    private Long id;
    @TableField(value = "product_id")
    private Long productId;
    @TableField(value = "label_id")
    private Long labelId;
    @TableField(exist = false)
    private String name;
}
