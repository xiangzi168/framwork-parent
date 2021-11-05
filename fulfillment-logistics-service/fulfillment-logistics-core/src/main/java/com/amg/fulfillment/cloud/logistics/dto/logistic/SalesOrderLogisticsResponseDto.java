package com.amg.fulfillment.cloud.logistics.dto.logistic;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by Seraph on 2021/5/17
 */
@Data
public class SalesOrderLogisticsResponseDto {

    private String salesOrderId;        //销信订单 id
    private Map<String, RepeatedSalesOrderLogisticsItemResponseDto> itemMap;

    @Data
    public static class RepeatedSalesOrderLogisticsItemResponseDto
    {
        private List<SalesOrderLogisticsItemResponseDto> repeatedItem;
    }

    @Data
    public static class SalesOrderLogisticsItemResponseDto
    {
        private Long id;        //包裹单号
        private String itemId;      //item id
        private String channelOrderId;      //渠道订单 id
        private String logisticsName;       //物流名称
        private String logisticsOrderNo;       //物流订单号
        private String logisticsNode;     //物流节点
    }
}
