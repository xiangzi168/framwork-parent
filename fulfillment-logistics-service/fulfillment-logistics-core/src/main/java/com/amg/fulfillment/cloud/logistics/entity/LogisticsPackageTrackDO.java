package com.amg.fulfillment.cloud.logistics.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Seraph on 2021/5/10
 */

@Data
@TableName("t_logistics_package_track")
public class LogisticsPackageTrackDO extends BaseDO {
    private static final long serialVersionUID = 1L;
    @TableId
    private Long id;
    @TableField(value = "package_id")
    private Long packageId;     //包裹单号
    private String status;        //关键节点状态
    private String content;       //轨迹内容
    private String location;      //地点
    @TableField(value = "time_zone")
    private String timeZone;      //时区
    @TableField(value = "event_time")
    private String eventTime;     //轨迹时间
}
