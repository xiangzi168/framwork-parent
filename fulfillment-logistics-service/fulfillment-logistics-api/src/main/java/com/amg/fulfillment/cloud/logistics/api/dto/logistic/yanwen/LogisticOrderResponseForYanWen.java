package com.amg.fulfillment.cloud.logistics.api.dto.logistic.yanwen;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author Tom
 * @date 2021-04-19-12:12
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogisticOrderResponseForYanWen extends AbstractResponseYanWen<LogisticOrderResponseForYanWen> {
    @ApiModelProperty(name = "")
    private CreatedExpress CreatedExpress;

    public CreatedExpress getCreatedExpress() {
        if (Objects.isNull(CreatedExpress)) {
            CreatedExpress = new CreatedExpress();
        }
        return CreatedExpress;
    }


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CreatedExpress {
        @ApiModelProperty(name = "")
        private Receiver Receiver;
        @ApiModelProperty(name = "")
        private String UserOrderNumber;
        @ApiModelProperty(name = "")
        private GoodsName GoodsName;
        @ApiModelProperty(name = "")
        private Sender Sender;
        @ApiModelProperty(name = "")
        private String DateOfReceipt;
        @ApiModelProperty(name = "")
        private String SalesOfEnterPriseCode;
        @ApiModelProperty(name = "")
        private String SalesOfEnterPriseName;
        @ApiModelProperty(name = "")
        private String PayOfEnterPriseName;
        @ApiModelProperty(name = "")
        private String PayOfEnterPriseCode;
        @ApiModelProperty(name = "")
        private String Epcode;
        @ApiModelProperty(name = "")
        private String Userid;
        @ApiModelProperty(name = "")
        private ChannelType ChannelType;
        @ApiModelProperty(name = "")
        private String Channel;
        @ApiModelProperty(name = "")
        private String SendDate;
        @ApiModelProperty(name = "")
        private Integer Quantity;
        @ApiModelProperty(name = "")
        private CustomDeclarationType CustomDeclarationCollection;
        @ApiModelProperty(name = "")
        private String YanwenNumber;
        @ApiModelProperty(name = "")
        private String ReferenceNo;
        @ApiModelProperty(name = "")
        private String PackageNo;
        @ApiModelProperty(name = "")
        private Boolean Insure;
        @ApiModelProperty(name = "")
        private String Memo;
        @ApiModelProperty(name = "")
        private String TrackingStatus;
        @ApiModelProperty(name = "")
        private boolean IsPrint;
        @ApiModelProperty(name = "")
        private String CreateDate;
        @ApiModelProperty(name = "")
        private String MerchantCsName;
        @ApiModelProperty(name = "")
        private String ProductLink;
        @ApiModelProperty(name = "")
        private String UndeliveryOption;
        @ApiModelProperty(name = "")
        private boolean IsPostPlatform;
        @ApiModelProperty(name = "")
        private String BatteryStatus;
        @ApiModelProperty(name = "")
        private String IsStatus;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChannelType {
        @ApiModelProperty(name = "")
        private String Id;
        @ApiModelProperty(name = "")
        private String Name;
        @ApiModelProperty(name = "")
        private boolean Status;
        @ApiModelProperty(name = "")
        private String NameEn;
        @ApiModelProperty(name = "")
        private boolean LimitStatus;
    }


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Receiver {
        @ApiModelProperty(name = "客户号")
        private String Userid;
        @ApiModelProperty(name = "收货人-姓名")
        private String Name;
        @ApiModelProperty(name = " 收货人手机。美国专线至少填一项")
        private String Phone;
        @ApiModelProperty(name = "收货人-邮箱")
        private String Email;
        @ApiModelProperty(name = "收货人-公司")
        private String Company;
        @ApiModelProperty(name = "收货人-国家")
        private String Country;
        @ApiModelProperty(name = "收货人-邮编")
        private String Postcode;
        @ApiModelProperty(name = "收货人-州")
        private String State;
        @ApiModelProperty(name = "收货人-城市")
        private String City;
        @ApiModelProperty(name = "收货人-地址1")
        private String Address1;
        @ApiModelProperty(name = "收货人-地址2")
        private String Address2;
        @ApiModelProperty(name = "护照ID，税号。（国家为巴西时 此属性必填）")
        private String NationalId;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sender {
        @ApiModelProperty(name = "寄件人税号（VOEC No/ VAT No）。若下单英国，请提供店铺VAT税号，税号规则为GB+9位数字")
        private String TaxNumber;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GoodsName {
        @ApiModelProperty(name = "客户号")
        private String Userid;
        @ApiModelProperty(name = "商品中文品名")
        private String NameCh;
        @ApiModelProperty(name = "商品英文品名")
        private String NameEn;
        @ApiModelProperty(name = "包裹总重量（单位是：克；不能有小数）")
        private Integer Weight;
        @ApiModelProperty(name = "申报总价值")
        private BigDecimal DeclaredValue;
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


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CustomDeclarationType {
        @ApiModelProperty(name = "")
        private String id;
        @ApiModelProperty(name = "")
        private String Userid;
        @ApiModelProperty(name = "")
        private String NameCh;
        @ApiModelProperty(name = "")
        private boolean NameEn;
        @ApiModelProperty(name = "")
        private Integer Weight;
        @ApiModelProperty(name = "")
        private BigDecimal DeclaredValue;
        @ApiModelProperty(name = "")
        private String DeclaredCurrency;
        @ApiModelProperty(name = "")
        private String MoreGoodsName;
        @ApiModelProperty(name = "")
        private String HsCode;
        @ApiModelProperty(name = "")
        private Integer Quantity;
    }

}
