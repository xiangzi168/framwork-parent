package com.amg.fulfillment.cloud.logistics.service;

import com.amg.fulfillment.cloud.logistics.entity.LogisticsTrackNodeDO;
import com.amg.fulfillment.cloud.logistics.model.req.LogisticsTrackNodeReq;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsTrackNodeVO;

import java.util.List;

/**
 * Created by Seraph on 2021/6/1
 */
public interface ILogisticsTrackNodeService {

    List<LogisticsTrackNodeVO> list(LogisticsTrackNodeReq logisticsTrackNodeReq);

    List<LogisticsTrackNodeDO> getLogisticsTrackNodeList(Integer type);
}
