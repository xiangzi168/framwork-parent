package com.amg.fulfillment.cloud.logistics.dto.logistic;

import com.amg.fulfillment.cloud.logistics.api.enumeration.DeliveryPackagePurchaseChannelEnum;
import com.amg.fulfillment.cloud.logistics.api.enumeration.DeliveryPackageTypeEnum;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsTrackNodeDO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by Seraph on 2021/5/14
 */

@Data
public class DeliveryPackageDto {

        private Integer type = DeliveryPackageTypeEnum.AE_DELIVERY_PACKAGE.getType();       //包裹类型  1 境外发货单  2 AE 发货单 4 CJ
        private Integer purchaseChannel = DeliveryPackagePurchaseChannelEnum.AE.getType();       //采购渠道
        private List<DeliveryPackageItemDto> list;
        private List<LogisticsTrackNodeDO> logisticsTrackNodeDOList;

        @Data
        public static class DeliveryPackageItemDto
        {
            private Boolean trackFlag = true;

            private String salesOrderId;        //销售订单号
            private String channelOrderId;      //渠道订单号
            private String logisticsCode;       //物流商 code
            private String logisticsName;       //物流商名称
            private String logisticsWaybillNo;       //物流单号
            private String logisticsTrackingCode;       //物流跟踪单号
            private String logisticsChannel;       //物流渠道
            private String logisticsChannelStatus;     //物流渠道状态
            private String logisticsArea;       //物流国家
            private String logisticsNode;       //物流最新节点
            private Integer logisticsStatus;     //物流状态
            private Date receivingGoodTime;     //收货时间
            private Date deliveryTime;     //出库时间
            private List<DeliveryPackageTrackDto> trackList;
    }

    @Data
    public static class DeliveryPackageTrackDto
    {
        private Date eventDate;
        private String status;
        private String content;
        private String location;
    }

}
