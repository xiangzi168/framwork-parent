package com.amg.fulfillment.cloud.logistics.dto.logistic;

import com.amg.fulfillment.cloud.logistics.dto.depository.OutDepositoryResultDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName TestDepositoryManagerdTO
 * @Description TODO
 * @Author 35112
 * @Date 2021/8/2 13:52
 **/
@ApiModel("测试物流出库类")
@Data
public class TestDepositoryManagerDto {
    @ApiModelProperty(value = "订单请求实体")
    private LogisticOrderDto logisticOrderDto;
    @ApiModelProperty(value = "仓库返回结果")
    private OutDepositoryResultDto outDepositoryResultTestDto;



    @ApiModelProperty(value = "是否是测试请求")
    private Boolean depositoryManagerTest;
}
