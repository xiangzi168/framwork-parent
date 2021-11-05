package com.amg.fulfillment.cloud.logistics.service.impl;

import com.amg.fulfillment.cloud.logistics.api.util.BeanConvertUtils;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsProviderDO;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsProviderMapper;
import com.amg.fulfillment.cloud.logistics.model.req.LogisticsProviderReq;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsProviderVO;
import com.amg.fulfillment.cloud.logistics.service.ILogisticsProviderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Seraph on 2021/5/25
 */

@Service
public class LogisticsProviderServiceImpl implements ILogisticsProviderService {

    @Autowired
    private LogisticsProviderMapper logisticsProviderMapper;

    @Override
    public List<LogisticsProviderVO> list(LogisticsProviderReq logisticsProviderReq) {

        LambdaQueryWrapper<LogisticsProviderDO> logisticsProviderDOLambdaQueryWrapper = Wrappers.lambdaQuery();

        List<LogisticsProviderDO> logisticsProviderDOList = logisticsProviderMapper.selectList(logisticsProviderDOLambdaQueryWrapper);
        return BeanConvertUtils.copyProperties(logisticsProviderDOList, LogisticsProviderVO.class);
    }
}
