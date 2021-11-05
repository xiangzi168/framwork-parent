package com.amg.fulfillment.cloud.logistics.api.dto.logistic.yuntu;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Tom
 * @date 2021-04-16-12:13
 */
@ApiModel("万邦物流轨迹响应对象")
@Data
@Builder
public class LogisticOrderForYunTu {
//    @NonNull
    @ApiModelProperty(name = "客户订单号,不能重复")
    private String CustomerOrderNumber;
//    @NonNull
    @ApiModelProperty(name = "运输方式代码")
    private String ShippingMethodCode;
    @ApiModelProperty(name = "包裹跟踪号，可以不填写")
    private String TrackingNumber;
    @ApiModelProperty(name = "平台交易号（wish邮")
    private String TransactionNumber;
    @ApiModelProperty(name = "增值税号，巴西国家必填 CPF 或 CNPJ， CPF 码格式为 000.000.000-00,CNPJ 码格式 为 00.000.000/0000-00，其它国家非必填， 英国税号格式为：前缀GB+9位纯数字 或者 前缀GB+12位纯数字")
    private String TaxNumber;
    @ApiModelProperty(name = "预估包裹单边长，单位cm，非必填，默认1")
    private Double Length;
    @ApiModelProperty(name = "预估包裹单边宽，单位cm，非必填，默认1")
    private Double Width;
    @ApiModelProperty(name = "预估包裹单边高，单位cm，非必填，默认1")
    private Double Height;
//    @NonNull
    @ApiModelProperty(name = "运单包裹的件数，必须大于0的整数")
    private Integer PackageCount;
//    @NonNull
    @ApiModelProperty(name = "预估包裹总重量，单位kg,最多3位小数")
    private BigDecimal Weight;
//    @NonNull
    @ApiModelProperty(name = "收件人信息")
    private AddressForYuntu Receiver;
    @ApiModelProperty(name = "发件人信息")
    private AddressForYuntu Sender;
    @ApiModelProperty(name = "申报类型,用于打印CN22，1-Gift,2-Sameple,3-Documents,4-Others,默认4-Other")
    private int ApplicationType;
    @ApiModelProperty(name = "是否退回,包裹无人签收时是否退回，1-退回，0-不退回，默认0 ")
    private Integer ReturnOption;
    @ApiModelProperty(name = "关税预付服务费，1-参加关税预付，0-不参加关税预付，默认0 (渠道需开通关税预付服务)")
    private Integer TariffPrepay;
    @ApiModelProperty(name = "包裹投保类型，0-不参保，1-按件，2-按比例，默认0，表示不参加运输保险，具体参考包裹运输")
    private Integer InsuranceOption;
    @ApiModelProperty(name = "保险的最高额度，单位RMB ")
    private BigDecimal Coverage;
    @ApiModelProperty(name = "包裹中特殊货品类型，可调用货品类型查询服务查询，可以不填写，表示普通货品")
    private Integer SensitiveTypeID;
    @ApiModelProperty(name = "申报信息")
    private List<Parcel> Parcels;
    @ApiModelProperty(name = "订单来源代码")
    private String SourceCode;
    @ApiModelProperty(name = "箱子明细信息，FBA订单必填")
    private List<ChildOrder> ChildOrders;
    @ApiModelProperty(name = "云途备案识别码或oss号")
    private String lossCode;
    @ApiModelProperty(name = "附加服务")
    private List<OrderExtra> OrderExtra;
    @Data
    @Builder
    public static class OrderExtra{
        @ApiModelProperty(name = "额外服务代码")
        private  String ExtraCode;
        @ApiModelProperty(name = "额外服务名称")
        private  String ExtraName;
    }
    @Data
    @Builder
    public static class AddressForYuntu {
        @ApiModelProperty(name = "即为该LogisticOrderForYunTu字段TaxNumber（增值税号）相同")
        private String  TaxId;
//        @NonNull
        @ApiModelProperty(name = "收件人所在国家，填写国际通用标准2位简码，可通过国家查询服务查询\n")
        private String  CountryCode;
//        @NonNull
        @ApiModelProperty(name = "收件人姓")
        private String  FirstName;
        @ApiModelProperty(name = "收件人名字")
        private String  LastName;
        @ApiModelProperty(name = "收件人公司名称")
        private String  Company;
//        @NonNull
        @ApiModelProperty(name = "收件人详细地址")
        private String  Street;
        @ApiModelProperty(name = "收件人详细地址1")
        private String  StreetAddress1;
        @ApiModelProperty(name = "收件人详细地址2")
        private String  StreetAddress2;
//        @NonNull
        @ApiModelProperty(name = "收件人所在城市")
        private String  City;
        @ApiModelProperty(name = "收件人省/州")
        private String  State;
        @ApiModelProperty(name = "收件人邮编")
        private String  Zip;
        @ApiModelProperty(name = "收件人电话")
        private String  Phone;
        @ApiModelProperty(name = "门牌号")
        private String  HouseNumber;
        @ApiModelProperty(name = "收件人电子邮箱")
        private String Email;
        @ApiModelProperty(name = "收件人手机号")
        private String MobileNumber;
        @ApiModelProperty(name = "收件人ID")
        private String CertificateCode;

    }

    @Data
    @Builder
    public static class Parcel {
//        @NonNull
        @ApiModelProperty(name = "包裹申报名称(英文)必填")
        private String  EName;
        @ApiModelProperty(name = "包裹申报名称(中文)")
        private String  CName;
        @ApiModelProperty(name = "海关编码")
        private String  HSCode;
//        @NonNull
        @ApiModelProperty(name = "申报数量,必填")
        private Integer  Quantity;
//        @NonNull
        @ApiModelProperty(name = "申报价格(单价),单位USD,必填")
        private BigDecimal  UnitPrice;
//        @NonNull
        @ApiModelProperty(name = "申报重量(单重)，单位kg, ")
        private BigDecimal  UnitWeight;
        @ApiModelProperty(name = "订单备注，用于打印配货单")
        private String  Remark;
        @ApiModelProperty(name = "产品销售链接地址 ")
        private String  ProductUrl;
        @ApiModelProperty(name = "用于填写商品SKU，FBA订单必填")
        private String  SKU;
        @ApiModelProperty(name = "配货信息")
        private String  InvoiceRemark;
//        @NonNull
        @ApiModelProperty(name = "申报币种，默认：USD")
        private String CurrencyCode;
    }

    @Data
    @Builder
    public static class ChildOrder {
        @ApiModelProperty(name = "箱子编号，FBA订单必填")
        private String BoxNumber ;
         @ApiModelProperty(name = "预估包裹单边长，单位cm，默认1，FBA订单必填")
        private Integer  Length;
         @ApiModelProperty(name = "预估包裹单边宽，单位cm，默认1，FBA订单必填")
        private Integer  Width;
         @ApiModelProperty(name = "估包裹单边高，单位cm，默认1，FBA订单必填")
        private Integer  Height;
         @ApiModelProperty(name = "预估包裹总重量，单位kg,最多3位小数，FBA订单必填")
        private Integer  BoxWeight;
         @ApiModelProperty(name = "单箱SKU信息，FBA订单必填")
        private List<ChildDetail>  ChildDetails;
    }

    @Data
    @Builder
    public static class ChildDetail {
        @ApiModelProperty(name = "用于填写商品SKU，FBA订单必填")
        private String SKU;
        @ApiModelProperty(name = "申报数量，FBA订单必填")
        private Integer Quantity;
    }
}



