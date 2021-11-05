package com.amg.fulfillment.cloud.logistics.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Created by Seraph on 2021/5/24
 */

@Data
@TableName(value = "t_logistics_label_category")
public class LogisticsLabelCategoryDO extends BaseDO{
    private static final long serialVersionUID = 1L;
    @TableId
    private Long id;
    @TableField(value = "label_id")
    private Long labelId;
    @TableField(value = "category_code")
    private String categoryCode;
    @TableField(value = "category_name")
    private String categoryName;
    @TableField(value = "category_level")
    private String categoryLevel;
}
