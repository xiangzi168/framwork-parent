package com.amg.fulfillment.cloud.logistics.module.rule;

import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticOrderDto;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author Tom
 * @date 2021-05-24-15:18
 * 规则对象
 */
@Data
public class LogisticRuleComposition implements RuleValidator<LogisticOrderDto>, Comparable<LogisticRuleComposition> {

    private String id;
    private String ruleName;
    private String logisticsCode;
    private String logisticsName;
    private String channelCode;
    private String channelName;
    private int level;
    private String createTime;
    private List<RuleValidator> list;

    public LogisticRuleComposition(List<RuleValidator> list) {
        this.list = list;
    }

    @Override
    public boolean validate(LogisticOrderDto dto) {
        if (dto != null) {
            if (StringUtils.isNoneBlank(dto.getLogisticCode()) && !dto.getLogisticCode().equals(logisticsCode)) {
                return false;
            }
            for (RuleValidator ruleValidator : list) {
                boolean validate = ruleValidator.validate(dto);
                if (!validate) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(LogisticRuleComposition ruleComposition) {
        if (this.level == ruleComposition.level) {
            throw new RuntimeException("发现两个相同等级的规则，业务不容许这样存在");
        }
        return this.level - ruleComposition.getLevel();
    }
}
