package com.amg.fulfillment.cloud.logistics.api.dto.logistic.Wanb;

import com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb.AbstractResponseMsg;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.apache.poi.ss.formula.functions.T;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Tom
 * @date 2021-04-16-11:40
 */
@ApiModel("万邦物流轨迹响应对象")
@Data
@ToString(callSuper = true)
public class TrackResponseForWanb extends AbstractResponseWanb<TrackResponseForWanb> {

    private TrackDetail data;

    @Data
    public static class TrackDetail {
        @ApiModelProperty(name = "匹配值类型，只要此值不为 Unknown，即表示找到了与输入值匹配的包裹\n" +
                "Unknown: 未能匹配到任何处理号、跟踪号或者客单号\n" +
                "ParcelId: 请求值为处理号\n" +
                "ParcelTrackingNumber: 请求值为包裹跟踪号\n" +
                "ParcelReferenceId: 请求值为包裹的客单号")
        private String Match;
        @ApiModelProperty(name = "请求值")
        private String TrackingNumber;
        @ApiModelProperty(name = "包裹投递状态，输入值未匹配到任何包裹时，此值为空\n" +
                "DataReceived: 收到数据\n" +
                "InTransit: 运输途中\n" +
                "DeliveryReady: 到达待取\n" +
                "DeliveryTried: 尝试投递失败。部分渠道会尝试投递数次\n" +
                "Delivered: 已妥投\n" +
                "DeliveryFailed: 投递失败。 地址问题、尝试投递数次均失败、收件人搬家或拒收等\n" +
                "Returned: 已退回\n" +
                "Lost: 包裹遗失")
        private String Status;
        @ApiModelProperty(name = "包裹元数据，输入值未匹配到任何包裹时，此值为 null")
        private Metadata Metadata;
        @ApiModelProperty(name = "轨迹")
        private List<TrackPoint> TrackPoints;
    }

    @Data
    public static class Metadata {
        @ApiModelProperty(name = "包裹处理号")
        private String TrackItemId;
        @ApiModelProperty(name = "客单号")
        private String ReferenceId;
        @ApiModelProperty(name = "包裹最终跟踪号")
        private String TrackingNumber;
        @ApiModelProperty(name = "航班号")
        private String FlightNumber;
        @ApiModelProperty(name = "")
        private String TrackItemType;
        @ApiModelProperty(name = "物流产品代码")
        private String ShippingProductId;
        @ApiModelProperty(name = "物流服务代码")
        private String ShippingServiceId;
        @ApiModelProperty(name = "包裹发件人所在国家代码")
        private String OriginCountryCode;
        @ApiModelProperty(name = "包裹收件人所在国家代码")
        private String DestinationCountryCode;
        @ApiModelProperty(name = "")
        private String DeliveredOn;
    }

    @Data
    public static class TrackPoint {
        @ApiModelProperty(name = "ISO 8601 标准格式化时间")
        private String Time;
        @ApiModelProperty(name = "关键节点状态")
        private String Status;
        @ApiModelProperty(name = "地点")
        private String Location;
        @ApiModelProperty(name = "轨迹内容")
        private String Content;
    }
}
