package com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb;

import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Tom
 * @date 2021-04-29-17:50
 */
@Data
public class SaleOrderResponseMsgForWanB extends AbstractResponseMsg<SaleOrderResponseMsgForWanB> {

    private SaleOrderDetail Data;

    public SaleOrderDetail getData() {
        if (Objects.isNull(Data)) {
            Data = new SaleOrderDetail();
        }
        return Data;
    }

    @Data
    public static class SaleOrderDetail {
        private String RefId;
        private String AccountNo;
        private List<SalesOrderProduct> Products;
        private String CreateOn;
        private String CancelOn;
        private boolean IsCancelled;
        private String Notes;

        public List<SalesOrderProduct> getProducts() {
            if (Objects.isNull(Products)) {
                Products=Collections.emptyList();
            }
            return Products;
        }
    }

    @Data
    public static class SalesOrderProduct {
        private String SKU;
        private String Quantity;
        private boolean IsCancelled;
    }

}
