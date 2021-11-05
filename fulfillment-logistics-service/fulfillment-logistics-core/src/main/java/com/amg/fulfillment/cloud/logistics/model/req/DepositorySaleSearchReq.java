package com.amg.fulfillment.cloud.logistics.model.req;

import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Tom
 * @date 2021-05-14-18:42
 */
@Data
@ApiModel("推送仓库销售订单查询")
public class DepositorySaleSearchReq extends BaseReq {
    @NotBlank(message = "仓库代码不能为null")
    @ApiModelProperty(name = "仓库代码")
    private String depositoryCode = Constant.DEPOSITORY_WB;
    @ApiModelProperty(name = "仓库名称")
    private String depositoryName = Constant.DEPOSITORY_WB_NAME;
    private Boolean saleNotice;
}
