package com.amg.fulfillment.cloud.logistics.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.amg.framework.boot.base.exception.GlobalException;
import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.api.util.BeanConvertUtils;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsChannelDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsRulesDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsRulesFreightDO;
import com.amg.fulfillment.cloud.logistics.enumeration.BaseLogisticsResponseCodeEnum;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsChannelMapper;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsRulesFreightMapper;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsRulesMapper;
import com.amg.fulfillment.cloud.logistics.model.req.LogisticsRuleAddReq;
import com.amg.fulfillment.cloud.logistics.model.req.LogisticsRuleSearchReq;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsRuleDetailVO;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsRuleVO;
import com.amg.fulfillment.cloud.logistics.module.rule.*;
import com.amg.fulfillment.cloud.logistics.service.ILogisticsRulesFreightService;
import com.amg.fulfillment.cloud.logistics.service.ILogisticsRulesService;
import com.amg.fulfillment.cloud.logistics.util.MetadataPlusUtils;
import com.amg.fulfillment.cloud.order.api.proto.MetadatapbMetadata;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.amg.fulfillment.cloud.logistics.enumeration.ExceptionEnum.REQUEST_PARAM_VALIDAT_EXCEPTION;

/**
 * <p>
 * 物流规则表 服务实现类
 * </p>
 *
 * @author zzx
 * @since 2021-05-24
 */
@Service
@Slf4j
public class LogisticsRulesServiceImpl extends ServiceImpl<LogisticsRulesMapper, LogisticsRulesDO> implements ILogisticsRulesService {

    @Autowired
    private LogisticsRulesMapper logisticsRulesMapper;
    @Autowired
    private LogisticMatchRuleHandler logisticMatchRuleHandler;
    @Autowired
    private MetadataPlusUtils metadataPlusUtils;
    @Autowired
    private LogisticsChannelMapper logisticsChannelMapper;
    @Autowired
    private ILogisticsRulesFreightService logisticsRulesFreightService;

