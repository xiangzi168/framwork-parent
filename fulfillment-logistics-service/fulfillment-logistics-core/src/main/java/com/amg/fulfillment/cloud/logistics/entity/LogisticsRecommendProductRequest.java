package com.amg.fulfillment.cloud.logistics.entity;

import lombok.Data;

/**
 * @ClassName LogisticsRecommendProductRequest
 * @Description TODO
 * @Author 35112
 * @Date 2021/8/4 18:01
 **/
@Data
public class LogisticsRecommendProductRequest {
    private String itemId;
    private String goodsId;
    private String goodsTitle;
    private String declaredNameEn;
    private String declaredNameCn;
    private String weight;
    private String purchaseId;
    private String categoryCode;
    private String salePriceCny;

}
