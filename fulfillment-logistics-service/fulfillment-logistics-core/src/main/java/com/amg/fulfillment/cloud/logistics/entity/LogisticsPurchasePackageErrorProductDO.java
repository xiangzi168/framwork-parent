package com.amg.fulfillment.cloud.logistics.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Seraph on 2021/5/11
 */

@Data
@TableName("t_logistics_purchase_package_error_product")
public class LogisticsPurchasePackageErrorProductDO implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId
    private Long id;
    @TableField(value = "product_id")
    private Long productId;      //物流商品 id
    @TableField(value = "purchase_package_Id")
    private Long purchasePackageId;       //采购包裹  id
    @TableField(value = "item_id")
    private String itemId;       //采购包裹  id
    @TableField(value = "purchase_id")
    private String purchaseId;      //采购 id
    @TableField(value = "sales_order_id")
    private String salesOrderId;      //销售订单 id
    @TableField(value = "arrive_time")
    private Date arriveTime;      //销售订单 id
    @TableField(value = "error_purchase_id")
    private String errorPurchaseId;     //异常采购 id
    @TableField(value = "error_type")
    private String errorType;       //异常类型
    @TableField(value = "error_handle")
    private Integer errorHandle;        //异常处理   1异常待处理  2 已重新采购  3已直接入库   4已作废处理
    @TableField(value = "error_message")
    private String errorMessage;        //异常描述
    @TableField(value = "error_img")
    private String errorImg;        //仓库图片
    @TableField(value = "create_time")
    private Date createTime;        //创建时间
    @TableField(value = "update_time")
    private Date updateTime;        //最后修改时间
    private String sku;
    private String channleSku;
    private BigDecimal weight;
    @ApiModelProperty(value = "itemid状态 10待入库 20待处理 21已处理 22已入库")
    private Integer status;
    private String createBy;
    @TableLogic
    private Integer isDeleted;
    private String updateBy;
    @ApiModelProperty(value = "人工预报对缺货是否隐藏：0 不隐藏 1 隐藏")
    private Integer hidden;
}
