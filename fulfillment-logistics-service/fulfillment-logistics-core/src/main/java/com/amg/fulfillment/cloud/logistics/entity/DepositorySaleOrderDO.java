package com.amg.fulfillment.cloud.logistics.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 推送销售订单到仓库表
 * </p>
 *
 * @author zzx
 * @since 2021-06-15
 */
@Data
@TableName("t_depository_sale_order")
@ApiModel(value="DepositorySaleOrderDO对象", description="推送销售订单到仓库表")
public class DepositorySaleOrderDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "仓库code")
    private String depositoryCode;

    @ApiModelProperty(value = "仓库名称")
    private String depositoryName;

    @ApiModelProperty(value = "预销售订单")
    private String saleOrder;

    @ApiModelProperty(value = "销售预告发送仓库成功 1：成功 0 失败")
    private Integer saleNotice;

    @ApiModelProperty(value = "推送失败原因")
    private String noticeFailReason;

    @ApiModelProperty(value = "发送到仓库消息内容")
    private String predictionContent;

    @ApiModelProperty(value = "预报次数")
    private Integer predictionCount;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "取消推送 1：不取消 2：取消")
    private Boolean cancel;

    private Boolean isDeleted;

    private Date createTime;

    private String createBy;

    private Date updateTime;

    private String updateBy;


}
