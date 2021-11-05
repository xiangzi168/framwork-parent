package com.amg.fulfillment.cloud.logistics.dto.logistic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author Tom
 * @date 2021-04-19-20:01
 */
@ApiModel("物流查询对象")
@Data
public class LogisticOrderSearchDto {

    @NotBlank
    @ApiModelProperty(name = "物流公司码")
    private String logisticCode;
    @ApiModelProperty(name = "运单号(单号、客户单号和面单号)")
    private String orderNo;
    @ApiModelProperty(name = "本系统成的订单号")
    private String logisticsOrderNo;
    @ApiModelProperty(name = "包裹处理号")
    private String processCode;
    @ApiModelProperty("运单号")
    private String wayBillNo;
    @ApiModelProperty(name = "追踪号")
    private String trackingNumber;
    @ApiModelProperty(name = "标签大小")
    private String labelSize;
}
