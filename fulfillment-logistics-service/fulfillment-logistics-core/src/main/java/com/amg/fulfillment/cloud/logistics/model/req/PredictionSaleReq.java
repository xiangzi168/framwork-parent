package com.amg.fulfillment.cloud.logistics.model.req;

import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author Tom
 * @date 2021-05-14-16:17
 */

@Data
@ApiModel("推送预销售订单")
public class PredictionSaleReq{
    @NotBlank(message = "仓库代码不能为null")
    @ApiModelProperty(name = "仓库代码")
    private String depositoryCode = Constant.DEPOSITORY_WB;
    @ApiModelProperty(name = "仓库名称")
    private String depositoryName = Constant.DEPOSITORY_WB_NAME;
    @NotNull(message = "id不能为null")
    @Size(min = 1,message = "id至少有一项")
    @ApiModelProperty(name = "ids")
    private List<Long> ids;
}
