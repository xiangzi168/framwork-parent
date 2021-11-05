package com.amg.fulfillment.cloud.logistics.dto.depository;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author Tom
 * @date 2021-04-12-16:56
 */
@ApiModel("物流信息")
@Data
public class ExpressDto {

    @ApiModelProperty(name = "快递公司单号")
    private String expressNo;
    @ApiModelProperty(name = "快递公司Id")
    private Integer expressId;
    @ApiModelProperty(name = "快递公司标识")
    private String expressVendorId;
    @ApiModelProperty(name = "快递公司名称")
    private String expressName;
    @ApiModelProperty(name = "物流渠道 Id")
    private String serviceId;
    @ApiModelProperty(name = "跟踪号")
    private String trackingNumber;
    @ApiModelProperty(name = "包裹⾯单地址")
    private String labelUrl;
    @ApiModelProperty(name = "分区码")
    private String zone;
    @ApiModelProperty(name = "快递公司发送地省")
    private String shipFromProvince;
    @ApiModelProperty(name = "大约到达时间")
    private Date expectedArriveDate;
}
