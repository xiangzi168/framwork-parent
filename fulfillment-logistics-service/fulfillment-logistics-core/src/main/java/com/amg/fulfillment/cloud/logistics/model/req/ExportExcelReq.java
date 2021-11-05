package com.amg.fulfillment.cloud.logistics.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Seraph on 2021/5/13
 */


@Data
@ApiModel("ExportExcelReq")
public class ExportExcelReq<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("选择中 id 集合")
    private List<String> idList;
    @ApiModelProperty(value = "title, excel标题")
    private String title;
    @ApiModelProperty(value = "sheetName")
    private String sheetName;
    @ApiModelProperty(hidden = true)
    private String fileName;
    @ApiModelProperty("搜索参数")
    private T data;
}
