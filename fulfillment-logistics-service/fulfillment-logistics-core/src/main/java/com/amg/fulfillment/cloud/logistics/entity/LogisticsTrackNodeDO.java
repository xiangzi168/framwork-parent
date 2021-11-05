package com.amg.fulfillment.cloud.logistics.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Created by Seraph on 2021/5/28
 */

@Data
@TableName(value = "t_logistics_track_node")
public class LogisticsTrackNodeDO extends BaseDO {
    private static final long serialVersionUID = 1L;
    @TableId
    private Long id;
    private Integer type;
    @TableField(value = "logistics_code")
    private String logisticsCode;
    private String node;
    @TableField(value = "desc_cn")
    private String descCn;
    @TableField(value = "desc_en")
    private String descEn;
    @TableField(value = "inside_cn")
    private String insideCn;
    @TableField(value = "inside_en")
    private String insideEn;

}
