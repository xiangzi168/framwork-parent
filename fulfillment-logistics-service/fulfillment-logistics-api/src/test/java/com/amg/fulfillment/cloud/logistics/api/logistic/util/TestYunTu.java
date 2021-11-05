package com.amg.fulfillment.cloud.logistics.api.logistic.util;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.http.HttpRequest;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;


/**
 * @author Tom
 * @date 2021-04-07-19:03
 */
public class TestYunTu {
/*
    static String url="http://omsapi.uat.yunexpress.com/api/";
    static String UserNo="C06901";
    static String ApiSecret="3VniAsdO6mU=";
    static String Authorization="";

    //正式  ok
//    static String url="http://oms.api.yunexpress.com/api/";
//    static String Authorization="Basic QzU5NjkzJkpNV3pWZmFyNzlNPQ==";

//    @Before
//    public void testBase64() {
//        System.out.println(Base64Encoder.encode(UserNo +"&"+ApiSecret));
//        Authorization="Basic "+Base64Encoder.encode(UserNo +"&"+ApiSecret);
//    }

    @Test
    public void testGetCountry() {
        String requestUrl=url+"Common/GetCountry";
        String reponseBody = HttpRequest.get(requestUrl).header("Accept", "application/json")
                .header("Authorization",Authorization)
                .execute().body();
        System.out.println(reponseBody);
    }

    //ok
    @Test
    public void testTrack() {
        String requestUrl=url+"Tracking/GetTrackAllInfo";
        requestUrl=requestUrl+"?OrderNumber=YT2113021272281270";
        String reponseBody = HttpRequest.get(requestUrl).header("Accept", "application/json")
                .header("Authorization",Authorization)
                .execute().body();
        System.out.println(reponseBody);
    }

    //OK
    @Test
    public void testCreateOrder() {
        String requestUrl=url+"WayBill/CreateOrder";
        String body="[{\"CustomerOrderNumber\":\"SG2G20190402003\",\"ShippingMethodCode\":\"THPHR\",\"TrackingNumber\":null,\"TransactionNumber\":null,\"BrazilianCode\":null,\"Height\":1,\"Length\":1,\"Width\":1,\"PackageCount\":1,\"Weight\":1,\"ApplicationType\":4,\"ReturnOption\":0,\"TariffPrepay\":0,\"InsuranceOption\":0,\"SourceCode\":\"API\",\"Receiver\":{\"CountryCode\":\"US\",\"FirstName\":\"xin\",\"LastName\":\"ming\",\"Company\":\"test gs\",\"Street\":\"67700 Lockwood-Jolon Road\",\"City\":\"Lockwood\",\"State\":\"California\",\"Zip\":\"93932\",\"Phone\":\"5869098233\",\"HouseNumber\":\"1\",\"Email\":\"12345@qq.com\"},\"Sender\":{\"CountryCode\":\"US\",\"FirstName\":\"test\",\"LastName\":\"ming\",\"Company\":\"test gs\",\"Street\":\"207 TELLURIDE DR\",\"City\":\"GEORGETOWN\",\"State\":\"TX\",\"Zip\":\"78626-7163\",\"Phone\":\"5869098233\",\"HouseNumber\":\"1\"},\"Parcels\":[{\"EName\":\"shangpin1\",\"CName\":\" 商 品 1\",\"HSCode\":null,\"Quantity\":1,\"SKU\":\"sku1001\",\"UnitPrice\":10,\"UnitWeight\":1,\"Sku\":\"sku1001\"}]}]";
        String reponseBody = HttpRequest.post(requestUrl)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + Base64Encoder.encode(UserNo +"&"+ApiSecret))
                .body(body)
                .execute().body();
        System.out.println(reponseBody);
    }*/
}
