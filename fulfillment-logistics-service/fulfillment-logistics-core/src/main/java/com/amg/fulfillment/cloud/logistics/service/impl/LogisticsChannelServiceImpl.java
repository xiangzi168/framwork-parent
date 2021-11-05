package com.amg.fulfillment.cloud.logistics.service.impl;

import com.amg.framework.boot.base.exception.GlobalException;
import com.amg.framework.boot.redis.utils.RedisUtils;
import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.api.enumeration.OperationalTypeEnum;
import com.amg.fulfillment.cloud.logistics.api.util.BeanConvertUtils;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsChannelDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsProviderDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsRulesDO;
import com.amg.fulfillment.cloud.logistics.enumeration.BaseLogisticsResponseCodeEnum;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsChannelMapper;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsProviderMapper;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsRulesMapper;
import com.amg.fulfillment.cloud.logistics.model.excel.LogisticsChannelExcel;
import com.amg.fulfillment.cloud.logistics.model.req.*;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsChannelVO;
import com.amg.fulfillment.cloud.logistics.service.ILogisticsChannelService;
import com.amg.fulfillment.cloud.logistics.util.FilePlusUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Seraph on 2021/5/24
 */
@Slf4j
@Service
public class LogisticsChannelServiceImpl implements ILogisticsChannelService {

    @Autowired
    private LogisticsChannelMapper logisticsChannelMapper;
    @Autowired
    private LogisticsProviderMapper logisticsProviderMapper;
    @Autowired
    private LogisticsRulesMapper logisticsRulesMapper;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Page<LogisticsChannelVO> list(LogisticsChannelReq logisticsChannelReq) {
        LambdaQueryWrapper<LogisticsChannelDO> logisticsChannelDOLambdaQueryWrapper = this.doConditionHandler(logisticsChannelReq);
        Page<LogisticsChannelDO> selectPage = logisticsChannelMapper.selectPage(new Page(logisticsChannelReq.getPage(), logisticsChannelReq.getRow()), logisticsChannelDOLambdaQueryWrapper);
        List<LogisticsChannelVO> logisticsChannelVOList = new ArrayList<>();
        List<LogisticsChannelDO> logisticsChannelDOList = selectPage.getRecords();
        logisticsChannelDOList.forEach(logisticsChannelDO -> {
            LogisticsChannelVO logisticsChannelVO = BeanConvertUtils.copyProperties(logisticsChannelDO, LogisticsChannelVO.class);
            logisticsChannelVO.setOperationRecord(OperationalTypeEnum.getOperationalTypeEnumByType(logisticsChannelDO.getOperationalBehavior()).getMsg());
            logisticsChannelVOList.add(logisticsChannelVO);
        });
        Page<LogisticsChannelVO> resultPage = BeanConvertUtils.copyProperties(selectPage, Page.class);
        resultPage.setRecords(logisticsChannelVOList);
        return resultPage;
    }

