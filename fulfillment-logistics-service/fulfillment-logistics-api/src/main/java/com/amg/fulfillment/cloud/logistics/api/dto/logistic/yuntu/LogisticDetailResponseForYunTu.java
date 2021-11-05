package com.amg.fulfillment.cloud.logistics.api.dto.logistic.yuntu;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Seraph on 2021/5/28
 */

@Data
public class LogisticDetailResponseForYunTu extends AbstractResponseForYunTu<LogisticDetailResponseForYunTu> {

    @JSONField(name = "Item")
    private WaybillOrder item;      //成功的订单信息

    @Data
    public static class WaybillOrder
    {
        @JSONField(name = "WayBillNumber")
        private String wayBillNumber;       //物流系统运单号
        @JSONField(name = "OrderNumber")
        private String orderNumber;     //客户订单号
        @JSONField(name = "ShippingMethodCode")
        private String shippingMethodCode;      //发货的方式
        @JSONField(name = "TrackingNumber")
        private String trackingNumber;      //包裹跟踪号
        @JSONField(name = "TransactionNumber")
        private String transactionNumber;       //平台交易号
        @JSONField(name = "Length")
        private Integer length;       //预估包裹单边长，单位 cm
        @JSONField(name = "Width")
        private Integer width;       //预估包裹单边宽，单位 cm
        @JSONField(name = "Height")
        private Integer height;       //预估包裹单边高，单位 cm
        @JSONField(name = "Status")
        private Integer status;       //订单状态:3-已提交，4-已收货，5-发货运输中，6-已删除， 7-已退回，8-待转单，9-退货在仓，10-已理赔，11-已签收
        @JSONField(name = "PackageNumber")
        private Integer packageNumber;       //运单的包裹件数
        @JSONField(name = "Weight")
        private BigDecimal weight;       //预估包裹总重量，单位 kg
        @JSONField(name = "Receiver")
        private Receiver receiver;       //收件人信息
        @JSONField(name = "Sender")
        private Sender sender;       //发件人信息
        @JSONField(name = "ApplicationType")
        private Integer applicationType;       //申报类型
        @JSONField(name = "ReturnOption")
        private Boolean returnOption;       //是否退回,包裹无人签收时是否退回，true-退回，false-不退回
        @JSONField(name = "InsuranceType")
        private Integer insuranceType;       //包裹投保类型，0-不参保，1-按件，2-按比例
        @JSONField(name = "InsureAmount")
        private BigDecimal insureAmount;       //保险的最高额度，单位 RMB
        @JSONField(name = "SensitiveTypeID")
        private Integer sensitiveTypeID;       //包裹中特殊货品类型
        @JSONField(name = "Parcels")
        private List<Parcels> parcels;       //申报信息
        @JSONField(name = "ChargeWeight")
        private BigDecimal chargeWeight;       //计费重量

    }

    @Data
    public static class Receiver
    {
        @JSONField(name = "TaxId")
        private String taxId;       //收件人企业税号
        @JSONField(name = "CountryCode")
        private String countryCode;       //收件人所在国家简码
        @JSONField(name = "FirstName")
        private String firstName;       //收件人姓
        @JSONField(name = "LastName")
        private String lastName;       //收件人名字
        @JSONField(name = "Company")
        private String company;       //收件人公司名称
        @JSONField(name = "Street")
        private String street;       //收件人详情地址
        @JSONField(name = "StreetAddress1")
        private String streetAddress1;       //收件人详细地址 1
        @JSONField(name = "StreetAddress2")
        private String streetAddress2;       //收件人详细地址 2
        @JSONField(name = "City")
        private String city;       //收件人所在城市
        @JSONField(name = "State")
        private String state;       //收货人省/州
        @JSONField(name = "Zip")
        private String zip;       //收货人邮编
        @JSONField(name = "Phone")
        private String phone;       //收货人电话
        @JSONField(name = "HouseNumber")
        private String houseNumber;       //收件人地址门牌号
    }

    @Data
    public static class Sender
    {
        @JSONField(name = "CountryCode")
        private String countryCode;       //发件人所在国家简码
        @JSONField(name = "FirstName")
        private String firstName;       //发件人姓
        @JSONField(name = "LastName")
        private String lastName;       //发件人名字
        @JSONField(name = "Company")
        private String company;       //发件人公司名称
        @JSONField(name = "Street")
        private String street;       //发件人详情地址
        @JSONField(name = "City")
        private String city;       //发件人所在城市
        @JSONField(name = "State")
        private String state;       //发货人省/州
        @JSONField(name = "Zip")
        private String zip;       //发货人邮编
        @JSONField(name = "Phone")
        private String phone;       //发货人电话
    }

    @Data
    public static class Parcels
    {
        @JSONField(name = "Sku")
        private String sku;       //包裹中货品 商品 SKU
        @JSONField(name = "EName")
        private String eName;       //包裹中货品 申报名称(英文)
        @JSONField(name = "CName")
        private String cName;       //包裹中货品 申报名称(中文)
        @JSONField(name = "HSCode")
        private String hSCode;       //包裹中货品 申报编码
        @JSONField(name = "Quantity")
        private Integer quantity;       //包裹中货品 申报数量
        @JSONField(name = "UnitPrice")
        private BigDecimal unitPrice;       //包裹中货品 申报价格
        @JSONField(name = "UnitWeight")
        private BigDecimal unitWeight;       //包裹中货品 申报重量
        @JSONField(name = "Remark")
        private String remark;       //材质，用途等备注
        @JSONField(name = "ProductUrl")
        private String productUrl;       //产品销售链接地址
        @JSONField(name = "InvoiceRemark")
        private String invoiceRemark;       //配货信息
        @JSONField(name = "CurrencyCode")
        private String currencyCode;       //申报币种
        @JSONField(name = "ClassCode")
        private String classCode;       //品类编码
        @JSONField(name = "Brand")
        private String brand;       //品牌
        @JSONField(name = "ModelType")
        private String modelType;       //型号规格
        @JSONField(name = "Unit")
        private String unit;       //单位
    }
}
