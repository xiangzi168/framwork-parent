package com.amg.fulfillment.cloud.logistics.api.dto.logistic.px4;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Tom
 * @date 2021-04-07-14:21
 */
@ApiModel(value = "创建4PX直发委托订单")
@Data
@Builder
public class LogisticOrderFor4PX {

    @ApiModelProperty(name = "4PX跟踪号（预分配号段的客户可传此值)")
    private String a4px_tracking_no;
    @NotNull
    @ApiModelProperty(name = "参考号（客户自有系统的单号，如客户单号)")
    private String ref_no;
    @ApiModelProperty(name = "面单条码（预分配号段的客户可传此值）")
    private String label_barcode;
    @NotNull
    @ApiModelProperty(name = "业务类型(4PX内部调度所需，如需对接传值将说明，默认值：BDS)")
    private String business_type;
    @NotNull
    @ApiModelProperty(name = "税费费用承担方式(可选值：U、P); U：DDU由收件人支付关税; P：DDP 由寄件方支付关税 （如果物流产品只提供其中一种，则以4PX提供的为准）")
    private String duty_type;
    @ApiModelProperty(name = "货物类型（1：礼品;2：文件;3：商品货样;5：其它；默认值：5）")
    private String cargo_type;
    @ApiModelProperty(name = "VAT税号(数字或字母)；欧盟国家(含英国)使用的增值税号")
    private String vat_no;
    @ApiModelProperty(name = "EORI号码(数字或字母)；欧盟入关时需要EORI号码，用于商品货物的清关")
    private String eori_no;
    @ApiModelProperty(name = "买家ID(数字或字母)")
    private String buyer_id;
    @ApiModelProperty(name = "销售平台（点击查看详情）")
    private String sales_platform;
    @ApiModelProperty(name = "交易号ID(数字或字母)")
    private String trade_id;
    @ApiModelProperty(name = "交易号ID(数字或字母)")
    private String seller_id;
    @ApiModelProperty(name = "能否提供商业发票（Y/N） Y：能提供商业发票(则系统不会生成形式发票)；N：不能提供商业发票(则系统会生成形式发票)； 默认为N")
    private String is_commercial_invoice;
    @ApiModelProperty(name = "包裹件数（一个订单有多少件包裹，就填写多少件数，请如实填写包裹件数，否则DHL无法返回准确的子单号数）")
    private String parcel_qty;
    @ApiModelProperty(name = "运费(客户填写自己估算的运输费用，目前只支持USD)")
    private String freight_charges;
    @ApiModelProperty(name = "物流服务信息")
    private LogisticsServiceInfo logistics_service_info;
    @ApiModelProperty(name = "退件信息")
    private ReturnInfo return_info;
    @NotNull
    @ApiModelProperty(name = "包裹列表")
    private List<Parcel> parcel_list;
    @NotNull
    @ApiModelProperty(name = "是否投保(Y、N)")
    private String is_insure;
    @NotNull
    @ApiModelProperty(name = "保险信息（投保时必须填写）")
    private InsuranceInfo insurance_info;
    @NotNull
    @ApiModelProperty(name = "发件人信息")
    private Sender sender;
    @NotNull
    @ApiModelProperty(name = "收件人信息")
    private RecipientInfo recipient_info;
    @NotNull
    @ApiModelProperty(name = "货物到仓方式信息")
    private DeliverTypeInfo deliver_type_info;
    @NotNull
    @ApiModelProperty(name = "投递信息")
    private DeliverToRecipientInfo deliver_to_recipient_info;

    @Data
    @Builder
    public static class LogisticsServiceInfo {
        @NotNull
        @ApiModelProperty(name = "物流产品代码(点击查看详情)")
        private String logistics_product_code;
        @ApiModelProperty(name = "单独报关（Y：单独报关；N：不单独报关） 默认值：N")
        private String customs_service;
        @ApiModelProperty(name = "签名服务（Y/N)；默认值：N")
        private String signature_service;
        @ApiModelProperty(name = "其他服务（待完善)")
        private String value_added_services;
    }

