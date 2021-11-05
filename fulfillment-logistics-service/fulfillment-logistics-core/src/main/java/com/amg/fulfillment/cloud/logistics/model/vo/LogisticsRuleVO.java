package com.amg.fulfillment.cloud.logistics.model.vo;

import com.amg.fulfillment.cloud.logistics.entity.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.ToString;

/**
 * @author Tom
 * @date 2021-05-24-18:24
 */
@ApiModel("返回物流规则")
@Data
@ToString
public class LogisticsRuleVO extends BaseDO {


    private Long id;

    @ApiModelProperty(value = "规则名称")
    private String name;

    @ApiModelProperty(value = "物流code")
    private String logisticsCode;

    @ApiModelProperty(value = "物流名称")
    private String logisticsName;

    @ApiModelProperty(value = "物流渠道 code")
    private String channelCode;

    @ApiModelProperty(value = "物流渠道名称")
    private String channelName;

    @ApiModelProperty(value = "外部优先级序号")
    private Integer level;

    @ApiModelProperty(value = "规则内容")
    private String content;

    @ApiModelProperty(value = "是否禁用   0 否  1 是")
    private Integer isDisable;

    @ApiModelProperty(value = "操作行为")
    private String operationalBehavior;

    @ApiModelProperty(value = "是否删除   0 否   1 是")
    private Boolean isDeleted;
}
