package com.amg.fulfillment.cloud.logistics.module.rule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.amg.framework.boot.base.exception.GlobalException;
import com.amg.framework.boot.redis.utils.RedisUtils;
import com.amg.framework.boot.utils.security.MD5;
import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.api.util.BeanConvertUtils;
import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticOrderDto;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsChannelDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsRulesDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsRulesFreightDO;
import com.amg.fulfillment.cloud.logistics.enumeration.LogisticMatchRuleTypeEnum;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsRulesFreightMapper;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsRulesMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author Tom
 * @date 2021-05-24-15:30
 * 提供给外界物流规则
 */
@Slf4j
@Component
public class LogisticMatchRuleHandler {

    @Autowired
    private LogisticsRulesMapper logisticsRulesMapper;
    @Autowired
    private LogisticsRulesFreightMapper logisticsRulesFreightMapper;
    @Autowired
    private RedisUtils redisUtils;
    private List<LogisticRuleComposition> rules;
    private List<LogisticRuleFreightMatching> freights;
    private String ruldesMd5;
    private String freightRuldesMd5;

    private static final String MATCHKEY = Constant.LOGISTICS_LOGISTIC_MATCH_RULE + "::100";
    private static final String FREIGHT = Constant.LOGISTICS_LOGISTIC_FREIGHT_RULE + "::100";


    public LogisticsChannelDO getLogisticsChannel(LogisticOrderDto logisticOrderDto) {
        //首先执行物流匹配规则，如果不满足，则执行最低运费规则
        LogisticRuleComposition logisticRuleComposition = this.getFirstSatisfyRule(logisticOrderDto);
        if (logisticRuleComposition != null) {
            LogisticsChannelDO logisticsChannelDO = BeanConvertUtils.copyProperties(logisticRuleComposition, LogisticsChannelDO.class);
            if (logisticsChannelDO != null) {
                BigDecimal firstMinimumFreight = geMinimumFreight(logisticOrderDto, logisticRuleComposition);
                //不为空赋值
                if (firstMinimumFreight != null) {
                    logisticsChannelDO.setTotalPrice(firstMinimumFreight);
                    return logisticsChannelDO;
                }
            }
        }
        //获取最低价格的LogisticRuleFreightMatching;
        LogisticRuleFreightMatching logisticRuleFreightMatching = this.getFirstMinimumFreight(logisticOrderDto);
        if (logisticRuleFreightMatching != null) {
            LogisticsChannelDO logisticsChannelDO = BeanConvertUtils.copyProperties(logisticRuleFreightMatching, LogisticsChannelDO.class);
            logisticsChannelDO.setTotalPrice(logisticRuleFreightMatching.getTotalPrice());
            return logisticsChannelDO;
        }
        return null;
    }

    /**
     * 获取最低总价运费
     *
     * @param logisticRuleComposition
     * @return
     * @auth qiuhao
     */
    public BigDecimal geMinimumFreight(LogisticOrderDto dto, LogisticRuleComposition logisticRuleComposition) {
        redisValueNotEqualsLocalValueToInitFreight();
        if (freights != null) {
            List<LogisticRuleFreightMatching> collect = freights.stream().filter(lr -> lr.getChannelCode().equals(logisticRuleComposition.getChannelCode())).collect(Collectors.toList());
            if (!collect.isEmpty()) {
                List<LogisticRuleFreightMatching> collect1 = collect.stream().filter(vo -> vo.cacl(dto)).sorted(Comparator.comparing(LogisticRuleFreightMatching::getTotalPrice)).collect(Collectors.toList());
                if (collect1 != null && collect1.size() > 0) {
                    LogisticRuleFreightMatching logisticRuleFreightMatching = collect1.get(0);
                    return logisticRuleFreightMatching.getTotalPrice();
                }
            } else {
                //没有匹配到渠道下的规则 返回空
                return null;
            }
        } else {
            //如果为空证明 内存没有加载上来
            log.error("后台加载异常:物流价格计算规则没有加载进内存freigths的值是: {}", freights);
            throw new GlobalException("后台加载异常:物流价格计算规则没有加载进内存", "freights=null");
        }
        return null;
    }

