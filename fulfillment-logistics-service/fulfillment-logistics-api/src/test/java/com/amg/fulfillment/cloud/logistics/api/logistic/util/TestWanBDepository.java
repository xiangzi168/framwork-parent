package com.amg.fulfillment.cloud.logistics.api.logistic.util;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb.DepositoryResponseMsgForWanB;
import com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb.PurchaseOrderExpressEditRequest;
import com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb.PurchaseOrderForWanB;
import com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb.PurchaseOrderProductContentEditRequest;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;


/**
 * @author Tom
 * @date 2021-04-08-11:36 万邦仓库里
 */
public class TestWanBDepository {

   /* public static String HC="Hc-OweDeveloper";      //为固定的，而后紧接一个空格
    public static String AccountNo="TEST";
    public static String Token="DK49AjVbKj23bdk";
    public static String Nounce= RandomUtil.randomNumbers(3);
    public static String split=";";
    public static String authorization=HC+" "+AccountNo+split+Token+split+Nounce;
    public static String baseUrl="http://api-sbx.wanbexpress.com/api/rps";
    public static String WarehouseCode="SZ";

    //3.1.2 推送备货采购订单接⼝
    //ok
    @Test
    public void testPutPurchaseOrder() {
        String url=baseUrl+"purchaseOrder/";
        String requestUrl=url+"DTAL201100000803";
        //文档中的属性大写
//        String body="{\"PurchaseReason\":\"StockReplenishment\",\"WarehouseCode\":\"SZ\",\"Notes\":\"Notes\",\"RefId1\":\"\",\"RefId2\":\"\",\"RefId3\":\"\",\"ProductContent\":{\"Products\":[{\"SKU\":\"1138\",\"CheckTypes\":[\"Quality\",\"Photo\"],\"Quantity\":10},{\"SKU\":\"1139\",\"CheckTypes\":null,\"Quantity\":100}]},\"Expresses\":[{\"ExpressId\":\"90101001010001\",\"ExpressVendorId\":\"SF\",\"ExpectedArriveOn\":\"2019-10-15T08:23:00Z\",\"ShipFromProvince\":\"Jiangsu\"}]}";
        //属性小写
//        String body="{\"purchaseReason\":\"StockReplenishment\",\"warehouseCode\":\"SZ\",\"notes\":\"Notes\",\"refId1\":\"\",\"refId2\":\"\",\"refId3\":\"\",\"productContent\":{\"products\":[{\"sKU\":\"1138\",\"checkTypes\":[\"Quality\",\"Photo\"],\"quantity\":10},{\"sKU\":\"1139\",\"checkTypes\":null,\"quantity\":100}]},\"expresses\":[{\"expressId\":\"90101001010001\",\"expressVendorId\":\"SF\",\"expectedArriveOn\":\"2019-10-15T08:23:00Z\",\"shipFromProvince\":\"Jiangsu\"}]}";
        PurchaseOrderProductContentEditRequest.PurchaseOrderProductEditRequest product1 = PurchaseOrderProductContentEditRequest.PurchaseOrderProductEditRequest.builder().SKU("1138").CheckTypes(Arrays.asList("Quality", "Photo")).Quantity(10).build();
        PurchaseOrderProductContentEditRequest.PurchaseOrderProductEditRequest product2 = PurchaseOrderProductContentEditRequest.PurchaseOrderProductEditRequest.builder().SKU("1139").Quantity(100).build();
        PurchaseOrderProductContentEditRequest ProductContent = PurchaseOrderProductContentEditRequest.builder().Products(Arrays.asList(product1,product2)).build();
        PurchaseOrderExpressEditRequest Expresses = PurchaseOrderExpressEditRequest.builder().ExpressId("90101001010001").ExpressVendorId("SF").ExpectedArriveOn(new Date()).ShipFromProvince("Jiangsu").build();
        PurchaseOrderForWanB purchaseOrderForWanB = PurchaseOrderForWanB.builder().PurchaseReason("StockReplenishment")
                .WarehouseCode("SZ").Notes("测试备注").ProductContent(ProductContent).Expresses(Arrays.asList(Expresses)).build();
        String body = JSON.toJSONString(purchaseOrderForWanB);
        System.out.println("发送数据："+body);
        String response = HttpRequest.put(requestUrl).header("Accept", "application/json")
                .header("Authorization",authorization)
                .body(body)
                .execute().body();
        System.out.println("接受到数据："+response);
        if (response.contains("Succeeded") && response.contains("Data")) {
            TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
           DepositoryResponseMsgForWanB depositoryResponseMsgForWanB = JSONObject.parseObject(response, DepositoryResponseMsgForWanB.class);
            System.out.println("接受到数据转化成对象" + depositoryResponseMsgForWanB);
            System.out.println(JSON.toJSONString(depositoryResponseMsgForWanB));
        } else {
            System.out.println("接受非JSON字符串："+response);
        }
    }


    //4.1 推送出库订单接⼝
    //ok
    @Test
    public void testPostDispatchOrders() {
        String url=baseUrl+"dispatchOrders/";
        String orderId="DTAL201100000802";
        String body="{\"SalesOrderId\":\"SO191021001\",\"WarehouseCode\":\"SZ\",\"Address\":{\"Contacter\":\"Sherlock Holmes\",\"Company\":null,\"Street1\":\"221b baker street\",\"Street2\":\"\",\"City\":\"London\",\"Province\":null,\"PostCode\":\"NW1 6XE\",\"CountryCode\":\"GB\",\"Tel\":null,\"Email\":null},\"Shipping\":{\"ServiceId\":\"CM_DHL\",\"TrackingNumber\":\"00340009923334552189\",\"LabelUrl\":\"http://label.sample.com/00340009923334552189.pdf\",\"Zone\":\"\"},\"Items\":[{\"SKU\":\"YCSBNG8361455\",\"Quantity\":1}],\"Notes\":\"Notes\"}";
        String requestUrl=url+orderId;
        String response = HttpRequest.post(requestUrl).header("Accept", "application/json")
                .header("Authorization",authorization)
                .body(body)
                .execute().body();
        System.out.println(response);
        //返回值
        //{"Data":{"RefId":"DTAL201100000802","AccountNo":"TEST","SalesOrderId":"SO191021001","WarehouseCode":"SZ","Status":"Created","CreateOn":"2021-04-08T10:49:21Z"},"Succeeded":true,"Error":null}
    }
    //4.2 取消出库订单接⼝
    // ok
    @Test
    public void testCancelDispatchOrders() {
        String url=baseUrl+"dispatchOrders/";
        String orderId="DTAL201100000802";
        String type="/cancellation";
        String requestUrl=url+orderId+type;
        String response = HttpRequest.post(requestUrl).header("Accept", "application/json")
                .header("Authorization",authorization)
                .execute().body();
        System.out.println(response);
        //返回值
        //{"Data":{"Status":"Requested","RequestOn":"2021-04-08T10:52:05Z","ResponseOn":null,"Message":""},"Succeeded":true,"Error":null}
    }
    //5.3 查询产品库存接⼝
    // ok
    @Test
    public void testInventories() {
        String url=baseUrl+"/inventories/";
        String sku="YCSBNG8361455";
        String type="InStock";
        String requestUrl=url+sku+"?warehouseCode="+WarehouseCode*//*+"&type="+type*//*;
        String response = HttpRequest.get(requestUrl).header("Accept", "application/json")
                .header("Authorization",authorization)
                .execute().body();
        System.out.println(response);
        //返回值
        //{"Data":[{"SKU":"YCSBNG8361455","WarehouseCode":"SZ","Type":"InTransit","TotalQuantity":0,"FrozenQuantity":0,"LockQuantity":0,"AvailableQuantity":0,"LastModifyOn":"2021-03-25T02:41:36Z"},{"SKU":"YCSBNG8361455","WarehouseCode":"SZ","Type":"InStock","TotalQuantity":10,"FrozenQuantity":1,"LockQuantity":0,"AvailableQuantity":9,"LastModifyOn":"2021-04-08T10:49:21Z"}],"Succeeded":true,"Error":null}
    }

    //4.3 查询出库订单接⼝
    // ok
    @Test
    public void testDispatchOrders() {
        String url=baseUrl+"dispatchOrders/";
        String requestUrl=url+"LOG20210628000010";
        String response = HttpRequest.get(requestUrl).header("Accept", "application/json")
                .header("Authorization",authorization)
                .execute().body();
        System.out.println(response);
        //返回值
        //{"Data":{"Items":[],"RefId":"DTAL201100000802","AccountNo":"TEST","SalesOrderId":"SO191021001","WarehouseCode":"SZ","Address":{"Contacter":"Sherlock Holmes","Company":"","Street1":"221b baker street","Street2":"","City":"London","Province":"","PostCode":"NW1 6XE","CountryCode":"GB","Tel":"","Email":""},"Status":"Created","CreateOn":"2021-04-08T10:49:21Z","PackOn":null,"EstimatedWeightInKg":0.0,"Shipping":{"ServiceId":"CM_DHL","TrackingNumber":"00340009923334552189","LabelUrl":"http://label.sample.com/00340009923334552189.pdf","Zone":""},"Products":[{"SKU":"YCSBNG8361455","Quantity":1,"PackQuantity":null}],"Packing":null,"ShipOutInfo":null,"PackFailReason":null,"Notes":"Notes"},"Succeeded":true,"Error":null}
    }
     //5.1 推送产品资料接⼝
    // ok
    @Test
    public void testPutTrackItems() {
        String url=baseUrl+"tradeItems/";
        String sku="sd01100000802";
        String requestUrl=url+sku;
//        String body="{\"Name\":\"⼿机壳\",\"Variants\":[{\"Name\":\"颜⾊\",\"Value\":\"墨绿\"},{\"Name\":\"型号\",\"Value\":\"iphone-x\"}],\"ImageUrls\":[\"http://sample.com/img/1.jpg\",\"http://sample.com/img/2.jpg\"],\"WeightInKg\":0.23}";
        String body="{\"name\":\"B3090臧\",\"sku\":\"YCSBNG59787\",\"variants\":[{\"name\":\"spec\",\"value\":\"采购规格1\"}],\"weightInKg\":0.002}";
        String response = HttpRequest.put(requestUrl).header("Accept", "application/json")
                .header("Authorization",authorization)
                .body(body)
                .execute().body();
        System.out.println(response);
    }
     //5.2查询产品资料接⼝
    // ok
    @Test
    public void testTrackItems() {
        String url=baseUrl+"tradeItems/";
        String sku="sd01100000802";
        String requestUrl=url+sku;
        String response = HttpRequest.get(requestUrl).header("Accept", "application/json")
                .header("Authorization",authorization)
                .execute().body();
        System.out.println(response);
    }
*/
}
