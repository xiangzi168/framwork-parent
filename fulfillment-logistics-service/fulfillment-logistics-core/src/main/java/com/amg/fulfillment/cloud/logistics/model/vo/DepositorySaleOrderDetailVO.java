package com.amg.fulfillment.cloud.logistics.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author Tom
 * @date 2021-05-14-18:52
 */
@Data
public class DepositorySaleOrderDetailVO {

    private Long id;

    @ApiModelProperty(value = "出库订单id")
    private Long saleOrderId;

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "期望重量（kg）")
    private Integer expireQuantity;

    @ApiModelProperty(value = "实际总量（kg）")
    private Integer realQuantity;

    @ApiModelProperty(value = "期望出库总数")
    private Double expireWeight;

    @ApiModelProperty(value = "实际到仓总数")
    private Double realWeight;

    @ApiModelProperty("imageUrls")
    private String imageUrls;

    @ApiModelProperty(value = "出库条件满足1：满足 0 不满足")
    private Boolean outFlag;

    private Boolean flag;

    private Date createTime;

    private Date updateTime;
}
