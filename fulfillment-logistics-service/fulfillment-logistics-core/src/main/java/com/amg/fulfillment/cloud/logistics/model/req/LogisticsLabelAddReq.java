package com.amg.fulfillment.cloud.logistics.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Created by Seraph on 2021/5/24
 */

@Data
@ApiModel(value = "LogisticsLabelAddReq")
public class LogisticsLabelAddReq {

    @NotNull(message = "标签名称 不能为空")
    @Size(max = 30)
    @ApiModelProperty(value = "标签名称")
    private String name;
    @Size(min = 1, message = "关联类目最少一个")
    @ApiModelProperty(value = "关联类目")
    private List<LogisticsLabelCategoryAddReq> categoryList;

    @Data
    @ApiModel(value = "LogisticsLabelCategoryAddReq")
    public static class LogisticsLabelCategoryAddReq
    {
        @NotNull(message = "标签类目 code 不能为空")
        @ApiModelProperty("标签类目 code")
        private String categoryCode;
        @ApiModelProperty("标签类目名称")
        private String categoryName;
        @ApiModelProperty("标签类目等级")
        private String categoryLevel;
    }
}
