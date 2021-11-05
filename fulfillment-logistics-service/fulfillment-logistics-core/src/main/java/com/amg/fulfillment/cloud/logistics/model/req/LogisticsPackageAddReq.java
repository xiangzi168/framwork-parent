package com.amg.fulfillment.cloud.logistics.model.req;

import com.amg.fulfillment.cloud.logistics.entity.LogisticsPackageItemDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@ApiModel("包裹新建对象")
public class LogisticsPackageAddReq {

    public interface Add {
    }

    public interface Update {
    }

    private Long id;
//    @NotNull(message = "采购渠道 不能为null", groups = {LogisticsPackageAddReq.Update.class})
//    @ApiModelProperty(value = "采购渠道  1.1688  2.AE  3.备货，添加到包裹采购渠道必填")
//    private Integer purchaseChannel;
    @NotNull(message = "关联销售订单 不能为null", groups = {LogisticsPackageAddReq.Update.class})
    @ApiModelProperty(value = "关联销售订单，添加到包裹必填")
    private String salesOrderId;
    @NotNull(message = "logisticsPackageItems不能为null", groups = {LogisticsPackageAddReq.Update.class})
    @Size(min = 1,message = "logisticsPackageItems至少有一项")
    @ApiModelProperty(value = "包裹详情，添加到包裹必填")
    private List<LogisticsPackageItemReq> logisticsPackageItems;
//    @NotNull(message = "关联渠道订单 不能为null", groups = {LogisticsPackageAddReq.Update.class})
//    @ApiModelProperty(value = "关联渠道订单，添加到包裹必填")
    private String channelOrderId;
//    @ApiModelProperty(value = "发货订单id")
//    private String dispatchOrderId;
    @NotBlank(message = "logisticsName不能为null", groups = {LogisticsPackageAddReq.Add.class})
    @ApiModelProperty(value = "物流名称，新建包裹必填")
    private String logisticsName;
    @NotBlank(message = "logisticsCode不能为null", groups = { LogisticsPackageAddReq.Add.class})
    @ApiModelProperty(value = "物流编号，新建包裹必填")
    private String logisticsCode;
    @NotBlank(message = "logisticsChannel不能为null", groups = {LogisticsPackageAddReq.Add.class})
    @ApiModelProperty(value = "物流渠道，新建包裹必填")
    private String logisticsChannel;
    @NotBlank(message = "logisticsOrderNo包裹单号不能为null", groups = {LogisticsPackageAddReq.Update.class})
    @ApiModelProperty(value = "包裹单号，添加到包裹必填")
    private String logisticsOrderNo;
//    @ApiModelProperty(value = "物流跟踪单号")
//    private String logisticsTrackingCode;
//    @ApiModelProperty(value = "物流包裹处理号")
//    private String logisticsProcessCode;
//    @ApiModelProperty(value = "物流包裹检索号")
//    private String logisticsIdnexNumber;
    @NotBlank(message = "logisticsWayBillNo不能为null", groups = {LogisticsPackageAddReq.Add.class})
    @ApiModelProperty(value = "物流商单号，新建包裹必填")
    private String logisticsWayBillNo;
//    @ApiModelProperty(value = "物流收货国家")
//    private String logisticsArea;
//    @ApiModelProperty(value = "物流费用")
//    private BigDecimal logisticsCost;
//    @ApiModelProperty(value = "物流费用币种")
//    private String logisticsCostCurrency;
//    @ApiModelProperty(value = "物流单重量")
//    private BigDecimal logisticsWeight;
//    @ApiModelProperty(value = "是否有效  0 否  1 是")
//    private Integer isValid;
//    @ApiModelProperty(value = "是否删除  0 否   1 是")
//    private Integer isDeleted;
//    @ApiModelProperty(value = "最后修改时间")
//    private Date updateTime;
//    @ApiModelProperty(value = "最后修改人")
//    private String updateBy;

}
