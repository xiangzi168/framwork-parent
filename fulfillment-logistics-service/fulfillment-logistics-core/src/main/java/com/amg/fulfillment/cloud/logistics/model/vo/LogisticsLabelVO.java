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
@ApiModel(value = "LogisticsLabelVO")
public class LogisticsLabelVO {

    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "标签名称")
    private String name;
    @ApiModelProperty(value = "操作行为")
    private String operationalBehavior;
    @ApiModelProperty(value = "最后修改时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;
    @ApiModelProperty(value = "最后修改人")
    private String updateBy;
    @ApiModelProperty(value = "关联类目 code")
    private String categoryCodeList;
    @ApiModelProperty(value = "关联类目")
    private List<LogisticsLabelCategoryVO> categoryList;

}
