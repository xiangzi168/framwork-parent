package com.amg.fulfillment.cloud.logistics.service;

import com.amg.fulfillment.cloud.logistics.model.req.LogisticsProviderReq;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsProviderVO;

import java.util.List;

/**
 * Created by Seraph on 2021/5/25
 */
public interface ILogisticsProviderService {

    List<LogisticsProviderVO> list(LogisticsProviderReq logisticsProviderReq);
}
