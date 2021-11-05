package com.amg.fulfillment.cloud.logistics.model.req;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.amg.fulfillment.cloud.logistics.api.enumeration.DeliveryPackageTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
@Data
@ApiModel(value = "LogisticsTrackingCodeUpdateImportReq")
public class LogisticsTrackingCodeUpdateImportReq implements Serializable {

    public static class AEImport{public static final Integer VALUE = DeliveryPackageTypeEnum.ABROAD_DELIVERY_PACKAGE.getType();}
    public static class AboardImport{public static final Integer VALUE = DeliveryPackageTypeEnum.AE_DELIVERY_PACKAGE.getType();}
    private Long id;
//    @NotBlank(message = "销售订单不能为空")
//    @ApiModelProperty(value = "销售订单",required = true)
//    @Excel(name = "销售订单（必填）",orderNum = "0")
//    private String salesOrderId;
    @NotBlank(message = "渠道订单号不能为空", groups = {AboardImport.class})
    @ApiModelProperty(value = "渠道订单号",required = true)
    @Excel(name = "渠道订单号（必填）",orderNum = "0")
    private String channelOrderId;
    @NotBlank(message = "旧的物流轨迹号不能为空",groups ={AEImport.class,AboardImport.class})
    @ApiModelProperty(value = "旧的物流轨迹号",required = true)
    @Excel(name = "旧的物流轨迹号（必填）",orderNum = "1")
    private String oldLogisticsTrackingCode;
    @NotBlank(message = "新的物流轨迹号不能为空",groups ={AEImport.class,AboardImport.class})
    @ApiModelProperty(value = "新的物流轨迹号",required = true)
    @Excel(name = "新的物流轨迹号（必填）",orderNum = "2")
    private String newLogisticsTrackingCode;
    @Excel(name = "异常原因",orderNum = "3")
    private String errorMsg;
}