    public LogisticRuleComposition getFirstSatisfyRule(LogisticOrderDto dto) {
        redisValueNotEqualsLocalValueToInit();
        if (rules != null) {
            Stream<LogisticRuleComposition> logisticRuleCompositionStream = rules.stream().filter(vo -> vo.validate(Optional.ofNullable(dto).orElse(new LogisticOrderDto()))).limit(1);
            if (logisticRuleCompositionStream != null) {
                Optional<LogisticRuleComposition> first = logisticRuleCompositionStream.findFirst();
                if (first.isPresent()) {
                    LogisticRuleComposition logisticRuleComposition = first.get();
                    log.info("按level匹配物流规则，找到规则是：{}", JSON.toJSONString(logisticRuleComposition));
                    return logisticRuleComposition;
                }
            }
            return null;
            //LogisticRuleComposition ruleComposition = rules.stream().filter(vo -> vo.validate(Optional.ofNullable(dto).orElse(new LogisticOrderDto()))).limit(1).findFirst().orElse(null);
            //  return ruleComposition;
        } else {
            //如果为空证明 内存没有加载上来
            log.error("后台加载异常:物流价格计算规则没有加载进内存rules的值是: {}", rules);
            throw new GlobalException("后台加载异常:物流价格计算规则没有加载进内存", rules.toString());
        }
    }

    public LogisticRuleFreightMatching getFirstMinimumFreight(LogisticOrderDto dto) {
        redisValueNotEqualsLocalValueToInitFreight();
        if (freights != null) {
            Stream<LogisticRuleFreightMatching> sorted = freights.stream().filter(vo -> vo.cacl(dto)).sorted(Comparator.comparing(LogisticRuleFreightMatching::getTotalPrice));
            if (sorted != null) {
                Optional<LogisticRuleFreightMatching> optional = sorted.findFirst();
                log.info("按最低运费匹配物流规则，找到规则是：{}", optional.orElse(null));
                if (optional.isPresent()) {
                    return optional.get();
                } else {
                    return null;
                }

            }
        } else {
            //如果为空证明 内存没有加载上来
            log.error("后台加载异常:物流价格计算规则没有加载进内存freigths的值是: {}", freights);
            throw new GlobalException("后台加载异常:物流价格计算规则没有加载进内存", freights.toString());
        }
        return null;
    }


    public List<LogisticRuleComposition> getAllSatisfyRule(LogisticOrderDto dto) {
        redisValueNotEqualsLocalValueToInit();
        List<LogisticRuleComposition> ruleCompositions = rules.stream().filter(vo -> vo.validate(Optional.ofNullable(dto).orElse(new LogisticOrderDto()))).collect(Collectors.toList());
        log.debug("匹配所有物流规则，找到规则是：{}", ruleCompositions);
        return ruleCompositions;
    }

    private void redisValueNotEqualsLocalValueToInit() {
        Object hash = redisUtils.get(MATCHKEY);
        if (Objects.isNull(hash) || !ruldesMd5.equals(hash.toString())) {
            init();
        }
    }

    private void redisValueNotEqualsLocalValueToInitFreight() {
        Object hash = redisUtils.get(FREIGHT);
        if (Objects.isNull(hash) || !freightRuldesMd5.equals(hash.toString())) {
            init();
        }
    }

    @PostConstruct
    public void init() {
        try {
            this.rulesInit();       //物流匹配规则初始化
            this.freightInit();       //物流最低运费初始化
        } catch (Exception e) {
            log.error("数据库初始化规则发生错误，错误原因是 {}", e);
            throw new RuntimeException("数据库初始化规则发生错误，错误原因是 " + e);
        }
    }

