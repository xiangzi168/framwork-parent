package com.amg.fulfillment.cloud.logistics.api.util;

import com.alibaba.fastjson.JSON;
import com.amg.fulfillment.cloud.logistics.api.config.depository.WanBConfig;
import com.amg.fulfillment.cloud.logistics.api.dto.depository.GoodSku;
import com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb.*;
import com.amg.fulfillment.cloud.logistics.api.enumeration.UrlConfigEnum;
import io.swagger.annotations.ApiModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tom
 * @date 2021-04-14-11:58
 */
@Component
@ApiModel("万邦仓库远程访问工具类")
@Slf4j
public class WanBDepistoryUtil extends AbstractLogisticUtil {

    @Autowired
    WanBConfig wanBConfig;

    public SaleOrderResponseMsgForWanB salesorders(String purchaseOrderId, SalesOrderForWanB salesOrderForWanB)
    {
        String url = String.format(UrlConfigEnum.UrlConfigEnumWanB.SALESORDERS.getUrl(), purchaseOrderId);
        String body = JSON.toJSONString(salesOrderForWanB);
        return this.sendRequest(HttpMethod.PUT, url, body, SaleOrderResponseMsgForWanB.class);
    }

    public DepositoryResponseMsgForWanB purchaseorders(String purchaseOrderId, PurchaseOrderForWanB purchaseOrderForWanB)
    {
        String url = String.format(UrlConfigEnum.UrlConfigEnumWanB.PURCHASEORDERS.getUrl(), purchaseOrderId);
        String body = JSON.toJSONString(purchaseOrderForWanB);
        return this.sendRequest(HttpMethod.PUT, url, body, DepositoryResponseMsgForWanB.class);
    }

    public DepositoryResponseMsgForWanB updatePurchaseorders(String purchaseOrderId, PurchaseOrderForWanB purchaseOrderForWanB) {
        String url = String.format(UrlConfigEnum.UrlConfigEnumWanB.UPDATEPURCHASEORDERS.getUrl(), purchaseOrderId);
        String body = JSON.toJSONString(purchaseOrderForWanB);
        return this.sendRequest(HttpMethod.PUT, url, body, DepositoryResponseMsgForWanB.class);
    }

    public void deletePurchaseorders(String purchaseOrderId) {
        String url = String.format(UrlConfigEnum.UrlConfigEnumWanB.PURCHASEORDERS.getUrl(), purchaseOrderId);
        this.sendRequest(HttpMethod.DELETE, url, null, String.class);
    }

    public DepositoryResponseMsgForWanB dispatchorders(String purchaseOrderId, OutDepositoryOrderForWanB outDepositoryOrderForWanB)
    {
        String url = String.format(UrlConfigEnum.UrlConfigEnumWanB.DISPATCHORDERS.getUrl(), purchaseOrderId);
        String body = JSON.toJSONString(outDepositoryOrderForWanB);
        return this.sendPostRequest(url, body, DepositoryResponseMsgForWanB.class);
    }

    public CancelDepositoryOrderResponseForWanB dispatchordersCancellation(String purchaseOrderId)
    {
        String url = String.format(UrlConfigEnum.UrlConfigEnumWanB.DISPATCHORDERS_CANCELLATION.getUrl(), purchaseOrderId);
        return this.sendPostRequest(url, null, CancelDepositoryOrderResponseForWanB.class);
    }

    public OutDepositoryOrderResponseForWanB dispatchorders(String purchaseOrderId)
    {
        String url = String.format(UrlConfigEnum.UrlConfigEnumWanB.DISPATCHORDERS.getUrl(), purchaseOrderId);
        return this.sendGetRequest(url, OutDepositoryOrderResponseForWanB.class);
    }

    public InventoryResponseForWanB inventories(String sku, String warehouseCode, String type)
    {
        String url = String.format(UrlConfigEnum.UrlConfigEnumWanB.INVENTORIES.getUrl(), sku, warehouseCode, type);
        return this.sendGetRequest(url, InventoryResponseForWanB.class);
    }

    public DepositoryResponseMsgForWanB tradeitemsPut(GoodSku goodSku)
    {
        String url = String.format(UrlConfigEnum.UrlConfigEnumWanB.TRADEITEMS.getUrl(), goodSku.getSku());
        String body = JSON.toJSONString(goodSku);
        return this.sendRequest(HttpMethod.PUT, url, body, DepositoryResponseMsgForWanB.class);
    }

    public GoodSkuResponseForWanB tradeitemsGet(GoodSku goodSku)
    {
        String url = String.format(UrlConfigEnum.UrlConfigEnumWanB.TRADEITEMS.getUrl(), goodSku.getSku());
        return this.sendGetRequest(url, GoodSkuResponseForWanB.class);
    }

    public PurchaseIntoDepositoryDetailForWanB queryPurchaseOrderDetail(String purchaseOrderId) {
        String url = String.format(UrlConfigEnum.UrlConfigEnumWanB.PURCHASEORDERS.getUrl(), purchaseOrderId);
        return this.sendGetRequest(url, PurchaseIntoDepositoryDetailForWanB.class);
    }

    public DepositoryProcessDetailForWanB queryOutOrderDetail(String outOrderId) {
        String url = String.format(UrlConfigEnum.UrlConfigEnumWanB.DISPATCHORDERS.getUrl(), outOrderId);
        return this.sendGetRequest(url, DepositoryProcessDetailForWanB.class);
    }

    @Override
    public String getLogisticName() {
        return "万邦仓库";
    }

    @Override
    public String getRequestUrl(String path) {
        return wanBConfig.getBaseUrl() + path;
    }

    @Override
    public Map<String, List<String>> getRequestHeader() {
        Map<String, List<String>> headerMap = new HashMap<>();
        headerMap.put("Accept", Collections.singletonList("application/json"));
        headerMap.put("Content-Type", Collections.singletonList("application/json"));
        headerMap.put("Authorization", Collections.singletonList(wanBConfig.getAuthorization()));
        return headerMap;
    }
}