    @CacheEvict(value = Constant.LOGISTICS_LOGISTIC_MATCH_RULE, allEntries = true)
    @Override
    public Integer save(LogisticsRuleAddReq logisticsRuleAddReq) {
        String content = logisticsRuleAddReq.getContent();
        JSONArray objects = JSONArray.parseArray(content);
        ArrayList<String> strings = new ArrayList<>();
        if (objects != null) {
            for (int i = 0; i < objects.size(); i++) {
                JSONObject o = (JSONObject) objects.get(i);
                String type = (String) o.get("type");
                if (type != null && type.equals("country")) {
                    JSONArray jsonArray = (JSONArray) o.get("detail");
                    if (jsonArray != null && jsonArray.size() > 0) {
                        for (int j = 0; j < jsonArray.size(); j++) {
                            JSONObject o1 = (JSONObject) jsonArray.get(j);
                            if (o1 != null) {
                                strings.add((String) o1.get("countryCode"));
                            }
                        }

                    }
                }

            }
        }
        Boolean aBoolean = logisticsRulesFreightService.selectLogisticsRulesFreight(logisticsRuleAddReq.getChannelCode(), strings);
        if (!aBoolean) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "该物流渠道未配置运费规则");
        }
        validatValueFromRemote(logisticsRuleAddReq);
        validatDisableFromChannel(logisticsRuleAddReq);
        LambdaQueryWrapper<LogisticsRulesDO> queryWrapperName = Wrappers.<LogisticsRulesDO>lambdaQuery()
                .eq(LogisticsRulesDO::getName, logisticsRuleAddReq.getName());
        LogisticsRulesDO one = this.getOne(queryWrapperName);
        if (!Objects.isNull(one)) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "名称【" + logisticsRuleAddReq.getName() + "】已存在！  ");
        }
        LogisticsRulesDO logisticsRulesNew = new LogisticsRulesDO();
        BeanUtils.copyProperties(logisticsRuleAddReq, logisticsRulesNew);
        if (Objects.isNull(logisticsRulesNew.getLevel())) {
            Integer level = getLastLevel();
            logisticsRulesNew.setLevel(Objects.isNull(level) ? 1 : level + 1);
            return logisticsRulesMapper.insert(logisticsRulesNew);
        }
        // 计算内部排序
        LambdaQueryWrapper<LogisticsRulesDO> queryWrapper = Wrappers.<LogisticsRulesDO>lambdaQuery()
                .ge(LogisticsRulesDO::getLevel, logisticsRulesNew.getLevel())
                .orderByAsc(LogisticsRulesDO::getLevel);
        updateLevle(logisticsRulesNew, queryWrapper);
        return logisticsRulesMapper.insert(logisticsRulesNew);
    }


    @Override
    public Page<LogisticsRuleVO> list(LogisticsRuleSearchReq logisticsRuleSearchReq) {
        Page<LogisticsRulesDO> page = new Page(logisticsRuleSearchReq.getPage(), logisticsRuleSearchReq.getRow());
        LambdaQueryWrapper<LogisticsRulesDO> queryWrapper = Wrappers.<LogisticsRulesDO>lambdaQuery()
                .eq(!Objects.isNull(logisticsRuleSearchReq.getIsDisable()), LogisticsRulesDO::getIsDisable, logisticsRuleSearchReq.getIsDisable())
                .eq(StrUtil.isNotBlank(logisticsRuleSearchReq.getLogisticsCode()), LogisticsRulesDO::getLogisticsCode, logisticsRuleSearchReq.getLogisticsCode())
                .eq(StrUtil.isNotBlank(logisticsRuleSearchReq.getChannelCode()), LogisticsRulesDO::getChannelCode, logisticsRuleSearchReq.getChannelCode())
                .likeRight(StrUtil.isNotBlank(logisticsRuleSearchReq.getName()), LogisticsRulesDO::getName, logisticsRuleSearchReq.getName())
                .orderByAsc(LogisticsRulesDO::getLevel);
        Page<LogisticsRulesDO> logisticsRulesDOPage = logisticsRulesMapper.selectPage(page, queryWrapper);
        List<LogisticsRulesDO> records = logisticsRulesDOPage.getRecords();
        String jsonString = JSON.toJSONString(records);
        List<LogisticsRuleVO> logisticsRuleVOList = JSON.parseArray(jsonString, LogisticsRuleVO.class);
        Page<LogisticsRuleVO> logisticsRuleVOPage = BeanConvertUtils.copyProperties(logisticsRulesDOPage, Page.class);
        logisticsRuleVOPage.setRecords(logisticsRuleVOList);
        return logisticsRuleVOPage;
    }

    @CacheEvict(value = Constant.LOGISTICS_LOGISTIC_MATCH_RULE, allEntries = true)
    @Override
    public Boolean update(LogisticsRuleAddReq logisticsRuleAddReq) {
        validatValueFromRemote(logisticsRuleAddReq);
        validatDisableFromChannel(logisticsRuleAddReq);
        LogisticsRulesDO oldLogisticsRule = this.getById(logisticsRuleAddReq.getId());
        if (Objects.isNull(oldLogisticsRule)) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "没有找到该数据,或已被删除");
        }
        // 名称检测
        LambdaQueryWrapper<LogisticsRulesDO> queryWrapperName = Wrappers.<LogisticsRulesDO>lambdaQuery()
                .eq(LogisticsRulesDO::getName, logisticsRuleAddReq.getName());
        LogisticsRulesDO one = this.getOne(queryWrapperName);
        if (!Objects.isNull(one) && one.getId().compareTo(oldLogisticsRule.getId()) != 0) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "名称【" + logisticsRuleAddReq.getName() + "】已存在！  ");
        }
        if (logisticsRuleAddReq.getIsDisable() != 1) {
            //判断物流匹配规则
            String content = logisticsRuleAddReq.getContent();
            JSONArray objects = JSONArray.parseArray(content);
            ArrayList<String> strings = new ArrayList<>();
            if (objects != null) {
                for (int i = 0; i < objects.size(); i++) {
                    JSONObject o = (JSONObject) objects.get(i);
                    String type = (String) o.get("type");
                    if (type != null && type.equals("country")) {
                        JSONArray jsonArray = (JSONArray) o.get("detail");
                        if (jsonArray != null && jsonArray.size() > 0) {
                            for (int j = 0; j < jsonArray.size(); j++) {
                                JSONObject o1 = (JSONObject) jsonArray.get(j);
                                if (o1 != null) {
                                    strings.add((String) o1.get("countryCode"));
                                }
                            }

                        }
                    }

                }
            }
            Boolean aBoolean = logisticsRulesFreightService.selectLogisticsRulesFreight(logisticsRuleAddReq.getChannelCode(), strings);
            if (!aBoolean) {
                throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "该物流渠道未配置运费规则");
            }
        }
        LogisticsRulesDO newLogisticsRule = new LogisticsRulesDO();
        BeanUtils.copyProperties(logisticsRuleAddReq, newLogisticsRule);
        int newLevel = !Objects.isNull(logisticsRuleAddReq.getLevel()) ? logisticsRuleAddReq.getLevel() : Integer.MAX_VALUE;
        int oldLevel = oldLogisticsRule.getLevel();
        int flag = newLevel - oldLevel;
        if (flag != 0) {
            if (newLevel == Integer.MAX_VALUE) {
                Integer level = getLastLevel();
                newLogisticsRule.setLevel(Objects.isNull(level) ? 1 : level + 1);
            }
            LambdaQueryWrapper<LogisticsRulesDO> queryWrapper = Wrappers.<LogisticsRulesDO>lambdaQuery()
                    .ge(flag > 0, LogisticsRulesDO::getLevel, newLevel) // level向下移动
                    .between(flag < 0, LogisticsRulesDO::getLevel, newLevel, oldLevel) // level向上移动
                    .orderByAsc(LogisticsRulesDO::getLevel);
            updateLevle(newLogisticsRule, queryWrapper);
        }
        return this.updateById(newLogisticsRule);
    }


    @Override
    public LogisticsRuleDetailVO getLogisticsRuleDetailById(Long id) {
        LogisticsRulesDO logisticsRulesDO = logisticsRulesMapper.selectById(id);
        if (Objects.isNull(logisticsRulesDO)) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "没有找到该数据");
        }
        LogisticsRuleDetailVO detailVO = new LogisticsRuleDetailVO();
        BeanUtils.copyProperties(logisticsRulesDO, detailVO);
        return detailVO;
    }


    private void validatValueFromRemote(LogisticsRuleAddReq logisticsRuleAddReq) {
        LogisticsRulesDO logisticsRulesDO = new LogisticsRulesDO();
        logisticsRulesDO.setContent(logisticsRuleAddReq.getContent());
        LogisticRuleComposition logisticRuleComposition = null;
        try {
            logisticRuleComposition = logisticMatchRuleHandler.createLogisticRuleComposition(logisticsRulesDO);
        } catch (JSONException e) {
            log.info("content内容不是JSON格式，错误原因是：{}", e);
            throw new GlobalException(REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "content内容不是JSON格式，请按正确格式传输数据！");
        }
        List<RuleValidator> list = logisticRuleComposition.getList();
        for (RuleValidator ruleValidator : list) {
            // 验证类目是否存在
            if (ruleValidator instanceof CategoryLogisticRule) {
                CategoryLogisticRule rule = (CategoryLogisticRule) ruleValidator;
                CategoryLogisticParamValidate paramValidate = new CategoryLogisticParamValidate();
                paramValidate.validate(rule.getArr());
            }
        }

    }

    public class CategoryLogisticParamValidate implements RuleValidator<List<RuleObjectFormate>> {
        @Override
        public boolean validate(List<RuleObjectFormate> list) {
            if (list != null && list.size() > 0) {
                for (RuleObjectFormate obj : list) {
                    try {
                        MetadatapbMetadata.CategoryMeta category = metadataPlusUtils.getCategory(obj.getCategory());
                        if (Objects.isNull(category)) {
                            throw new RuntimeException("没有找到对应的类目");
                        }
                    } catch (Exception e) {
                        log.info("验证类型不存,类目id是：{},原因是：{}", obj.getCategory(), e);
                        throw new GlobalException(REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(), "类目id是【" + obj.getCategory() + "】在类目系统查询不到！");
                    }
                }
            }
            return true;
        }
    }


    private Integer getLastLevel() {
        LambdaQueryWrapper<LogisticsRulesDO> queryWrapperLevel = Wrappers.<LogisticsRulesDO>lambdaQuery()
                .orderByDesc(LogisticsRulesDO::getLevel);
        List<LogisticsRulesDO> rulesDOS = logisticsRulesMapper.selectList(queryWrapperLevel);
        LogisticsRulesDO lastlogisticsRules = rulesDOS.stream().findFirst().orElseGet(() -> new LogisticsRulesDO());
        return lastlogisticsRules.getLevel();
    }

    private void updateLevle(LogisticsRulesDO
                                     logisticsRulesNew, LambdaQueryWrapper<LogisticsRulesDO> queryWrapper) {
        List<LogisticsRulesDO> logisticsRuleList = logisticsRulesMapper.selectList(queryWrapper);
        if (!Objects.isNull(logisticsRuleList) && logisticsRuleList.size() > 0) {
            int newLevelUpdate = logisticsRulesNew.getLevel();
            List<LogisticsRulesDO> updateList = new ArrayList<>();
            for (LogisticsRulesDO logisticsRulesDO : logisticsRuleList) {
                if (newLevelUpdate == logisticsRulesDO.getLevel()) {
                    updateList.add(logisticsRulesDO);
                    newLevelUpdate++;
                } else {
                    break;
                }
            }
            if (updateList.size() > 0) {
                LambdaUpdateWrapper<LogisticsRulesDO> updateWrapper = Wrappers.<LogisticsRulesDO>lambdaUpdate()
                        .setSql("level=level+1")
                        .in(LogisticsRulesDO::getId, updateList.stream().map(LogisticsRulesDO::getId).collect(Collectors.toList()));
                logisticsRulesMapper.update(null, updateWrapper);
            }
        }
    }

    private void validatDisableFromChannel(LogisticsRuleAddReq logisticsRuleAddReq) {
        LambdaQueryWrapper<LogisticsChannelDO> queryWrapper = Wrappers.<LogisticsChannelDO>lambdaQuery()
                .eq(LogisticsChannelDO::getLogisticsCode, logisticsRuleAddReq.getLogisticsCode())
                .eq(LogisticsChannelDO::getChannelCode, logisticsRuleAddReq.getChannelCode())
                .eq(LogisticsChannelDO::getIsDisable, Constant.YES);
        LogisticsChannelDO channelDO = logisticsChannelMapper.selectOne(queryWrapper);
        if (!Objects.isNull(channelDO)) {
            throw new GlobalException(REQUEST_PARAM_VALIDAT_EXCEPTION.getCode(),
                    "该物流商【" + logisticsRuleAddReq.getLogisticsCode() + "】下的渠道【" + logisticsRuleAddReq.getChannelCode() + "】已被禁用！请先开启该渠道");
        }
    }
}