    private void rulesInit() {
        LambdaQueryWrapper<LogisticsRulesDO> queryWrapper = Wrappers.<LogisticsRulesDO>lambdaQuery()
                .eq(LogisticsRulesDO::getIsDisable, Constant.NO_0);
        List<LogisticsRulesDO> rulesList = logisticsRulesMapper.selectList(queryWrapper);
        ArrayList<LogisticRuleComposition> logisticRuleCompositionLists = new ArrayList<>();
        for (LogisticsRulesDO rule : rulesList) {
            LogisticRuleComposition logisticRuleComposition = createLogisticRuleComposition(rule);
            logisticRuleCompositionLists.add(logisticRuleComposition);
        }
        Collections.sort(logisticRuleCompositionLists);
        this.rules = logisticRuleCompositionLists;
        Object hash = redisUtils.get(MATCHKEY);
        ruldesMd5 = MD5.md5Password(this.rules.toString());
        if (Objects.isNull(hash) || !ruldesMd5.equals(hash.toString())) {
            log.info("数据库初始化规则发生,设置redis值是 {}", ruldesMd5);
            redisUtils.set(MATCHKEY, ruldesMd5);
        }
    }

    private void freightInit() {
        LambdaQueryWrapper<LogisticsRulesFreightDO> logisticsRulesFreightDOLambdaQueryWrapper = Wrappers.<LogisticsRulesFreightDO>lambdaQuery()
                .eq(LogisticsRulesFreightDO::getIsDisable, Boolean.FALSE);
        List<LogisticsRulesFreightDO> logisticsRulesFreightDOList = logisticsRulesFreightMapper.selectList(logisticsRulesFreightDOLambdaQueryWrapper);
        List<LogisticRuleFreightMatching> logisticRuleFreightMatchingList = new ArrayList<>();
        logisticsRulesFreightDOList.forEach(logisticsRulesFreightDO -> {
            LogisticRuleFreightMatching logisticRuleFreightMatching = new LogisticRuleFreightMatching();
            logisticRuleFreightMatching.setName(logisticsRulesFreightDO.getName());
            logisticRuleFreightMatching.setLogisticsCode(logisticsRulesFreightDO.getLogisticsCode());
            logisticRuleFreightMatching.setChannelCode(logisticsRulesFreightDO.getChannelCode());
            logisticRuleFreightMatching.setCountryAbbre(logisticsRulesFreightDO.getCountryAbbre());
            logisticRuleFreightMatching.setStartWeight(logisticsRulesFreightDO.getStartWeight());
            logisticRuleFreightMatching.setEndWeight(logisticsRulesFreightDO.getEndWeight());
            logisticRuleFreightMatching.setPegistrationMoney(logisticsRulesFreightDO.getPegistrationMoney());
            logisticRuleFreightMatching.setUnitPrice(logisticsRulesFreightDO.getUnitPrice());
            logisticRuleFreightMatchingList.add(logisticRuleFreightMatching);
        });

        this.freights = logisticRuleFreightMatchingList;
        Object hash = redisUtils.get(FREIGHT);
        freightRuldesMd5 = MD5.md5Password(this.freights.toString());
        if (Objects.isNull(hash) || !freightRuldesMd5.equals(hash.toString())) {
            log.info("数据库初始化规则发生,设置redis值是 {}", freightRuldesMd5);
            redisUtils.set(FREIGHT, freightRuldesMd5);
        }
    }

