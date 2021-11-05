package com.amg.fulfillment.cloud.logistics.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Created by Seraph on 2021/5/25
 */

@Data
@TableName(value = "t_logistics_provider")
public class LogisticsProviderDO extends BaseDO {
    private static final long serialVersionUID = 1L;
    @TableId
    private Long id;
    @TableField(value = "logistics_code")
    private String logisticsCode;
    @TableField(value = "logistics_name")
    private String logisticsName;
    @TableField(value = "logistics_desc")
    private String logisticsDesc;
    @TableField(value = "strategy_code")
    private String strategyCode;
    // 物流商在万邦仓库注册的前缀码
    private String depositoryRegisterCode;

}
