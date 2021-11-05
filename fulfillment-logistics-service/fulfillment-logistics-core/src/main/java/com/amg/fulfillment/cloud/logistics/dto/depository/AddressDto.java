package com.amg.fulfillment.cloud.logistics.dto.depository;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Tom
 * @date 2021-04-19-18:40
 */
@Data
public class AddressDto {

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
