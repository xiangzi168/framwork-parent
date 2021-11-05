package com.amg.fulfillment.cloud.logistics.api.dto.logistic.yuntu;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Tom
 * @date 2021-04-16-13:58
 */
@Data
public class TrackResponseForYunTu extends AbstractResponseForYunTu<TrackResponseForYunTu> {

    @ApiModelProperty(name = "OrderResponse对象")
    private Item Item;


    @Data
    public static class Item {

        @ApiModelProperty(name = "目的地国家简码")
        private String CountryCode;
        @ApiModelProperty(name = "运单号")
        private String WaybillNumber;
        @ApiModelProperty(name = "跟踪号")
        private String TrackingNumber;
        @ApiModelProperty(name = "创建人")
        private String CreatedBy;
        @ApiModelProperty(name = "包裹状态 0-未知，1-已提交  2-运输中  3-已签收，4-已收货，5-订单取消，6-投递失败，7-已退回")
        private Integer PackageState;
        @ApiModelProperty(name = "包裹签收天数")
        private Integer IntervalDays;
        @ApiModelProperty(name = "订单跟踪详情")
        private List<OrderTrackingDetail> OrderTrackingDetails;

    }

    @Data
    public static class OrderTrackingDetail {
        @ApiModelProperty(name = "包裹请求日期")
        private String ProcessDate;
        @ApiModelProperty(name = "包裹请求内容")
        private String ProcessContent;
        @ApiModelProperty(name = "包裹请求地址")
        private String ProcessLocation;
        @ApiModelProperty(name = "包裹节点")
        private Integer TrackingStatus;
    }
}
