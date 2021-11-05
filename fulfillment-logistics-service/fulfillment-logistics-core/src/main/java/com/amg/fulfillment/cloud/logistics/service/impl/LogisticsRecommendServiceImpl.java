package com.amg.fulfillment.cloud.logistics.service.impl;

import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.api.util.BeanConvertUtils;
import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticOrderDto;
import com.amg.fulfillment.cloud.logistics.entity.*;
import com.amg.fulfillment.cloud.logistics.mapper.*;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsChannelVO;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsProviderVO;
import com.amg.fulfillment.cloud.logistics.module.rule.LogisticMatchRuleHandler;
import com.amg.fulfillment.cloud.logistics.service.ILogisticsRecommendService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Seraph on 2021/5/31
 */

@Service
public class LogisticsRecommendServiceImpl implements ILogisticsRecommendService {

    @Autowired
    private LogisticsProviderMapper logisticsProviderMapper;
    @Autowired
    private LogisticsChannelMapper logisticsChannelMapper;

    @Override
    public List<LogisticsProviderVO> provider() {
        //物流商数据
        List<LogisticsProviderDO> logisticsProviderDOList = logisticsProviderMapper.selectList(Wrappers.emptyWrapper());
        List<LogisticsChannelDO> logisticsChannelDOList = logisticsChannelMapper.selectList(Wrappers.emptyWrapper());
        Map<String, List<LogisticsChannelDO>> logisticsChannelDOMap = logisticsChannelDOList.stream().collect(Collectors.groupingBy(LogisticsChannelDO::getLogisticsCode));
        List<LogisticsProviderVO> logisticsProviderVOList = new ArrayList<>();
        logisticsProviderDOList.forEach(logisticsProviderDO -> {
            LogisticsProviderVO logisticsProviderVO = BeanConvertUtils.copyProperties(logisticsProviderDO, LogisticsProviderVO.class);
            List<LogisticsChannelDO> tempLogisticsChannelDOList = Optional.ofNullable(logisticsChannelDOMap.get(logisticsProviderDO.getLogisticsCode())).orElse(new ArrayList<>());
            logisticsProviderVO.setChannelList(this.getLogisticsChannelVOList(tempLogisticsChannelDOList));
            logisticsProviderVOList.add(logisticsProviderVO);
        });
        return logisticsProviderVOList;
    }
    @Override
    public List<LogisticsChannelVO> channel(LogisticOrderDto logisticOrderDto) {
        String logisticsCode = logisticOrderDto.getLogisticCode();
        LambdaQueryWrapper<LogisticsChannelDO> logisticsChannelDOLambdaQueryWrapper = Wrappers.<LogisticsChannelDO>lambdaQuery()
                .eq(LogisticsChannelDO::getIsDisable, Constant.NO_0)
                .eq(LogisticsChannelDO::getLogisticsCode, logisticsCode);
        List<LogisticsChannelDO> logisticsChannelDOList = logisticsChannelMapper.selectList(logisticsChannelDOLambdaQueryWrapper);
        return this.getLogisticsChannelVOList(logisticsChannelDOList);
    }

    private List<LogisticsChannelVO> getLogisticsChannelVOList(List<LogisticsChannelDO> logisticsChannelDOList)
    {
        return BeanConvertUtils.copyProperties(logisticsChannelDOList, LogisticsChannelVO.class);
    }
}
