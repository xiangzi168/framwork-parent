package com.amg.fulfillment.cloud.logistics.entity;

import lombok.Data;

/**
 * @ClassName LogisticsRecommendAddressRequest
 * @Description TODO
 * @Author 35112
 * @Date 2021/8/4 18:01
 **/
@Data
public class LogisticsRecommendAddressRequest {
    /**
     * 国家号
     */
    private String countryCode;
    private String firstName;
    private String lastName;
    private String company;
    private String street1;
    private String street2;
    private String city;
    private String province;
    private String postCode;
    private String tel;
    private String email;
    private String taxNumber;
}
