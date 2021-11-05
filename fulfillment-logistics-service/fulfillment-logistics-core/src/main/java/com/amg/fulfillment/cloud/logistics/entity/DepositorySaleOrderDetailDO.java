package com.amg.fulfillment.cloud.logistics.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 销售订单详情
 * </p>
 *
 * @author zzx
 * @since 2021-06-17
 */
@Data
@TableName("t_depository_sale_order_detail")
@ApiModel(value="DepositorySaleOrderDetailDO对象", description="销售订单详情")
public class DepositorySaleOrderDetailDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "销售id")
    private Long saleId;

    @ApiModelProperty(value = "销售订单号")
    private String saleOrder;

    @ApiModelProperty(value = "item_id")
    private String itemId;

    @ApiModelProperty(value = "采购id")
    private String purchaseId;

    @ApiModelProperty(value = "error_product表的purchase_id数据")
    private String receivePurchaseId;

    @ApiModelProperty(value = "purchae_package表主键")
    private Long purchasePackageId;

    @ApiModelProperty(value = "logistics_package表主键")
    private Long logisticsPackageId;

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "channelSku")
    private String channelSku;


    @ApiModelProperty(value = "name")
    private String name;

    @ApiModelProperty(value = "1:临采，3 备货")
    private Integer pattern;

    @ApiModelProperty(value = "状态：1：预报仓库 0 取消预报")
    private Integer predictionState;

    @ApiModelProperty(value = "期望重量(g)")
    private BigDecimal expireWeight;

    @ApiModelProperty(value = "实际重量(g)")
    private BigDecimal weight;

    @ApiModelProperty(value = "入库状态： 10待入库  22已入库")
    private Integer receiveState;

    @ApiModelProperty(value = "入库时间")
    private Date receiveTime;

    private Date createTime;

    private String createBy;

    private Date updateTime;

    private String updateBy;


}
