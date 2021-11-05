package com.amg.fulfillment.cloud.logistics.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

/**
 * @ClassName PackageStatusMarkReq
 * @Description TODO
 * @Author 35112
 * @Date 2021/8/26 14:06
 **/
@Data
@ApiModel("包裹状态修改请求")
public class PackageStatusMarkReq {
    @ApiModelProperty("ID集合")
    private List<Long> id;
    @ApiModelProperty("状态值 true 有效 false 无效")
    private boolean status;
}
