package com.amg.fulfillment.cloud.logistics.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tom
 * @date 2021-05-17-10:38
 */
@Data
@ApiModel("仓库销售订单预报结果")
public class DepositorySaleOrderPredictionVO {

    @ApiModelProperty(name = "推送销售订单详情")
    private List<SaleOrderResult> saleOrderResults = new ArrayList<>();

    @Data
    @ApiModel("仓库销售订单预报结果")
    public static class SaleOrderResult {
        @ApiModelProperty(name = "id")
        private Long id;
        @ApiModelProperty(name = "销售订单")
        private String saleOrderId;
        @ApiModelProperty(name = "预报结果")
        private Boolean predictionResult;
    }

}
