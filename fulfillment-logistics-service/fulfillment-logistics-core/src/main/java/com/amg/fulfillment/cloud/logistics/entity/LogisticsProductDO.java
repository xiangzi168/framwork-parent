package com.amg.fulfillment.cloud.logistics.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Seraph on 2021/5/13
 */

@Data
@TableName("t_logistics_product")
public class LogisticsProductDO implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId
    private Long id;
    private Integer type;       //包裹类型   1 采购包裹单   2发货包裹单
    @TableField(value = "relation_id")
    private Long relationId;        //关联类型 id
    private String sku;     //销售订单sku
    @TableField(value = "sku_channel")
    private String skuChannel;      //渠道sku（1688）
    private String spu;     //销售订单spu
    @TableField(value = "spu_channel")
    private String spuChannel;      //渠道spu（1688）
    @TableField(value = "category_code")
    private String categoryCode;        //类目 code
    @TableField(value = "product_name")
    private String productName;     //商品名称
    @TableField(value = "product_img")
    private String productImg;      //商品图片
    @TableField(value = "product_attribute")
    private String productAttribute;        //商品属性
    @TableField(value = "product_count")
    private Integer productCount;       //商品数量
    @TableField(value = "product_weight")
    private String productWeight;       //商品重量(单件)
    private BigDecimal productSalePriceCny;      //商品销售价格（元）
    @TableField(value = "product_declared_name_en")
    private String productDeclaredNameEn;        //英文申报名称
    @TableField(value = "product_declared_name_cn")
    private String productDeclaredNameCn;        //中文申报名称
    @TableField(value = "product_declared_price")
    private BigDecimal productDeclaredPrice;      //单件申报价值
    @TableField(value = "product_status")
    private String productStatus;
    @TableField(value = "product_info")
    private String productInfo;
    @TableField(value = "real_product_weight")
    private BigDecimal realProductWeight;      //商品重量(单件)
    @TableField(value = "real_product_count")
    private Integer realProductCount;
    @TableField(value = "refund_status")
    private Integer refundStatus;
    @TableField(value = "original_sku_code")
    private String originalSkuCode;
    @TableField(value = "create_time")
    private Date createTime;
    @TableField(value = "update_time")
    private Date updateTime;
}
