package com.amg.fulfillment.cloud.logistics.module.rule;

import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticOrderDto;
import com.amg.fulfillment.cloud.logistics.dto.logistic.WaybillGoodDetailDto;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tom
 * @date 2021-05-25-16:35
 * 订单内含商品为指定类目
 */
@ToString(callSuper = true)
public class CategoryLogisticRule extends AbstractArrayRuleValidator<RuleObjectFormate, LogisticOrderDto> {

    public CategoryLogisticRule(List<RuleObjectFormate> arr) {
        super(arr);
    }

    @Override
    public boolean validate(LogisticOrderDto logisticOrderDto) {
        if (logisticOrderDto != null && logisticOrderDto.getWaybillGoodDetailDtos() != null) {
            List<String> categoryList = new ArrayList<>();
            List<WaybillGoodDetailDto> waybillGoodDetailDtoList = logisticOrderDto.getWaybillGoodDetailDtos();
            waybillGoodDetailDtoList.forEach(waybillGoodDetailDto -> {
                categoryList.addAll(waybillGoodDetailDto.getCategoryCodeList());
            });
            List<String> standardList = this.getArr().stream().map(RuleObjectFormate::getCategory).collect(Collectors.toList());
            return categoryList.stream().anyMatch(vo -> standardList.contains(vo));
        }
        return false;
    }
}
