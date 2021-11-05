package com.amg.fulfillment.cloud.logistics.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.*;

/**
 * @author Tom
 * @date 2021-05-24-16:48
 */
@ApiModel("新增物流规则")
@Data
@ToString
public class LogisticsRuleAddReq {

    public interface Add{}
    public interface Update{}

    @NotNull(message = "id不能为null",groups = Update.class)
    @ApiModelProperty(value = "id")
    private Long id;

    @NotBlank(message = "name不能为null",groups = {Update.class,Add.class})
    @Size(max=30,message = "name长度不超过30字符",groups = {Update.class,Add.class})
    @ApiModelProperty(value = "规则名称")
    private String name;

    @NotBlank(message = "logisticsCode不能为null",groups = {Update.class,Add.class})
    @ApiModelProperty(value = "物流code")
    private String logisticsCode;

    @ApiModelProperty(value = "物流名称")
    private String logisticsName;

    @NotBlank(message = "channelCode不能为null",groups = {Update.class,Add.class})
    @ApiModelProperty(value = "物流渠道 code")
    private String channelCode;

    @ApiModelProperty(value = "物流渠道名称")
    private String channelName;
    @Positive(message = "level必须是正整数",groups = {Update.class,Add.class})
    @ApiModelProperty(value = "外部优先级序号")
    private Integer level;

    @NotBlank(message = "content不能为null",groups = {Update.class,Add.class})
    @ApiModelProperty(value = "规则内容")
    private String content;

    @ApiModelProperty(value = "是否禁用   0 否  1 是")
    private Integer isDisable;

    @ApiModelProperty(value = "操作行为")
    private String operationalBehavior;

    @ApiModelProperty(value = "是否删除   0 否   1 是")
    private Boolean isDeleted;
}
