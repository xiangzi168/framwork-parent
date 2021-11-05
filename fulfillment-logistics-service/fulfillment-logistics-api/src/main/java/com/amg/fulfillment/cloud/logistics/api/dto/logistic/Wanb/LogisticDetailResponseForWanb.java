package com.amg.fulfillment.cloud.logistics.api.dto.logistic.Wanb;

import com.alibaba.fastjson.annotation.JSONField;
import com.amg.fulfillment.cloud.logistics.api.dto.wanb.*;
import com.amg.fulfillment.cloud.logistics.api.enumeration.LogisticItemEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Seraph on 2021/5/28
 */

@Data
public class LogisticDetailResponseForWanb extends AbstractResponseWanb<LogisticDetailResponseForWanb> {

    @JSONField(name = "Data")
    private DetailData data;

    @Data
    public static class DetailData
    {
        @JSONField(name = "AccountNo")
        private String accountNo;       //客户代码
        @JSONField(name = "ReferenceId")
        private String referenceId;       //客户订单号
        @JSONField(name = "ProcessCode")
        private String processCode;       //包裹处理号
        @JSONField(name = "IndexNumber")
        private String indexNumber;       //检索号
        @JSONField(name = "SellingPlatformOrder")
        private SellingPlatformOrder sellingPlatformOrder;       //销售平台订单信息
        @JSONField(name = "ShippingAddress")
        private Address shippingAddress;       //收件人地址信息
        @JSONField(name = "Status")
        private String status;       //包裹状态	        Original: 初始状态       Confirmed: 己确认交运(已预报)       Arrived: 已揽件/到仓       InStock: 已收货/核验       Shipped: 己发货       Transfer: 运输中       Abnormal: 异常       DepartFromPort: 己离港       Cancel: 己取消       Returned: 已退回
        @JSONField(name = "WeightInKg")
        private BigDecimal weightInKg;       //包裹预报重量(单位:KG)
        @JSONField(name = "CheckWeightInKg")
        private BigDecimal checkWeightInKg;       //包裹复核重量(单位:KG)
        @JSONField(name = "ItemDetails")
        private List<ParcelItemDetail> itemDetails;       //包裹件内明细
        @JSONField(name = "Cases")
        private ParcelCase cases;       //一票多件分箱信息
        @JSONField(name = "TotalValue")
        private Money totalValue;       //包裹申报金额
        @JSONField(name = "TotalVolume")
        private CubeSize totalVolume;       //包裹预报尺寸
        @JSONField(name = "CheckTotalVolume")
        private CubeSize checkTotalVolume;       //包裹复核尺寸
        @JSONField(name = "WithBatteryType")
        private LogisticItemEnum.LogisticItemEnumWanB.WithBatteryTypeEnum withBatteryType;       //包裹带电信息          NOBattery  不带电       WithBattery  带电      Battery   纯电池
        @JSONField(name = "Notes")
        private String notes;       //包裹备注
        @JSONField(name = "BatchNo")
        private String batchNo;       //批次
        @JSONField(name = "WarehouseCode")
        private String warehouseCode;       //送货仓库代码
        @JSONField(name = "WarehouseName")
        private String warehouseName;       //送货仓库名称
        @JSONField(name = "ProcessLocation")
        private String processLocation;       //送货仓库名称
        @JSONField(name = "ShippingMethod")
        private ParcelShippingMethod shippingMethod;       //申请的发货方式
        @JSONField(name = "ItemType")
        private LogisticItemEnum.LogisticItemEnumWanB.ParcelItemTypeEnum itemType;       //包裹类型
        @JSONField(name = "DutyPaymentMethod")
        private LogisticItemEnum.LogisticItemEnumWanB.DutyPaymentMethodEmum dutyPaymentMethod;       //关税结算方式     DDP	Delivered Duty Paid，完税后交货      DDU	Delivered Duty Unpaid，未完税交货
        @JSONField(name = "RealShippingMethod")
        private ParcelShippingMethod realShippingMethod;       //实际的发货方式
        @Deprecated
        @JSONField(name = "TrackingNumber")
        private String trackingNumber;       //已废弃，创建包裹时候的导入的跟踪号，或者Relabel前系统分配的跟踪号
        @JSONField(name = "RealTrackingNumber")
        private String realTrackingNumber;       //已废弃，Relabel后的跟踪号
        @JSONField(name = "FinalTrackingNumber")
        private String finalTrackingNumber;       //最终跟踪号 TrackingNoProcessResult.Code 为 Success 时，此属性才会有值。请始终先参考 TrackingNoProcessResult 属性查看跟踪号分配信息 当TrackingNoProcessResult.Code为Success，并且TrackingNoProcessResult.IsVirtual为true 时，说明当前单号为虚拟单号。请先打单发货，待我司操作之后才会分配最终的派送单号。 您需要视平台标记发货方式而定，有选择性地调用本接口来查询包裹真实派送单号。当我司操作货物并且成功获取到真实派送单号之后，TrackingNoProcessResult.Code为Success，TrackingNoProcessResult.IsVirtual为false。 一般只有美国USPS渠道才会出现此情况。
        @JSONField(name = "TrackingNoProcessResult")
        private TrackingNoProcessResult trackingNoProcessResult;       //跟踪号分配信息 Code 为 Success 表示跟踪号分配成功，可通过 FinalTrackingNumber 获取跟踪号。 Code 为 Processing 时，你需要隔一阵子再次查询获取包裹接口以获取跟踪号。 通常会在5分钟内完成。 Code 为 Error 时，请参考 Message 查看具体错误信息
        @JSONField(name = "IsMPS")
        private String isMPS;       //快递一票多件(multiple package shipment)
        @JSONField(name = "MPSType")
        private LogisticItemEnum.LogisticItemEnumWanB.ParcelMPSTypeEnum mPSType;       //快递一票多件类型
        @Deprecated
        @JSONField(name = "Shipper")
        private String shipper;     //已废弃，请参考 ShipperInfo
        @Deprecated
        @JSONField(name = "ShipContactInfo")
        private String shipContactInfo;     //已废弃，请参考 ShipperInfo
        @Deprecated
        @JSONField(name = "ShippedBy")
        private String shippedBy;       //已废弃，请参考 ShipperInfo
        @Deprecated
        @JSONField(name = "ShipVatNo")
        private String shipVatNo;       //已废弃，请参考 ShipperInfo
        @JSONField(name = "ShipperInfo")
        private ParcelShipperInfo shipperInfo;       //发件信息
        @JSONField(name = "IsRemoteArea")
        private String isRemoteArea;       //是否偏远
        @JSONField(name = "CreateOn")
        private String createOn;       //创建时间
        @JSONField(name = "ReceiveOn")
        private String receiveOn;       //收货时间
        @JSONField(name = "ShipOn")
        private String shipOn;       //出库时间
    }

}