    @Data
    @Builder
    public static class ReturnInfo {
        @NotNull
        @ApiModelProperty(name = "境内异常处理策略(Y：退件--实际是否支持退件，以及退件策略、费用，参考报价表；N：销毁；U：其他--等待客户指令) 默认值：N；")
        private String is_return_on_domestic;
        @ApiModelProperty(name = "境内退件接收地址信息（退件地址非必填；若填写，则姓名/电话/邮编/国家/城市/详细地址均需填写）")
        private DomesticReturnAddr domestic_return_addr;
        @NotNull
        @ApiModelProperty(name = "境外异常处理策略(Y：退件--实际是否支持退件，以及退件策略、费用，参考报价表；N：销毁；U：其他--等待客户指令) 默认值：N；")
        private String is_return_on_oversea;
        @ApiModelProperty(name = "境外退件接收地址信息（退件地址非必填；若填写，则姓名/电话/邮编/国家/城市/详细地址均需填写")
        private OverseaReturnAddr oversea_return_addr;
    }

    @Data
    @Builder
    public static class DomesticReturnAddr {
        @NotNull
        @ApiModelProperty(name = "名/姓名")
        private String first_name;
        @ApiModelProperty(name = "姓")
        private String last_name;
        @ApiModelProperty(name = "公司名")
        private String company;
        @NotNull
        @ApiModelProperty(name = "电话（必填）")
        private String phone;
        @ApiModelProperty(name = "电话2")
        private String phone2;
        @ApiModelProperty(name = "邮箱")
        private String email;
        @NotNull
        @ApiModelProperty(name = "邮编")
        private String post_code;
        @NotNull
        @ApiModelProperty(name = "国家（国际二字码 标准ISO 3166-2 ）")
        private String country;
        @ApiModelProperty(name = "州/省")
        private String state;
        @NotNull
        @ApiModelProperty(name = "城市")
        private String city;
        @ApiModelProperty(name = "区、县")
        private String district;
        @NotNull
        @ApiModelProperty(name = "街道/详细地址")
        private String street;
        @ApiModelProperty(name = "门牌号")
        private String house_number;
        @NotNull
        @ApiModelProperty(name = "境外异常处理策略(Y：退件--实际是否支持退件，以及退件策略、费用，参考报价表；N：销毁；U：其他--等待客户指令) 默认值：N；")
        private String is_return_on_oversea;
    }

    @Data
    @Builder
    public static class OverseaReturnAddr {
        @NotNull
        @ApiModelProperty(name = "名/姓名")
        private String first_name;
        @ApiModelProperty(name = "姓")
        private String last_name;
        @ApiModelProperty(name = "公司名")
        private String company;
        @NotNull
        @ApiModelProperty(name = "电话（必填）")
        private String phone;
        @ApiModelProperty(name = "电话2")
        private String phone2;
        @ApiModelProperty(name = "邮箱")
        private String email;
        @NotNull
        @ApiModelProperty(name = "邮编")
        private String post_code;
        @NotNull
        @ApiModelProperty(name = "国家（国际二字码 标准ISO 3166-2 ）")
        private String country;
        @ApiModelProperty(name = "州/省")
        private String state;
        @NotNull
        @ApiModelProperty(name = "城市")
        private String city;
        @ApiModelProperty(name = "区、县")
        private String district;
        @NotNull
        @ApiModelProperty(name = "街道/详细地址")
        private String street;
        @ApiModelProperty(name = "门牌号")
        private String house_number;
    }

