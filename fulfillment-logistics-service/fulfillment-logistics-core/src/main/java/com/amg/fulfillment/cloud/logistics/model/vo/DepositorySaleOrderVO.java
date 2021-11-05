package com.amg.fulfillment.cloud.logistics.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Tom
 * @date 2021-05-14-18:46
 */
@Data
public class DepositorySaleOrderVO {

    private Long id;

    @ApiModelProperty(value = "仓库code")
    private String depositoryCode;

    @ApiModelProperty(value = "仓库名称")
    private String depositoryName;

    @ApiModelProperty(value = "预销售订单")
    private String saleOrder;

    @ApiModelProperty(value = "销售预告发送仓库成功 1：成功 0 失败")
    private Boolean saleNotice;

    @ApiModelProperty(value = "推送失败返回原因")
    private String noticeFailReason;

    @ApiModelProperty(value = "期望重量（kg）")
    private Double expireWeight;

    @ApiModelProperty(value = "实际总量（kg）")
    private Double realWeight;

    @ApiModelProperty(value = "期望出库总数")
    private Integer expireQuantity;

    @ApiModelProperty(value = "实际到仓总数")
    private Integer realQuantity;

    @ApiModelProperty(value = "预报内容")
    private String content;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "出库条件满足1：满足 0 不满足")
    private Boolean outFlag;

    private Boolean flag;

    private Date createTime;

    private Date updateTime;

    @ApiModelProperty(value = "商品详情")
    private List<DepositorySaleOrderDetailVO> products;
}
