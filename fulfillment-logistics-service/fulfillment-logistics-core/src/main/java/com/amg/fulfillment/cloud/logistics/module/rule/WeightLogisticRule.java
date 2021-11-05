package com.amg.fulfillment.cloud.logistics.module.rule;

import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticOrderDto;
import com.amg.fulfillment.cloud.logistics.dto.logistic.WaybillGoodDetailDto;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Tom
 * @date 2021-05-24-15:15
 */
@ToString(callSuper = true)
public class WeightLogisticRule extends AbstractSectionRuleValidator<BigDecimal, LogisticOrderDto> {

    public WeightLogisticRule(List<Section<BigDecimal>> list) {
        super(list);
    }

    @Override
    public boolean validate(LogisticOrderDto dto) {
        if (dto != null && dto.getWaybillGoodDetailDtos() != null) {
            List<WaybillGoodDetailDto> waybillGoodDetailDtos = dto.getWaybillGoodDetailDtos();
            BigDecimal weight = waybillGoodDetailDtos.stream().map(vo -> vo.getWeight()).reduce((k, v) -> k.add(v)).orElse(new BigDecimal("0"));
            return this.getList().stream()
                    .anyMatch(standard -> standard.getStart().compareTo(weight) <= 0 && standard.getEnd().compareTo(weight) >= 0);
        }
        return false;
    }

}
