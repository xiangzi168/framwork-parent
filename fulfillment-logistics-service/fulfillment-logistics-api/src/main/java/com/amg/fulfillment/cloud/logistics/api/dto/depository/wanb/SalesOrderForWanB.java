package com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb;

import com.amg.fulfillment.cloud.logistics.api.dto.depository.ProductVariant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

/**
 * @author Tom
 * @date 2021-04-29-17:42
 */
@Data
@Builder
public class SalesOrderForWanB {
    @ApiModelProperty(name = "备注")
    private String Notes;
    @NonNull
    @ApiModelProperty(name = "产品列表")
    private List<SalesOrderProductEditRequest> Products;

    @Data
    @Builder
    public static class SalesOrderProductEditRequest {
        @NonNull
        @ApiModelProperty(name = "SKU")
        private String SKU;
        @NonNull
        @ApiModelProperty(name = "数量")
        private Integer Quantity;
        @NonNull
        @ApiModelProperty(name = "名称")
        private String Name;
        @ApiModelProperty(name = "产品规格")
        private List<ProductVariant> Variants;

        @ApiModelProperty(name = "图⽚地址 jpg 或者 png 格式图⽚地址不⽀持 WebP 等非常规格式")
        private List<String> ImageUrls;
        @NonNull
        @ApiModelProperty(name = "重量")
        private Double WeightInKg;
    }
}
