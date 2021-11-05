package com.amg.fulfillment.cloud.logistics.api.dto.logistic.Wanb;

import cn.hutool.core.util.StrUtil;
import com.amg.fulfillment.cloud.logistics.api.dto.depository.Address;
import com.amg.fulfillment.cloud.logistics.api.dto.wanb.*;
import com.amg.fulfillment.cloud.logistics.api.exception.ExceptionConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Tom
 * @date 2021-04-15-20:41
 */
@ApiModel(value = "万邦物流单")
@Data
@Builder
public class LogisticOrderForWanb {
    @NonNull
    @ApiModelProperty(name = "客户订单号，在整个系统内不得重复。")
    private String ReferenceId;
    @ApiModelProperty(name = "销售平台订单信息")
    private SellingPlatformOrder SellingPlatformOrder;
    @NonNull
    @ApiModelProperty(name = "收件人地址信息")
    private Address ShippingAddress;
    @NonNull
    @ApiModelProperty(name = "包裹重量(单位:KG)")
    private BigDecimal WeightInKg;
    @NonNull
    @ApiModelProperty(name = "包裹件内明细")
    private List<ParcelItemDetail> ItemDetails;
    @NonNull
    @ApiModelProperty(name = "包裹总金额")
    private Money TotalValue;
    @NonNull
    @ApiModelProperty(name = "包裹尺寸")
    private CubeSize TotalVolume;
    @NonNull
    @ApiModelProperty(name = "包裹是否含有带电产品: NOBattery:不带电  WithBattery:带电 Battery:纯电池")
    private String  WithBatteryType;
    @ApiModelProperty(name = "包裹备注")
    private String Notes;
    @ApiModelProperty(name = "批次号或邮袋号")
    private String BatchNo;
    @NonNull
    @ApiModelProperty(name = "交货仓库代码")
    private String WarehouseCode;
    @NonNull
    @ApiModelProperty(name = "发货产品服务代码")
    private String ShippingMethod;
    @NonNull
    @ApiModelProperty(name = "包裹类型 DOC:文件 SPX:包裹")
    private String ItemType;
    @ApiModelProperty(name = "订单交易类型:B2B, B2C，默认为 B2C")
    private String TradeType;
    @ApiModelProperty(name = "预分配挂号")
    private String TrackingNumber;
    @ApiModelProperty(name = "快递一票多件(multiple package shipment)")
    private Boolean IsMPS;
    @ApiModelProperty(name = "快递一票多件类型 Normal:NormalFBA  FBA:亚马逊FBA")
    private String MPSType;
    @ApiModelProperty(name = "快递一票多件,箱子列表")
    private List<ParcelCaseInfo> Cases;
    @ApiModelProperty(name = "是否允许偏远区域下单，默认值为 true\n" +
            "如果设置值为false，并且地址属于偏远区域，此接口则会返回代码为 0x10008E 的错误")
    private Boolean AllowRemoteArea;
    @ApiModelProperty(name = "自动确认交运包裹，或此值为true，则无须再调用确认交运包裹接口")
    private Boolean AutoConfirm;
    @ApiModelProperty(name = "发货人信息   代理客户必须填写发货人英文姓名、英文地址以及联系方式\n" +
            "英国脱欧后，进入英国货物需要提供 VatNo\n" +
            "B2B类型或者B2C的高价值包裹必须填写 EORI")
    private ParcelShipperInfo ShipperInfo;

    public void setShippingAddress(Address shippingAddress) {
        checkAddress(shippingAddress);
        ShippingAddress = shippingAddress;
    }


    private void checkAddress(Address shippingAddress) {
        if (StrUtil.isBlank(shippingAddress.getStreet1())) {
            throw new RuntimeException(ExceptionConstant.ADDRESS);
        }
        if (StrUtil.isBlank(shippingAddress.getCity())) {
            throw new RuntimeException(ExceptionConstant.CITY);
        }
        if (StrUtil.isBlank(shippingAddress.getProvince())) {
            throw new RuntimeException(ExceptionConstant.PROVINCE);
        }
        if (StrUtil.isBlank(shippingAddress.getPostCode())) {
            throw new RuntimeException(ExceptionConstant.POSTCODE);
        }
        if (StrUtil.isBlank(shippingAddress.getContacter())) {
            throw new RuntimeException(ExceptionConstant.CONTACTER);
        }
        if (StrUtil.isBlank(shippingAddress.getContacter())) {
            throw new RuntimeException(ExceptionConstant.COUNTRYCODE);
        }
    }
}
