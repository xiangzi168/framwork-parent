package com.amg.fulfillment.cloud.logistics.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LogisticsPackageOperationerStatisticsVO {

    List<LogisticsPackageOperationer> logisticsPackageOperationers = new ArrayList<>();

    @Data
    public static class LogisticsPackageOperationer {
        @ApiModelProperty("操作人")
        private String operationer;
        @ApiModelProperty("出库包裹数")
        private int packageCount;
        @ApiModelProperty("出库商品数")
        private int productCount;
    }

}
