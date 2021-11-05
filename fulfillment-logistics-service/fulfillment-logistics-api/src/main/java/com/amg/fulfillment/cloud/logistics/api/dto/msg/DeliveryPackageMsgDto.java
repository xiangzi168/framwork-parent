package com.amg.fulfillment.cloud.logistics.api.dto.msg;

import lombok.Data;

import java.util.List;

/**
 * Created by Seraph on 2021/5/21
 */

@Data
public class DeliveryPackageMsgDto {

    private String salesOrderId;        //关联销售订单id
    private String channelOrderId;      //关联渠道订单 id
    private String logisticsArea;     //物流收货国家
    private List<DeliveryPackagelogisticsDto> logisticsList;


    @Data
    public static class DeliveryPackagelogisticsDto {
        private String logisticsCode;       //物流商编号      logistics_service
        private String logisticsTrackingCode;       //物流跟踪单号        logistics_no
        private String logisticsStatus;     //物流状态
        private String logisticsName;       //物流商名称
        private String logisticsWaybillNo;       //物流单号
        private String logisticsChannel;       //物流渠道
        private String logisticsChannelStatus;     //物流渠道状态
    }
}
