package com.amg.fulfillment.cloud.logistics.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Seraph on 2021/5/31
 */

@Data
@ApiModel(value = "LogisticsRecommendReq")
public class LogisticsRecommendReq {

    @ApiModelProperty(value = "物流商 code")
    private String logisticsCode;
    @ApiModelProperty(value = "商品集合")
    private List<LogisticsRecommendProductReq> productList;
    @ApiModelProperty(value = "地址信息")
    private LogisticsRecommendAddressReq address;

    @Data
    public static class LogisticsRecommendProductReq
    {
        @ApiModelProperty(name = "item Id")
        private String itemId;
        @ApiModelProperty(name = "货物编号 sku")
        private String goodsId;
        @ApiModelProperty(name = "货物描述")
        private String goodsTitle;
        @ApiModelProperty(name = "英文申报名称")
        private String declaredNameEn;
        @ApiModelProperty(name = "中文申报名称")
        private String declaredNameCn;
        @ApiModelProperty(name = "单件申报价值")
        private BigDecimal declaredValue;
        @ApiModelProperty(name = "单件重量")
        private BigDecimal weight;
        @ApiModelProperty(name = "sku 图片")
        private String img;
        @ApiModelProperty(name = "sku 属性")
        private String attribute;
        @ApiModelProperty(name = "采购 id")
        private String purchaseId;
        @ApiModelProperty(name = "类目 code")
        private String categoryCode;
        @ApiModelProperty(name = "商品所包含的标签")
        private List<Long> labelIdList;
        @ApiModelProperty(name = "销售价格（元）")
        private BigDecimal salePriceCny;
    }

    @Data
    public static class LogisticsRecommendAddressReq
    {
        @NotBlank(message = "收件⼈名不能是null")
        @ApiModelProperty(name = "收件⼈名-last")
        private String firstName;
        @ApiModelProperty(name = "收件⼈名-first")
        private String lastName;
        @ApiModelProperty(name = "收件⼈公司")
        private String company;
        @ApiModelProperty(name = "地址 1")
        private String street1;
        @ApiModelProperty(name = "地址 2")
        private String street2;
        @ApiModelProperty(name = "城市")
        private String city;
        @ApiModelProperty(name = "省份/州")
        private String province;
        @NotBlank(message = "邮编不能是null")
        @ApiModelProperty(name = "邮编")
        private String postCode;
        @NotBlank(message = "国家代码不能是null")
        @ApiModelProperty(name = "国家代码 ISO 3166-1 alpha-2 标准")
        private String countryCode;
        @ApiModelProperty(name = "电话")
        private String tel;
        @ApiModelProperty(name = "Email")
        private String email;
        @ApiModelProperty(name = "寄件人税号（VOEC No/ VAT No）。若下单英国，请提供店铺VAT税号，税号规则为GB+9位数字")
        private String taxNumber;
    }
}