    @Data
    @Builder
    public static class Parcel {
        @NotNull
        @ApiModelProperty(name = "预报重量（g）")
        private BigDecimal weight;
        @ApiModelProperty(name = "包裹长（cm）")
        private BigDecimal length;
        @ApiModelProperty(name = "包裹宽（cm）")
        private BigDecimal width;
        @ApiModelProperty(name = "包裹高（cm）")
        private BigDecimal height;
        @NotNull
        @ApiModelProperty(name = "包裹申报价值（最多4位小数）")
        private BigDecimal parcel_value;
        @NotNull
        @ApiModelProperty(name = "币别（按照ISO标准三字码，目前只支持USD）")
        private String currency;
        @NotNull
        @ApiModelProperty(name = "是否含电池（Y/N）")
        private String include_battery;
        @ApiModelProperty(name = "带电类型(967:内置电池PI967；966:配套电池PI966）")
        private String battery_type;
        @NotNull
        @ApiModelProperty(name = "投保物品信息")
        private List<Product> product_list;
        @NotNull
        @ApiModelProperty(name = "海关申报信息")
        private List<DeclareProductInfo> declare_product_info;
    }

    @Data
    @Builder
    public static class Product {
        @ApiModelProperty(name = "SKU（客户自定义SKUcode）（数字或字母或空格）")
        private String sku_code;
        @ApiModelProperty(name = "商品标准条码（UPC、EAN、JAN…）")
        private String standard_product_barcode;
        @NonNull
        @ApiModelProperty(name = "商品名称")
        private String product_name;
        @NonNull
        @ApiModelProperty(name = "商品描述")
        private String product_description;
        @NonNull
        @ApiModelProperty(name = "商品单价（按对应币别的法定单位，最多4位小数点）")
        private BigDecimal product_unit_price;
        @NonNull
        @ApiModelProperty(name = "币别（按照ISO标准三字码，目前只支持USD）")
        private String currency;
        @NonNull
        @ApiModelProperty(name = "数量（单位为pcs）")
        private Integer qty;
    }

    @Data
    @Builder
    public static class DeclareProductInfo {
        @ApiModelProperty(name = "申报产品代码（在4PX已备案申报产品的代码")
        private String declare_product_code;
        @ApiModelProperty(name = "申报品名(当地语言)")
        private String declare_product_name_cn;
        @ApiModelProperty(name = "申报品名（英语）")
        private String declare_product_name_en;
        @ApiModelProperty(name = "用途")
        private String uses;
        @ApiModelProperty(name = "规格")
        private String specification;
        @ApiModelProperty(name = "成分")
        private String component;
        @ApiModelProperty(name = "单件商品净重（默认以g为单位）")
        private String unit_net_weight;
        @ApiModelProperty(name = "单件商品毛重（默认以g为单位）")
        private String unit_gross_weight;
        @ApiModelProperty(name = "材质")
        private String material;
        @NotNull
        @ApiModelProperty(name = "申报数量")
        private Integer declare_product_code_qty;
        @ApiModelProperty(name = "单位（点击查看详情；默认值：PCS）")
        private String unit_declare_product;
        @ApiModelProperty(name = "原产地（ISO标准2字码）点击查看详情")
        private String origin_country;
        @ApiModelProperty(name = "出口国（ISO标准2字码）点击查看详情")
        private String country_export;
        @ApiModelProperty(name = "进口国（ISO标准2字码）点击查看详情")
        private String country_import;
        @ApiModelProperty(name = "出口国海关编码(只支持数字)")
        private String hscode_export;
        @ApiModelProperty(name = "进口国海关编码(只支持数字)")
        private String hscode_import;
        @NotNull
        @ApiModelProperty(name = "出口国申报单价（按对应币别的法定单位，最多4位小数点）")
        private BigDecimal declare_unit_price_export;
        @NotNull
        @ApiModelProperty(name = "币别（按照ISO标准，目前只支持USD）点击查看详情")
        private String currency_export;
        @NotNull
        @ApiModelProperty(name = "进口国申报单价（按对应币别的法定单位，最多4位小数点）")
        private BigDecimal declare_unit_price_import;
        @NotNull
        @ApiModelProperty(name = "币别（按照ISO标准，目前只支持USD）")
        private String currency_import;
        @NotNull
        @ApiModelProperty(name = "出口国品牌(必填；若无，填none即可)")
        private String brand_export;
        @NotNull
        @ApiModelProperty(name = "进口国品牌(必填；若无，填none即可)")
        private String brand_import;
        @ApiModelProperty(name = "商品销售URL")
        private String sales_url;
        @ApiModelProperty(name = "配货字段（打印标签选择显示配货信息是将会显示：package_remarks*qty")
        private String package_remarks;
    }

