package com.amg.fulfillment.cloud.logistics.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * Created by Seraph on 2021/5/24
 */

@Data
@ApiModel(value = "LogisticsLabelProductVO")
public class LogisticsLabelProductVO {

    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("商品 sku")
    private String sku;
    @ApiModelProperty("商品名称")
    private String productName;
    @ApiModelProperty("商品属性")
    private String productAttribute;
    @ApiModelProperty("商品图片")
    private String productImg;
    @ApiModelProperty(value = "操作行为")
    private String operationalBehavior;
    @ApiModelProperty(value = "最后修改时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;
    @ApiModelProperty(value = "最后修改人")
    private String updateBy;
    @ApiModelProperty(value = "商品标签")
    private List<LogisticsLabelVO> logisticsLabelVOList;
}
