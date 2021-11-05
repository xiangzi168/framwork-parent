package com.amg.fulfillment.cloud.logistics.api.dto.depository;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NonNull;

/**
 * @author Tom
 * @date 2021-04-13-10:20
 */
@ApiModel(value = "地址")
@Data
public class Address {
    @ApiModelProperty(name = "收件⼈名")
    private String Contacter;
    @ApiModelProperty(name = "收件⼈公司")
    private String Company;
    @ApiModelProperty(name = "地址 1")
    private String Street1;
    @ApiModelProperty(name = "地址 2")
    private String Street2;
    @ApiModelProperty(name = "城市")
    private String City;
    @ApiModelProperty(name = "省份/州")
    private String Province;
    @ApiModelProperty(name = "邮编")
    private String PostCode;
    @ApiModelProperty(name = "国家代码 ISO 3166-1 alpha-2 标准")
    private String CountryCode;
    @ApiModelProperty(name = "电话")
    private String Tel;
    @ApiModelProperty(name = "Email")
    private String Email;
    @ApiModelProperty(name = "收件人/收件公司税号。不同国家叫法不完全一样，如美国为 Tax Identification Number，巴西为 CPF/CNPJ。")
    private String TaxId;
}
