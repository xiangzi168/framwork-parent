package com.amg.fulfillment.cloud.logistics.api.dto.wanb;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * Created by Seraph on 2021/5/28
 */

@Data
public class ParcelShipperInfo {

    @JSONField(name = "CountryCode")
    private String countryCode;     //发货人国家代码
    @JSONField(name = "Province")
    private String province;        //发货人省份
    @JSONField(name = "City")
    private String city;        //发货人城市
    @JSONField(name = "Postcode")
    private String postcode;        //发货人邮编
    @JSONField(name = "Name")
    private String name;        //发货人姓名
    @JSONField(name = "Address")
    private String address;        //发货人地址
    @JSONField(name = "ContactInfo")
    private String contactInfo;        //联系信息(邮箱 或者 电话)
    @JSONField(name = "VatNo")
    private String vatNo;        //税号，英国脱欧后，进入英国货物需要提供此值
    @JSONField(name = "EORI")
    private String eori;        //B2B类型以及B2C的高价值包裹必须提供EORI，如贵司暂无EORI，请联系我司业务人员辅助申请EORI
}
