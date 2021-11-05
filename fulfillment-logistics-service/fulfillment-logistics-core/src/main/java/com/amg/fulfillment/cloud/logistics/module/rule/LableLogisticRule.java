package com.amg.fulfillment.cloud.logistics.module.rule;

import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticOrderDto;
import com.amg.fulfillment.cloud.logistics.dto.logistic.WaybillGoodDetailDto;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tom
 * @date 2021-05-25-16:37
 * 订单内含商品为指定标签
 */
@ToString(callSuper = true)
public class LableLogisticRule extends AbstractArrayRuleValidator<RuleObjectFormate, LogisticOrderDto> {

    public LableLogisticRule(List<RuleObjectFormate> arr) {
        super(arr);
    }

    @Override
    public boolean validate(LogisticOrderDto logisticOrderDto) {
        if (logisticOrderDto != null && logisticOrderDto.getWaybillGoodDetailDtos() != null && logisticOrderDto.getWaybillGoodDetailDtos().size() > 0) {
            List<List<Long>> labelList = logisticOrderDto.getWaybillGoodDetailDtos().stream().map(WaybillGoodDetailDto::getLabelIdList).collect(Collectors.toList());
            List<Long> idList = this.getArr().stream().map(RuleObjectFormate::getId).collect(Collectors.toList());
            if (labelList != null && labelList.size() > 0 && idList != null && idList.size() > 0) {
                return labelList.stream().flatMapToLong(v -> v.stream().mapToLong(item -> item)).anyMatch(item -> idList.contains(item));
            }
        }
        return false;
    }
}
