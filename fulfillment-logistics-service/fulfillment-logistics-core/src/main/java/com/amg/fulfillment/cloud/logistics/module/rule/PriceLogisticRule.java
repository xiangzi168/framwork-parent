package com.amg.fulfillment.cloud.logistics.module.rule;

import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticOrderDto;
import com.amg.fulfillment.cloud.logistics.dto.logistic.WaybillGoodDetailDto;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tom
 * @date 2021-05-25-18:30
 */
@ToString(callSuper = true)
public class PriceLogisticRule extends AbstractSectionRuleValidator<BigDecimal, LogisticOrderDto> {

    public PriceLogisticRule(List<Section<BigDecimal>> list) {
        super(list);
    }

    @Override
    public boolean validate(LogisticOrderDto logisticOrderDto) {
        if (logisticOrderDto != null && logisticOrderDto.getWaybillGoodDetailDtos() != null) {
            List<WaybillGoodDetailDto> waybillGoodDetailDtos = logisticOrderDto.getWaybillGoodDetailDtos();
            List<BigDecimal> matchList = waybillGoodDetailDtos.stream().map(vo -> vo.getSalePriceCny()).collect(Collectors.toList());
            if (matchList != null) {
                for (BigDecimal bigDecimal : matchList) {
                    if (bigDecimal == null) {
                        return false;
                    }
                }
            } else {
                return false;
            }
            List<Section<BigDecimal>> list = this.getList();
            if (list != null) {
                for (Section<BigDecimal> s : list) {
                    if (s == null) {
                        return false;
                    } else {
                        BigDecimal end = s.getEnd();
                        BigDecimal start = s.getStart();
                        if (end == null || start == null) {
                            return false;
                        }
                    }
                }
            } else {
                return false;
            }
            return matchList.stream()
                    .anyMatch(match -> this.getList().stream().anyMatch(standard -> match.compareTo(standard.getStart()) >= 0 && match.compareTo(standard.getEnd()) <= 0));
        }
        return false;
    }
}
