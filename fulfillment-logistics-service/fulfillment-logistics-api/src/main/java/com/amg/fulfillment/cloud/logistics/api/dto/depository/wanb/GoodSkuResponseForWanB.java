package com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb;

import com.amg.fulfillment.cloud.logistics.api.dto.depository.ProductVariant;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @author Tom
 * @date 2021-04-13-15:54
 */
@ApiModel(value = "万邦仓库返回产品资料SKU对象")
@Data
public class GoodSkuResponseForWanB extends AbstractResponseMsg<GoodSkuResponseForWanB>{

    private Good Data = new Good();

    @Data
    public static class Good {
        private String SKU;
        private String Name;
        private List<ProductVariant> Variants;
        private List<String> ImageUrls;
        private double WeightInKg;
        private String StockReplenishMode;
        private String CreateOn;
    }

}
