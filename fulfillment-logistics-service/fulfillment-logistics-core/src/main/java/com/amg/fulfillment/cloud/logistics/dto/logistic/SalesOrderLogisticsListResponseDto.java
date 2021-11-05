package com.amg.fulfillment.cloud.logistics.dto.logistic;

import lombok.Data;

import java.util.List;

/**
 * Created by Seraph on 2021/5/31
 */

@Data
public class SalesOrderLogisticsListResponseDto {

    private String salesOrderId;        //销信订单 id
    private List<RepeatedSalesOrderLogisticsItemListResponse> itemList;

    @Data
    public static class RepeatedSalesOrderLogisticsItemListResponse
    {
        private String logisticsName;      //物流名称
        private String logisticsOrderNo;        //物流订单号
        private String logisticsNode;     //物流节点
    }
}
