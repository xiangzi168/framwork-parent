package com.amg.fulfillment.cloud.logistics.entity;

import java.math.BigDecimal;
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
 * 物流商品表
 * </p>
 *
 * @author zzx
 * @since 2021-06-18
 */
@Data
@TableName("t_logistics_purchase_product")
@ApiModel(value="LogisticsPurchaseProductDO对象", description="物流商品表")
public class LogisticsPurchaseProductDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    @ApiModelProperty(value = "采购包裹id")
    private Long packageId;

    @ApiModelProperty(value = "销售订单sku")
    private String sku;

    @ApiModelProperty(value = "渠道sku（1688）")
    private String skuChannel;

    @ApiModelProperty(value = "spu")
    private String spu;

    @ApiModelProperty(value = "spuChannel")
    private String spuChannel;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "商品图片")
    private String productImg;

    @ApiModelProperty(value = "商品属性")
    private String productAttribute;

    @ApiModelProperty(value = "商品数量")
    private Integer productCount;

    @ApiModelProperty(value = "商品重量(单件)")
    private BigDecimal productWeight;

    @ApiModelProperty(value = "1688sku原始数据")
    private String originalSkuCode;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "最后修改时间")
    private Date updateTime;

}
