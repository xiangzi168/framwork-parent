package com.amg.fulfillment.cloud.logistics.api.dto.logistic.yanwen;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;


/**
 * @author Tom
 * @date 2021-04-19-12:02
 */
@ApiModel("燕云创建物流订单")
@Data
@Builder
public class LogisticOrderForYanWen {

    @ApiModelProperty(name = "运单号")
    private String Epcode;
    @NonNull
    @ApiModelProperty(name = "客户号")
    @JacksonXmlProperty(localName = "Userid")
    private String Userid;
    @NonNull
    @ApiModelProperty(name = "发货方式代码：接口2中的channelType类型下Id字段")
    private String Channel;
    @NonNull
    @ApiModelProperty(name = "客户订单号。若下单英国，请提供平台/独立站销售订单号")
    private String UserOrderNumber;
    @NonNull
    @ApiModelProperty(name = "发货日期")
    private String SendDate;
    @ApiModelProperty(name = "申报建议零售价")
    private String MRP;
    @ApiModelProperty(name = "产品使用到期日")
    private String ExpiryDate;
    @ApiModelProperty(name = "")
    private Receiver Receiver;
    @ApiModelProperty(name = "")
    private Sender Sender;
    @ApiModelProperty(name = "备注。会出现在拣货单上")
    private String Memo;
    @NonNull
    @ApiModelProperty(name = "货品总数量")
    private Integer Quantity;
    @ApiModelProperty(name = "包裹号")
    private String PackageNo;
    @ApiModelProperty(name = "")
    private GoodsName GoodsName;
    @ApiModelProperty(name = "店铺名称， 燕特快不含电，国家为巴西时 此属性必填")
    private String MerchantCsName;
    @ApiModelProperty(name = "是否需要保险")
    private String Insure;


    @Data
    @Builder
    public static class Receiver {
        @NonNull
        @ApiModelProperty(name = "客户号")
        private String Userid;
        @NonNull
        @ApiModelProperty(name = "收货人-姓名")
        private String Name;
        @NonNull
        @ApiModelProperty(name = " 收货人手机。美国专线至少填一项")
        private String Phone;
        @ApiModelProperty(name = "收货人-邮箱")
        private String Email;
        @ApiModelProperty(name = "收货人-公司")
        private String Company;
        @NonNull
        @ApiModelProperty(name = "收货人-国家")
        private String Country;
        @NonNull
        @ApiModelProperty(name = "收货人-邮编")
        private String Postcode;
        @NonNull
        @ApiModelProperty(name = "收货人-州")
        private String State;
        @NonNull
        @ApiModelProperty(name = "收货人-城市")
        private String City;
        @NonNull
        @ApiModelProperty(name = "收货人-地址1")
        private String Address1;
        @ApiModelProperty(name = "收货人-地址2")
        private String Address2;
        @ApiModelProperty(name = "护照ID，税号。（国家为巴西时 此属性必填）")
        private String NationalId;
    }

    @Data
    @Builder
    public static class Sender {
        @ApiModelProperty(name = "寄件人税号（VOEC No/ VAT No）。若下单英国，请提供店铺VAT税号，税号规则为GB+9位数字")
        private String TaxNumber;
    }

    @Data
    @Builder
    public static class GoodsName {
        @NonNull
        @ApiModelProperty(name = "客户号")
        private String Userid;
        @NonNull
        @ApiModelProperty(name = "商品中文品名")
        private String NameCh;
        @NonNull
        @ApiModelProperty(name = "商品英文品名")
        private String NameEn;
        @NonNull
        @ApiModelProperty(name = "包裹总重量（单位是：克；不能有小数）")
        private Integer Weight;
        @NonNull
        @ApiModelProperty(name = "申报总价值")
        private BigDecimal DeclaredValue;
        @NonNull
        @ApiModelProperty(name = "申报币种。支持的值：USD,EUR,GBP,CNY,AUD。")
        private String DeclaredCurrency;
        @ApiModelProperty(name = "多品名. 会出现在拣货单上")
        private String MoreGoodsName;
        @ApiModelProperty(name = "商品海关编码,香港FedEx经济必填。")
        private String HsCode;
        @ApiModelProperty(name = "产品品牌，中俄SPSR专线此项必填")
        private String ProductBrand;
        @ApiModelProperty(name = "产品尺寸，中俄SPSR专线此项必填")
        private String ProductSize;
        @ApiModelProperty(name = "产品颜色，中俄SPSR专线此项必填")
        private String ProductColor;
        @ApiModelProperty(name = "产品材质，中俄SPSR专线此项必填")
        private String ProductMaterial;
    }


}