    @Data
    @Builder
    public static class InsuranceInfo {
        @ApiModelProperty(name = "保险类型（XY:4PX保价；XP:第三方保险） 5Y, 5元每票 8Y, 8元每票 6P, 0.6%保费")
        private String insure_type;
        @ApiModelProperty(name = "保险价值")
        private String insure_value;
        @ApiModelProperty(name = "币别（按照ISO标准，目前只支持USD）")
        private String currency;
        @ApiModelProperty(name = "投保人/公司")
        private String insure_person;
        @ApiModelProperty(name = "投保人证件类型（暂时只支持身份证，类型为：ID）")
        private String certificate_type;
        @ApiModelProperty(name = "投保人证件号码")
        private String certificate_no;
        @ApiModelProperty(name = "类目ID（保险的类目，暂时不填，默认取第一个类目）")
        private String category_code;
        @ApiModelProperty(name = "货物名称")
        private String insure_product_name;
        @ApiModelProperty(name = "包装及数量")
        private String package_qty;
    }

    @Data
    @Builder
    public static class Sender {
        @NotNull
        @ApiModelProperty(name = "名/姓名")
        private String first_name;
        @ApiModelProperty(name = "姓")
        private String last_name;
        @ApiModelProperty(name = "公司名")
        private String company;
        @NotNull
        @ApiModelProperty(name = "电话（必填）")
        private String phone;
        @ApiModelProperty(name = "电话2")
        private String phone2;
        @ApiModelProperty(name = "邮箱")
        private String email;
        @NotNull
        @ApiModelProperty(name = "邮编")
        private String post_code;
        @NotNull
        @ApiModelProperty(name = "国家（国际二字码 标准ISO 3166-2 ）")
        private String country;
        @ApiModelProperty(name = "州/省")
        private String state;
        @NotNull
        @ApiModelProperty(name = "城市")
        private String city;
        @ApiModelProperty(name = "区、县")
        private String district;
        @NotNull
        @ApiModelProperty(name = "街道/详细地址")
        private String street;
        @ApiModelProperty(name = "门牌号")
        private String house_number;
        @ApiModelProperty(name = "证件信息，根据海关要求变化")
        private CertificateInfo certificate_info;

    }

    @Data
    @Builder
    public static class CertificateInfo {
        @ApiModelProperty(name = "证件类型（点击查看详情）")
        private String id_type;
          @ApiModelProperty(name = "证件号")
        private String id_no;
          @ApiModelProperty(name = "证件正面照URL")
        private String id_front_url;
          @ApiModelProperty(name = "证件背面照URL")
        private String id_back_url;
    }

    @Data
    @Builder
    public static class RecipientInfo {
        @NotNull
        @ApiModelProperty(name = "名/姓名")
        private String first_name;
        @ApiModelProperty(name = "姓")
        private String last_name;
        @ApiModelProperty(name = "公司名")
        private String company;
        @NotNull
        @ApiModelProperty(name = "电话（必填）")
        private String phone;
        @ApiModelProperty(name = "电话2")
        private String phone2;
        @ApiModelProperty(name = "邮箱")
        private String email;
        @NotNull
        @ApiModelProperty(name = "邮编")
        private String post_code;
        @NotNull
        @ApiModelProperty(name = "国家（国际二字码 标准ISO 3166-2 ）")
        private String country;
        @ApiModelProperty(name = "州/省")
        private String state;
        @NotNull
        @ApiModelProperty(name = "城市")
        private String city;
        @ApiModelProperty(name = "区、县")
        private String district;
        @NotNull
        @ApiModelProperty(name = "街道/详细地址")
        private String street;
        @ApiModelProperty(name = "门牌号")
        private String house_number;
        @ApiModelProperty(name = "证件信息，根据海关要求变化")
        private CertificateInfo certificate_info;
    }

