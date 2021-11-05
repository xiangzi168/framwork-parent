package com.amg.fulfillment.cloud.logistics.service;

import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticOrderDto;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsChannelVO;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsProviderVO;

import java.util.List;

/**
 * Created by Seraph on 2021/5/31
 */
public interface ILogisticsRecommendService {

    /**
     * 查询所有渠道
     * @return 所有渠道信息
     */
    List<LogisticsProviderVO> provider();

    List<LogisticsChannelVO> channel(LogisticOrderDto logisticOrderDto);

}
