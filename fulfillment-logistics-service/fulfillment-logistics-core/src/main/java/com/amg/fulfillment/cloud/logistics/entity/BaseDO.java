package com.amg.fulfillment.cloud.logistics.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.Date;

/**
 * Created by Seraph on 2021/5/24
 */

@Data
public class BaseDO {

    @TableLogic
    @TableField(value = "is_deleted")
    protected Boolean isDeleted;      //是否删除   0 否   1 是
    @TableField(value = "create_time")
    protected Date createTime;        //创建时间
    @TableField(value = "create_by")
    private String createBy;        //创建人
    @TableField(value = "update_time")
    protected Date updateTime;        //最后修改时间
    @TableField(value = "update_by")
    protected String updateBy;        //最后修改人
}
