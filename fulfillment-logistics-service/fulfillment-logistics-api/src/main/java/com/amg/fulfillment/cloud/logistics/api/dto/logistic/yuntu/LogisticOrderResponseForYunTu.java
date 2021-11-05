package com.amg.fulfillment.cloud.logistics.api.dto.logistic.yuntu;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Tom
 * @date 2021-04-16-13:48
 */
@ApiModel("云图物流订单响应对象")
@Data
public class LogisticOrderResponseForYunTu extends AbstractResponseForYunTu<LogisticOrderResponseForYunTu>{


    @ApiModelProperty(name = "OrderResponse对象")
    private List<OrderResponse> Item;

    public List<OrderResponse> getItem() {
        if (Objects.isNull(Item)) {
            Item= Collections.EMPTY_LIST;
        }
        return Item;
    }

    @Data
    public static class OrderResponse {
        @ApiModelProperty(name = "客户的订单号")
        private String CustomerOrderNumber;
        @ApiModelProperty(name = "运单请求状态，1-成功，0-失败")
        private int Success;
        @ApiModelProperty(name = "1-已产生跟踪号，2-等待后续更新跟踪号,3-不需要跟踪号")
        private String TrackType;
        @ApiModelProperty(name = "运单请求失败反馈失败原因")
        private String Remark;
        @ApiModelProperty(name = "代理单号")
        private int AgentNumber;
        @ApiModelProperty(name = "YT运单号")
        private String WayBillNumber;
        @ApiModelProperty(name = "0-不需要分配地址，1-需要分配地址")
        private String RequireSenderAddress;
        @ApiModelProperty(name = "跟踪号")
        private String TrackingNumber;
        @ApiModelProperty(name = "箱子信息")
        private String ShipperBoxs;
        @ApiModelProperty(name = "箱子号码")
        private String BoxNumber;
        @ApiModelProperty(name = "物流运单子单号")
        private String ShipperHawbcode;
    }
}