    @Data
    @Builder
    public static class DeliverTypeInfo {
        @ApiModelProperty(name = "到仓方式（1:上门揽收；2:快递到仓；3:自送到仓；5:自送门店）")
        private String deliver_type;
        @ApiModelProperty(name = "收货仓库/门店代码（仓库代码）")
        private String warehouse_code;
        @ApiModelProperty(name = "上门揽收信息")
        private PickUpInfo pick_up_info;
        @ApiModelProperty(name = "快递到仓信息")
        private ExpressTo4pxInfo express_to_4px_info;
        @ApiModelProperty(name = "自己送仓信息")
        private SelfSendTo4pxInfo self_send_to_4px_info;
    }

    @Data
    @Builder
    public static class PickUpInfo {
        @ApiModelProperty(name = "期望提货最迟时间（*注：时间格式的传入值需要转换为long类型格式。）")
        private String expect_pick_up_earliest_time;
        @ApiModelProperty(name = "期望提货最迟时间（*注：时间格式的传入值需要转换为long类型格式。）")
        private String expect_pick_up_latest_time;
        @ApiModelProperty(name = "收货地址")
        private PickUpAddressInfo pick_up_address_info;
    }

    @Data
    @Builder
    public static class PickUpAddressInfo {
        @NotNull
        @ApiModelProperty(name = "名/姓名")
        private String first_name;
        @ApiModelProperty(name = "姓")
        private String last_name;
        @ApiModelProperty(name = "公司名")
        private String company;
        @NotNull
        @ApiModelProperty(name = "电话（必填）")
        private String phone;
        @ApiModelProperty(name = "电话2")
        private String phone2;
        @ApiModelProperty(name = "邮箱")
        private String email;
        @NotNull
        @ApiModelProperty(name = "邮编")
        private String post_code;
        @NotNull
        @ApiModelProperty(name = "国家（国际二字码 标准ISO 3166-2 ）")
        private String country;
        @ApiModelProperty(name = "州/省")
        private String state;
        @NotNull
        @ApiModelProperty(name = "城市")
        private String city;
        @ApiModelProperty(name = "区、县")
        private String district;
        @NotNull
        @ApiModelProperty(name = "街道/详细地址")
        private String street;
        @ApiModelProperty(name = "门牌号")
        private String house_number;
    }

    @Data
    @Builder
    public static class ExpressTo4pxInfo {
        @ApiModelProperty(name = "快递公司")
        private String express_company;
        @ApiModelProperty(name = "追踪号")
        private String tracking_no;
    }

    @Data
    @Builder
    public static class SelfSendTo4pxInfo {
        @ApiModelProperty(name = "预约送仓最早时间（*注：时间格式的传入值需要转换为long类型格式。）")
        private String booking_earliest_time;
        @ApiModelProperty(name = "预约送仓最晚时间（*注：时间格式的传入值需要转换为long类型格式。）")
        private String booking_latest_time;
    }

    @Data
    @Builder
    public static class DeliverToRecipientInfo {
        @ApiModelProperty(name = "投递类型：HOME_DELIVERY-投递到门；SELF_PICKUP_STATION-投递门店（自提点）；SELF_SERVICE_STATION-投递自提柜(自助点）；默认：HOME_DELIVERY；注：目前暂时不支持投递门店、投递自提柜")
        private String deliver_type;
        @ApiModelProperty(name = "自提门店/自提点的信息(选择自提时必传，点击获取详情)")
        private String station_code;

    }
}
