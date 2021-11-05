package com.amg.fulfillment.cloud.logistics.entity;

import lombok.Data;

import java.util.List;

/**
 * @ClassName TesrDO
 * @Description TODO
 * @Author 35112
 * @Date 2021/8/4 17:52
 **/
@Data
public class TestDO {
    private  String logisticsCode;
    private LogisticsRecommendAddressRequest address;
    private List<LogisticsRecommendProductRequest> productList;
}