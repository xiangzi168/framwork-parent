package com.amg.fulfillment.cloud.logistics.dto.logistic;

import lombok.Data;

import java.util.List;

/**
 * Created by Seraph on 2021/5/17
 */

@Data
public class ChannelOrderLogisticsDto {
    private Integer type;       //1  1688 渠道订单     2 AE 渠道订单
    private List<String> channelOrderIdList;
}
