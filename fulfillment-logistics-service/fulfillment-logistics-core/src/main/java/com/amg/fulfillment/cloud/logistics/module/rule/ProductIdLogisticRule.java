package com.amg.fulfillment.cloud.logistics.module.rule;

import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticOrderDto;
import com.amg.fulfillment.cloud.logistics.dto.logistic.WaybillGoodDetailDto;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tom
 * @date 2021-05-25-11:51
 * 订单内含商品为指定商品ID
 */
@ToString(callSuper = true)
public class ProductIdLogisticRule extends AbstractArrayRuleValidator<RuleObjectFormate, LogisticOrderDto> {

    public ProductIdLogisticRule(List<RuleObjectFormate> arr) {
        super(arr);
    }

    @Override
    public boolean validate(LogisticOrderDto logisticOrderDto) {
        if (logisticOrderDto != null && logisticOrderDto.getWaybillGoodDetailDtos() != null) {
            List<WaybillGoodDetailDto> waybillGoodDetailDtos = logisticOrderDto.getWaybillGoodDetailDtos();
            List<String> productIds = waybillGoodDetailDtos.stream().map(vo -> vo.getItemId()).collect(Collectors.toList());
            List<String> standarList = this.getArr().stream().map(RuleObjectFormate::getProductId).collect(Collectors.toList());
            long count = productIds.stream().filter(id -> standarList.contains(id)).count();
            return count > 0 ? true : false;
        }
        return false;
    }
}
