package com.amg.fulfillment.cloud.logistics.dto.logistic;

import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.dto.depository.AddressDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author Tom
 * @date 2021-04-19-18:23
 */
@ApiModel("物流下单订单类")
@Data
public class LogisticOrderDto {
    @NotBlank(message = "物流商标识号不能为null")
    @ApiModelProperty(value = "物流商标识号")
    private String logisticCode;        //WB、YUNTU、YANWEN、PX4
    @ApiModelProperty(value = "订单号")
    private String logisticOrderNo;
    @ApiModelProperty(value = "销售订单号")
    private String salesOrderId;
    @ApiModelProperty(value = "渠道订单号")
    private List<String> channelOrderIdList;
    @Valid
    @NotNull(message = "收件地址不能为null")
    @ApiModelProperty(value = "收件地址")
    private AddressDto receiverAddress;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "包裹类型 包裹类型 DOC:文件 SPX:包裹")
    private String packageType = "SPX";
    @NotNull(message = "waybillGoodDetailDtos 运单商品详情不能是null")
    @Size(min = 1, message = "waybillGoodDetailDtos 运单商品详情至少有一项")
    @ApiModelProperty(value = "运单商品详情")
    private List<WaybillGoodDetailDto> waybillGoodDetailDtos;
    @ApiModelProperty(value = "是否带电池 1：是 2：否 ")
    private Integer battery = Constant.NO;
    @ApiModelProperty(value = "电池类型  1:不带电  2:带电 3:纯电池")
    private Integer batteryType = 1;
    @ApiModelProperty(value = "发货渠道")
    private String channel;
    private Long logisticsPackageId;
    private Boolean createPackageFlag = false;      //发货仅制单
    @ApiModelProperty(value = "操作人id")
    private Long operationId;
    @ApiModelProperty(value = "操作人")
    private String operationer;
    @ApiModelProperty(value = "操作时间")
    private String operationTime;
    @ApiModelProperty(value = "仓库code")
    private Integer storageCode;
}
