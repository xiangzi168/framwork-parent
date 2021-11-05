package com.amg.fulfillment.cloud.logistics.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 物流包裹关联item表
 *
 * </p>
 *
 * @author zzx
 * @since 2021-06-15
 */
@TableName("t_logistics_package_item")
@ApiModel(value = "LogisticsPackageItemDO对象", description = "物流包裹关联item表")
@Data
public class LogisticsPackageItemDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "包裹单号")
    private Long packageId;

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "item id")
    private String itemId;

    @ApiModelProperty(value = "采购 id")
    private String purchaseId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "最后修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "最后修改人")
    private String updateBy;

   @ApiModelProperty(value = "删除 0 否 1 是  ")
   @TableLogic
   private Integer isDeleted;
}
