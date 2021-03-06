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
        @ApiModelProperty(name = "?????????")
        private String Userid;
        @ApiModelProperty(name = "?????????-??????")
        private String Name;
        @ApiModelProperty(name = " ?????????????????????????????????????????????")
        private String Phone;
        @ApiModelProperty(name = "?????????-??????")
        private String Email;
        @ApiModelProperty(name = "?????????-??????")
        private String Company;
        @ApiModelProperty(name = "?????????-??????")
        private String Country;
        @ApiModelProperty(name = "?????????-??????")
        private String Postcode;
        @ApiModelProperty(name = "?????????-???")
        private String State;
        @ApiModelProperty(name = "?????????-??????")
        private String City;
        @ApiModelProperty(name = "?????????-??????1")
        private String Address1;
        @ApiModelProperty(name = "?????????-??????2")
        private String Address2;
        @ApiModelProperty(name = "??????ID????????????????????????????????? ??????????????????")
        private String NationalId;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sender {
        @ApiModelProperty(name = "??????????????????VOEC No/ VAT No???????????????????????????????????????VAT????????????????????????GB+9?????????")
        private String TaxNumber;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GoodsName {
        @ApiModelProperty(name = "?????????")
        private String Userid;
        @ApiModelProperty(name = "??????????????????")
        private String NameCh;
        @ApiModelProperty(name = "??????????????????")
        private String NameEn;
        @ApiModelProperty(name = "??????????????????????????????????????????????????????")
        private Integer Weight;
        @ApiModelProperty(name = "???????????????")
        private BigDecimal DeclaredValue;
        @ApiModelProperty(name = "??????????????????????????????USD,EUR,GBP,CNY,AUD???")
        private String DeclaredCurrency;
        @ApiModelProperty(name = "?????????. ????????????????????????")
        private String MoreGoodsName;
        @ApiModelProperty(name = "??????????????????,??????FedEx???????????????")
        private String HsCode;
        @ApiModelProperty(name = "?????????????????????SPSR??????????????????")
        private String ProductBrand;
        @ApiModelProperty(name = "?????????????????????SPSR??????????????????")
        private String ProductSize;
        @ApiModelProperty(name = "?????????????????????SPSR??????????????????")
        private String ProductColor;
        @ApiModelProperty(name = "?????????????????????SPSR??????????????????")
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
