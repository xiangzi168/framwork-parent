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
@ApiModel(value = "LogisticsLabelUpdateReq")
public class LogisticsLabelUpdateReq {

    @NotNull(message = "id 不能为空")
    @ApiModelProperty(value = "id")
    private Long id;
    @NotNull(message = "标签名称 不能为空")
    @Size(max = 30)
    @ApiModelProperty(value = "标签名称")
    private String name;
    @ApiModelProperty(value = "删除标签类目 id 集合")
    private List<Long> deleteLabelCategoryIdList;
    @ApiModelProperty(value = "修改标签类目集合")
    private List<LogisticsLabelCategoryUpdateReq> updateLabelCategoryList;
    @ApiModelProperty(value = "添加标签类目集合")
    private List<LogisticsLabelAddReq.LogisticsLabelCategoryAddReq> addLabelCategoryList;

    @Data
    @ApiModel(value = "LogisticsLabelCategoryAddReq")
    public static class LogisticsLabelCategoryUpdateReq
    {
        @NotNull(message = "id 不能为空")
        private Long id;
        @NotNull(message = "标签类目 code 不能为空")
        @ApiModelProperty("标签类目 code")
        private String categoryCode;
        @ApiModelProperty("标签类目名称")
        private String categoryName;
        @ApiModelProperty("标签类目等级")
        private String categoryLevel;
    }
}
