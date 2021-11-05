package com.amg.fulfillment.cloud.logistics.dto.logistic;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Created by Seraph on 2021/5/17
 */

@Data
public class ChannelOrderLogisticsResponseDto {

    private List<ChannelOrderLogistics> data;

    @Data
    @Builder
    public static class ChannelOrderLogistics
    {
        private String channelOrderId;
        private List<ChannelOrderLogisticsItemResponse> itemList;        //物流信息
    }


    @Data
    public static class ChannelOrderLogisticsItemResponse
    {
        private Long id;        //包裹单号
        private String status;      //包裹状态
        private Boolean isWarehouse;        //是否入库
    }

}
