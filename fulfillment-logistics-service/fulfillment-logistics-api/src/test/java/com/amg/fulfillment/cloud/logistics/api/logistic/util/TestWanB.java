package com.amg.fulfillment.cloud.logistics.api.logistic.util;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import org.junit.Test;

import java.util.Random;

/**
 * @author Tom
 * @date 2021-04-07-20:14
 */
public class TestWanB {
//    public static String HC="Hc-OweDeveloper";      //为固定的，而后紧接一个空格
//    public static String AccountNo="TEST";
//    public static String Token="DK49AjVbKj23bdk";
//    public static String Nounce= RandomUtil.randomNumbers(3);
//    public static String split=";";
//    public static String authorization=HC+" "+AccountNo+split+Token+split+Nounce;
//    public static String baseUrl="http://api-sbx.wanbexpress.com/api/";
//
////    //生产 ok
////    public static String HC="Hc-OweDeveloper";      //为固定的，而后紧接一个空格
////    public static String AccountNo="WNB02328";
////    public static String Token="Y4QMpDeXM2bi5jd";
////    public static String Nounce= RandomUtil.randomNumbers(3);
////    public static String split=";";
////    public static String authorization=HC+" "+AccountNo+split+Token+split+Nounce;
////    public static String baseUrl="http://api.wanbexpress.com/api/";
//
//    // ok
//    @Test
//    public void testTrack() {
//        String url=baseUrl+"trackPoints";
//        String requestUrl=url+"?trackingNumber=SBXTT0000046203YQ";
//        String response = HttpRequest.get(requestUrl).header("Accept", "application/json")
//                .header("Authorization",authorization)
//                .execute().body();
//        System.out.println(response);
//    }
//
//    // ok
//    @Test
//    public void testCreateOrder() {
//        String url=baseUrl+"parcels";
//        String body="{\"ReferenceId\":\"REF1000000008\",\"SellingPlatformOrder\":null,\"ShippingAddress\":{\"Company\":\"Company\",\"Street1\":\"Street1\",\"Street2\":\"Street1\",\"Street3\":null,\"City\":\"City\",\"Province\":\"\",\"Country\":\"\",\"CountryCode\":\"GB\",\"Postcode\":\"NW1 6XE\",\"Contacter\":\"Jon Snow\",\"Tel\":\"134567890\",\"Email\":\"\",\"TaxId\":\"\"},\"WeightInKg\":1.2,\"ItemDetails\":[{\"GoodsId\":\"GoodsId\",\"GoodsTitle\":\"GoodsTitle\",\"DeclaredNameEn\":\"Test\",\"DeclaredNameCn\":\"品名测试\",\"DeclaredValue\":{\"Code\":\"USD\",\"Value\":5.0},\"WeightInKg\":0.6,\"Quantity\":2,\"HSCode\":\"\",\"CaseCode\":\"\",\"SalesUrl\":\"http://www.amazon.co.uk/gp/product/B00FEDIPQ4\",\"IsSensitive\":false,\"Brand\":\"\",\"Model\":\"\",\"MaterialCn\":\"\",\"MaterialEn\":\"\",\"UsageCn\":\"\",\"UsageEn\":\"\"}],\"TotalValue\":{\"Code\":\"USD\",\"Value\":10.0},\"TotalVolume\":{\"Length\":1.0,\"Width\":1.0,\"Height\":1.0,\"Unit\":\"CM\"},\"WithBatteryType\":\"NOBattery\",\"Notes\":\"Test\",\"BatchNo\":\"\",\"WarehouseCode\":\"SZ\",\"ShippingMethod\":\"3HPA\",\"ItemType\":\"SPX\",\"TradeType\":\"B2C\",\"TrackingNumber\":\"\",\"IsMPS\":false,\"MPSType\":null,\"Cases\":[],\"AllowRemoteArea\":true,\"AutoConfirm\":false,\"ShipperInfo\":null}";
//        String response = HttpRequest.post(url).header("Accept", "application/json")
//                .header("Authorization",authorization)
//                .body(body)
//                .execute().body();
//        System.out.println(response);
//    }
//
//    @Test
//    public void testConfirm() {
//        String url=baseUrl+"parcels/SBXTT0000046203YQ1/confirmation";
//        String response = HttpRequest.post(url).header("Accept", "application/json")
//                .header("Authorization",authorization)
//                .execute().body();
//        System.out.println(response);
//    }
}