    public LogisticRuleComposition createLogisticRuleComposition(LogisticsRulesDO rule) {
        List<LogisticRuleType> typeList = JSON.parseArray(rule.getContent(), LogisticRuleType.class);
        ArrayList<RuleValidator> rules = new ArrayList<>();
        for (LogisticRuleType logisticRuleType : typeList) {
            RuleValidator logisticRule;
            if (LogisticMatchRuleTypeEnum.COUNTRY.getCode().equals(logisticRuleType.getType())) {
                List<CountryProvinceCity> countries = JSON.parseArray(logisticRuleType.getDetail(), CountryProvinceCity.class);
                logisticRule = new CountryLogisticRule(countries);
            } else if (LogisticMatchRuleTypeEnum.PROVINCE.getCode().equals(logisticRuleType.getType())) {
                List<CountryProvinceCity> countryProvinceCities = JSON.parseArray(logisticRuleType.getDetail(), CountryProvinceCity.class);
                logisticRule = new ProvinceLogisticRule(countryProvinceCities);
            } else if (LogisticMatchRuleTypeEnum.CITY.getCode().equals(logisticRuleType.getType())) {
                List<CountryProvinceCity> countryProvinceCities = JSON.parseArray(logisticRuleType.getDetail(), CountryProvinceCity.class);
                logisticRule = new CityLogisticRule(countryProvinceCities);
            } else if (LogisticMatchRuleTypeEnum.POSTCODE.getCode().equals(logisticRuleType.getType())) {
                List<Section<Integer>> postCodes = JSON.parseObject(logisticRuleType.getDetail(), new TypeReference<List<Section<Integer>>>() {
                });
                logisticRule = new PostCodeLogisticRule(postCodes);
            } else if (LogisticMatchRuleTypeEnum.CATEGORY.getCode().equals(logisticRuleType.getType())) {
                List<RuleObjectFormate> categoryLogisticRules = JSON.parseArray(logisticRuleType.getDetail(), RuleObjectFormate.class);
                logisticRule = new CategoryLogisticRule(categoryLogisticRules);
            } else if (LogisticMatchRuleTypeEnum.PRODUCTID.getCode().equals(logisticRuleType.getType())) {
                List<RuleObjectFormate> productids = JSON.parseArray(logisticRuleType.getDetail(), RuleObjectFormate.class);
                logisticRule = new ProductIdLogisticRule(productids);
            } else if (LogisticMatchRuleTypeEnum.LABEL.getCode().equals(logisticRuleType.getType())) {
                List<RuleObjectFormate> labels = JSON.parseArray(logisticRuleType.getDetail(), RuleObjectFormate.class);
                logisticRule = new LableLogisticRule(labels);
            } else if (LogisticMatchRuleTypeEnum.LABEL_All.getCode().equals(logisticRuleType.getType())) {
                List<RuleObjectFormate> labels = JSON.parseArray(logisticRuleType.getDetail(), RuleObjectFormate.class);
                logisticRule = new LableLogisticAllRule(labels);
            } else if (LogisticMatchRuleTypeEnum.WEIGHT.getCode().equals(logisticRuleType.getType())) {
                List<Section<BigDecimal>> weights = JSON.parseObject(logisticRuleType.getDetail(), new TypeReference<List<Section<BigDecimal>>>() {
                });
                logisticRule = new WeightLogisticRule(weights);
            } else if (LogisticMatchRuleTypeEnum.AMOUNT.getCode().equals(logisticRuleType.getType())) {
                List<Section<BigDecimal>> amounts = JSON.parseObject(logisticRuleType.getDetail(), new TypeReference<List<Section<BigDecimal>>>() {
                });
                logisticRule = new AmountLogisticRule(amounts);
            } else if (LogisticMatchRuleTypeEnum.PRICE.getCode().equals(logisticRuleType.getType())) {
                List<Section<BigDecimal>> prices = JSON.parseObject(logisticRuleType.getDetail(), new TypeReference<List<Section<BigDecimal>>>() {
                });
                logisticRule = new PriceLogisticRule(prices);
            } else {
                throw new RuntimeException("初始化规则找到未匹配项，内容是:" + rule);
            }
            rules.add(logisticRule);
        }
        LogisticRuleComposition logisticRuleComposition = new LogisticRuleComposition(rules);
        logisticRuleComposition.setRuleName(rule.getName());
        logisticRuleComposition.setLogisticsCode(rule.getLogisticsCode());
        logisticRuleComposition.setLogisticsName(rule.getLogisticsName());
        logisticRuleComposition.setChannelCode(rule.getChannelCode());
        logisticRuleComposition.setChannelName(rule.getChannelName());
        logisticRuleComposition.setLevel(Objects.isNull(rule.getLevel()) ? Integer.MAX_VALUE : rule.getLevel());
        return logisticRuleComposition;
    }


    @Data
    public static class LogisticRuleType {
        private String type;
        private String detail;
    }
}

