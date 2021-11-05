package com.amg.fulfillment.cloud.logistics.service.impl;

import com.amg.fulfillment.cloud.logistics.api.enumeration.DeliveryPackageTypeEnum;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsTrackNodeDO;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsTrackNodeMapper;
import com.amg.fulfillment.cloud.logistics.model.req.LogisticsTrackNodeReq;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsTrackNodeVO;
import com.amg.fulfillment.cloud.logistics.service.ILogisticsTrackNodeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Seraph on 2021/6/1
 */

@Service
public class LogisticsTrackNodeServiceImpl implements ILogisticsTrackNodeService {

    @Autowired
    private LogisticsTrackNodeMapper logisticsTrackNodeMapper;

    @Override
    public List<LogisticsTrackNodeVO> list(LogisticsTrackNodeReq logisticsTrackNodeReq) {
        LambdaQueryWrapper<LogisticsTrackNodeDO> logisticsTrackNodeDOLambdaQueryWrapper = Wrappers.<LogisticsTrackNodeDO>lambdaQuery()
                .eq(LogisticsTrackNodeDO::getType, logisticsTrackNodeReq.getType());
        if (logisticsTrackNodeReq.getType().equals(DeliveryPackageTypeEnum.ABROAD_DELIVERY_PACKAGE.getType()) && StringUtils.isNotBlank(logisticsTrackNodeReq.getLogisticsCode()))
        {
            logisticsTrackNodeDOLambdaQueryWrapper.eq(LogisticsTrackNodeDO::getLogisticsCode, logisticsTrackNodeReq.getLogisticsCode());
        }

        Set<String> insideEnSet = new HashSet<>();
        List<LogisticsTrackNodeVO> logisticsTrackNodeVOList = new ArrayList<>();
        List<LogisticsTrackNodeDO> logisticsTrackNodeDOList = logisticsTrackNodeMapper.selectList(logisticsTrackNodeDOLambdaQueryWrapper);
        logisticsTrackNodeDOList.forEach(logisticsTrackNodeDO -> {
            if(StringUtils.isNoneBlank(logisticsTrackNodeDO.getInsideEn()) && !insideEnSet.contains(logisticsTrackNodeDO.getLogisticsCode() + logisticsTrackNodeDO.getInsideCn()))
            {
                LogisticsTrackNodeVO logisticsTrackNodeVO = new LogisticsTrackNodeVO();
                logisticsTrackNodeVO.setLogisticsCode(logisticsTrackNodeDO.getLogisticsCode());
                logisticsTrackNodeVO.setNameEn(logisticsTrackNodeDO.getInsideEn());
                logisticsTrackNodeVO.setNameCn(logisticsTrackNodeDO.getInsideCn());
                logisticsTrackNodeVOList.add(logisticsTrackNodeVO);
                insideEnSet.add(logisticsTrackNodeDO.getLogisticsCode() + logisticsTrackNodeDO.getInsideCn());
            }
        });
        return logisticsTrackNodeVOList;
    }

    @Override
    public List<LogisticsTrackNodeDO> getLogisticsTrackNodeList(Integer type) {
        LambdaQueryWrapper<LogisticsTrackNodeDO> logisticsTrackNodeDOLambdaQueryWrapper = Wrappers.<LogisticsTrackNodeDO>lambdaQuery()
                .eq(type != null, LogisticsTrackNodeDO::getType, type);
        return logisticsTrackNodeMapper.selectList(logisticsTrackNodeDOLambdaQueryWrapper);
    }
}
