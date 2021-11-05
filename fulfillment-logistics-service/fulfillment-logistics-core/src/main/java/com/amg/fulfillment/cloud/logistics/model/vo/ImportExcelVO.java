package com.amg.fulfillment.cloud.logistics.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Seraph on 2021/5/26
 */

@Data
@ApiModel(value = "ImportExcelVO")
public class ImportExcelVO {

    @ApiModelProperty(value = "是否导入成功")
    public Boolean success;

    @ApiModelProperty(value = "导入失败下载的文件地址")
    private String errorFileUrl;

}
