package com.amg.fulfillment.cloud.logistics.api.dto.wanb;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * Created by Seraph on 2021/5/28
 */

@Data
public class Address {

    @JSONField(name = "Company")
    private String company;     //联系人公司
    @JSONField(name = "Street1")
    private String street1;     //街道1
    @JSONField(name = "Street2")
    private String street2;     //街道2
    @JSONField(name = "Street3")
    private String street3;     //街道3
    @JSONField(name = "City")
    private String city;     //城市
    @JSONField(name = "Province")
    private String province;     //州/省
    @JSONField(name = "CountryCode")
    private String countryCode;     //国家代码（ISO 3166-1 alpha-2标准）,创建包裹时国家代码与国家英文名称二者至少得有一个
    @JSONField(name = "Country")
    private String country;     //国家英文名称
    @JSONField(name = "Postcode")
    private String postcode;     //邮编
    @JSONField(name = "Contacter")
    private String contacter;     //收件人
    @JSONField(name = "Tel")
    private String tel;     //收件人联系电话
    @JSONField(name = "Email")
    private String email;     //收件人邮箱
    @JSONField(name = "TaxId")
    private String taxId;     //收件人/收件公司税号。不同国家叫法不完全一样，如美国为 Tax Identification Number，巴西为 CPF/CNPJ。
}
