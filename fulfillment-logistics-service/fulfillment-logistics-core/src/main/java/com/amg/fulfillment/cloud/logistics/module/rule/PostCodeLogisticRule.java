package com.amg.fulfillment.cloud.logistics.module.rule;

import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticOrderDto;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author Tom
 * @date 2021-05-25-16:22
 * 订单目的地为指定邮编
 */
@Slf4j
@ToString(callSuper = true)
public class PostCodeLogisticRule extends AbstractSectionRuleValidator<Integer, LogisticOrderDto> {

    public PostCodeLogisticRule(List<Section<Integer>> list) {
        super(list);
    }

    @Override
    public boolean validate(LogisticOrderDto logisticOrderDto) {
        if (logisticOrderDto != null && logisticOrderDto.getReceiverAddress() != null) {
            String postCode = logisticOrderDto.getReceiverAddress().getPostCode();
            if (StringUtils.isNotEmpty(postCode)) {
                Integer intPostCode;
                try {
                    intPostCode = Integer.valueOf(postCode);
                } catch (NumberFormatException e) {
                    log.error("物流规则配置邮编转换异常，邮编是{}，异常是{}", postCode, e);
                    return false;
                }
                return this.getList().stream().anyMatch(vo -> vo.getStart() <= intPostCode && intPostCode <= vo.getEnd());
            }
        }
        return false;
    }
}
