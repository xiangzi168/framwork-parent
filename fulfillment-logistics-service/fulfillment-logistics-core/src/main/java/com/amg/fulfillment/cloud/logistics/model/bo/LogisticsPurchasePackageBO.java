package com.amg.fulfillment.cloud.logistics.model.bo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LogisticsPurchasePackageBO {
    private Long id;
    private List<LogisticsProductBO> logisticsProductList = new ArrayList<>();

    @Data
    public static class LogisticsProductBO {
        private Long id;
        private Long packageId;
        private String sku;             //销售订单sku
        private String skuChannel;      //渠道sku（1688）
        private String spu;             //销售订单spu
        private String spuChannel;      //渠道spu（1688）
        private String productStatus;      //渠道spu（1688）
        private int productCount;           //数量
        private int realProductCount;           //数量
        private List<LogisticsPurchasePackageErrorProductBO> logisticsPurchasePackageErrorProductList = new ArrayList<>();
    }

    @Data
    public static class LogisticsPurchasePackageErrorProductBO {
        private Long id;
        private Long productId;
        private Long packageId;
        private String  purchaseId;
        private String saleOrderId;
    }

}