    @Override
    public LogisticsChannelVO detail(Long logisticsChannelId) {
        LogisticsChannelDO logisticsChannelDO = logisticsChannelMapper.selectById(logisticsChannelId);
        if (logisticsChannelDO == null) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "没有找到该数据");
        }
        LogisticsChannelVO logisticsChannelVO = BeanConvertUtils.copyProperties(logisticsChannelDO, LogisticsChannelVO.class);
        logisticsChannelVO.setOperationRecord(OperationalTypeEnum.getOperationalTypeEnumByType(logisticsChannelDO.getOperationalBehavior()).getMsg());
        return logisticsChannelVO;
    }
    @Override
    public Boolean update(LogisticsChannelUpdateReq logisticsChannelUpdateReq) {
        LogisticsChannelDO logisticsChannelDO = logisticsChannelMapper.selectById(logisticsChannelUpdateReq.getId());
        if (logisticsChannelDO == null) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "没有找到该数据");
        }
        //判断是否重复
        LogisticsChannelReq logisticsChannelReq = new LogisticsChannelReq();
        logisticsChannelReq.setId(logisticsChannelUpdateReq.getId());
        logisticsChannelReq.setLogisticsCode(logisticsChannelUpdateReq.getLogisticsCode());
        logisticsChannelReq.setChannelName(logisticsChannelUpdateReq.getChannelName());
        logisticsChannelReq.setChannelCode(logisticsChannelUpdateReq.getChannelCode());
        Boolean existsFlag = this.checkExists(logisticsChannelReq);
        if (existsFlag) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "该渠道已存在");
        }
        List<LogisticsProviderDO> logisticsProviderDOList = logisticsProviderMapper.selectList(Wrappers.emptyWrapper());
        LogisticsProviderDO logisticsProviderDO = logisticsProviderDOList.stream().filter(item -> item.getLogisticsCode().equals(logisticsChannelUpdateReq.getLogisticsCode())).findFirst().orElse(null);
        if (logisticsProviderDO == null) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "物流商数据不存在");
        }
        LogisticsChannelDO tempLogisticsChannelDO = new LogisticsChannelDO();
        tempLogisticsChannelDO.setId(logisticsChannelDO.getId());
        tempLogisticsChannelDO.setLogisticsCode(logisticsChannelUpdateReq.getLogisticsCode());
        tempLogisticsChannelDO.setLogisticsName(logisticsProviderDO.getLogisticsName());    //物流名称
        tempLogisticsChannelDO.setChannelCode(logisticsChannelUpdateReq.getChannelCode());
        tempLogisticsChannelDO.setChannelName(logisticsChannelUpdateReq.getChannelName());
        tempLogisticsChannelDO.setIsDisable(logisticsChannelUpdateReq.getIsDisable());
        tempLogisticsChannelDO.setOperationalBehavior(OperationalTypeEnum.UPDATE.getType());
        int update = logisticsChannelMapper.updateById(tempLogisticsChannelDO);
        cascadeUpdateLogisticMatchRule();
        return update != 0;
    }
    @Override
    public Boolean add(LogisticsChannelAddReq logisticsChannelAddReq) {
        //判断是否重复
        LogisticsChannelReq logisticsChannelReq = new LogisticsChannelReq();
        logisticsChannelReq.setLogisticsCode(logisticsChannelAddReq.getLogisticsCode());
        logisticsChannelReq.setChannelName(logisticsChannelAddReq.getChannelName());
        logisticsChannelReq.setChannelCode(logisticsChannelAddReq.getChannelCode());
        Boolean existsFlag = this.checkExists(logisticsChannelReq);
        if (existsFlag) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "该渠道已存在");
        }
        List<LogisticsProviderDO> logisticsProviderDOList = logisticsProviderMapper.selectList(Wrappers.emptyWrapper());
        LogisticsProviderDO logisticsProviderDO = logisticsProviderDOList.stream().filter(item -> item.getLogisticsCode().equals(logisticsChannelAddReq.getLogisticsCode())).findFirst().orElse(null);
        if (logisticsProviderDO == null) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "物流商数据不存在");
        }
        LogisticsChannelDO logisticsChannelDO = new LogisticsChannelDO();
        logisticsChannelDO.setLogisticsCode(logisticsChannelAddReq.getLogisticsCode());
        logisticsChannelDO.setLogisticsName(logisticsProviderDO.getLogisticsName());    //物流名称
        logisticsChannelDO.setChannelCode(logisticsChannelAddReq.getChannelCode());
        logisticsChannelDO.setChannelName(logisticsChannelAddReq.getChannelName());
        logisticsChannelDO.setIsDisable(logisticsChannelAddReq.getIsDisable());
        logisticsChannelDO.setOperationalBehavior(OperationalTypeEnum.ADD.getType());
        logisticsChannelMapper.insert(logisticsChannelDO);
        return true;
    }

    @Override
    public Boolean checkExists(LogisticsChannelReq logisticsChannelReq) {
        LambdaQueryWrapper<LogisticsChannelDO> logisticsChannelDOLambdaQueryWrapper = Wrappers.<LogisticsChannelDO>lambdaQuery()
                .eq(LogisticsChannelDO::getLogisticsCode, logisticsChannelReq.getLogisticsCode())
                .eq(LogisticsChannelDO::getChannelCode, logisticsChannelReq.getChannelCode()).or().eq(LogisticsChannelDO::getChannelName, logisticsChannelReq.getChannelName());

        List<LogisticsChannelDO> logisticsChannelDOList = logisticsChannelMapper.selectList(logisticsChannelDOLambdaQueryWrapper);
        if (logisticsChannelDOList.size() == 0) {
            return false;       //没有重复
        }
        //判断查询出来的数据 id 是否一样
        LogisticsChannelDO logisticsChannelDO = logisticsChannelDOList.get(0);
        if (logisticsChannelReq.getId() != null && logisticsChannelDO.getId().equals(logisticsChannelReq.getId())) {
            return false;       //没有重复
        }
        return true;
    }

    @Override
    public File exportLogisticsChannelExcel(ExportExcelReq<LogisticsChannelReq> exportExcelReq) {
        List<LogisticsChannelExcel> logisticsChannelExcelList = this.listLogisticsChannel(exportExcelReq);
        return FilePlusUtils.exportExcel(exportExcelReq, logisticsChannelExcelList, LogisticsChannelExcel.class);
    }

    private List<LogisticsChannelExcel> listLogisticsChannel(ExportExcelReq<LogisticsChannelReq> exportExcelReq) {
        LogisticsChannelReq logisticsChannelReq = Optional.ofNullable(exportExcelReq.getData()).orElse(new LogisticsChannelReq());
        LambdaQueryWrapper<LogisticsChannelDO> logisticsChannelDOLambdaQueryWrapper = this.doConditionHandler(logisticsChannelReq);
        if (exportExcelReq.getIdList() != null && exportExcelReq.getIdList().size() != 0) {
            logisticsChannelDOLambdaQueryWrapper.in(LogisticsChannelDO::getId, exportExcelReq.getIdList());
        }
        List<LogisticsChannelDO> logisticsChannelDOList = logisticsChannelMapper.selectList(logisticsChannelDOLambdaQueryWrapper);
        List<LogisticsChannelExcel> logisticsChannelExcelList = new ArrayList<>();
        logisticsChannelDOList.forEach(logisticsChannelDO -> {
            LogisticsChannelExcel logisticsChannelExcel = new LogisticsChannelExcel();
            logisticsChannelExcel.setLogisticsName(logisticsChannelDO.getLogisticsName());
            logisticsChannelExcel.setLogisticsCode(logisticsChannelDO.getLogisticsCode());
            logisticsChannelExcel.setChannelName(logisticsChannelDO.getChannelName());
            logisticsChannelExcel.setChannelCode(logisticsChannelDO.getChannelCode());
            logisticsChannelExcel.setIsDisable(logisticsChannelDO.getIsDisable() == Constant.YES ? "是" : "否");
            logisticsChannelExcel.setOperationalBehavior(OperationalTypeEnum.getOperationalTypeEnumByType(logisticsChannelDO.getOperationalBehavior()).getMsg());
            logisticsChannelExcel.setUpdateBy(logisticsChannelDO.getUpdateBy());
            logisticsChannelExcel.setUpdateTime(DateFormatUtils.format(logisticsChannelDO.getUpdateTime(), "yyyy-MM-dd HH:mm:ss"));
            logisticsChannelExcelList.add(logisticsChannelExcel);
        });
        return logisticsChannelExcelList;
    }

    private LambdaQueryWrapper<LogisticsChannelDO> doConditionHandler(LogisticsChannelReq logisticsChannelReq) {
        LambdaQueryWrapper<LogisticsChannelDO> logisticsChannelDOLambdaQueryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(logisticsChannelReq.getLogisticsCode())) {
            logisticsChannelDOLambdaQueryWrapper.eq(LogisticsChannelDO::getLogisticsCode, logisticsChannelReq.getLogisticsCode());
        }

        if (StringUtils.isNotBlank(logisticsChannelReq.getLogisticsName())) {
            logisticsChannelDOLambdaQueryWrapper.likeRight(LogisticsChannelDO::getLogisticsName, logisticsChannelReq.getLogisticsName());
        }

        if (StringUtils.isNotBlank(logisticsChannelReq.getChannelCode())) {
            logisticsChannelDOLambdaQueryWrapper.eq(LogisticsChannelDO::getChannelCode, logisticsChannelReq.getChannelCode());
        }

        if (StringUtils.isNotBlank(logisticsChannelReq.getChannelName())) {
            logisticsChannelDOLambdaQueryWrapper.likeRight(LogisticsChannelDO::getChannelName, logisticsChannelReq.getChannelName());
        }

        if (StringUtils.isNotBlank(logisticsChannelReq.getLogisticsProvider())) {
            logisticsChannelDOLambdaQueryWrapper.likeRight(LogisticsChannelDO::getLogisticsCode, logisticsChannelReq.getLogisticsProvider()).or().likeRight(LogisticsChannelDO::getLogisticsName, logisticsChannelReq.getLogisticsProvider());
        }

        if (StringUtils.isNotBlank(logisticsChannelReq.getLogisticsChannel())) {
            logisticsChannelDOLambdaQueryWrapper.likeRight(LogisticsChannelDO::getChannelCode, logisticsChannelReq.getLogisticsChannel()).or().likeRight(LogisticsChannelDO::getChannelName, logisticsChannelReq.getLogisticsChannel());
        }

        if (logisticsChannelReq.getIsDisable() != null) {
            logisticsChannelDOLambdaQueryWrapper.eq(LogisticsChannelDO::getIsDisable, logisticsChannelReq.getIsDisable());
        }

        logisticsChannelDOLambdaQueryWrapper.orderByDesc(LogisticsChannelDO::getUpdateTime);      //按照操作记录的最新时间倒序排列
        return logisticsChannelDOLambdaQueryWrapper;
    }


    private void cascadeUpdateLogisticMatchRule() {
        LambdaQueryWrapper<LogisticsChannelDO> queryWrapperForChannel = Wrappers.<LogisticsChannelDO>lambdaQuery()
                .eq(LogisticsChannelDO::getIsDisable, Constant.YES);
        List<LogisticsChannelDO> channelDOS = logisticsChannelMapper.selectList(queryWrapperForChannel);
        if (channelDOS.isEmpty()) {
            return;
        }
        List<LogisticsRulesDO> ruleList = new ArrayList<>();
        channelDOS.stream().forEach(channel -> {
            LambdaQueryWrapper<LogisticsRulesDO> queryWrapperForRule = Wrappers.<LogisticsRulesDO>lambdaQuery()
                    .eq(LogisticsRulesDO::getLogisticsCode, channel.getLogisticsCode())
                    .eq(LogisticsRulesDO::getChannelCode, channel.getChannelCode())
                    .eq(LogisticsRulesDO::getIsDisable, Constant.NO_0);
            List<LogisticsRulesDO> rulesDOs = logisticsRulesMapper.selectList(queryWrapperForRule);
            if (!Objects.isNull(rulesDOs) && !rulesDOs.isEmpty()) {
                ruleList.addAll(rulesDOs);
            }
        });
        if (ruleList.isEmpty()) {
            return;
        }
        List<Long> idList = ruleList.stream().map(LogisticsRulesDO::getId).collect(Collectors.toList());
        LambdaUpdateWrapper<LogisticsRulesDO> updateWrapper = Wrappers.<LogisticsRulesDO>lambdaUpdate()
                .set(LogisticsRulesDO::getIsDisable, Constant.YES)
                .in(LogisticsRulesDO::getId, idList);
        logisticsRulesMapper.update(null, updateWrapper);
        //删除缓存
        log.info("---------------------渠道发生禁用变化删除缓存-----------------");
        redisUtils.removePattern(Constant.LOGISTICS_LOGISTIC_FREIGHT_RULE);
        redisUtils.removePattern(Constant.LOGISTICS_LOGISTIC_MATCH_RULE);
    }

}
