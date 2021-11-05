package com.amg.fulfillment.cloud.logistics.module.rule;

import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticOrderDto;
import com.amg.fulfillment.cloud.logistics.dto.logistic.WaybillGoodDetailDto;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Tom
 * @date 2021-05-24-15:15
 * 订单的付款金额为指定的范围
 */
@ToString(callSuper = true)
public class AmountLogisticRule extends AbstractSectionRuleValidator<BigDecimal, LogisticOrderDto> {

    public AmountLogisticRule(List<Section<BigDecimal>> list) {
        super(list);
    }

    @Override
    public boolean validate(LogisticOrderDto dto) {
        if (dto != null) {
            List<WaybillGoodDetailDto> waybillGoodDetailDtos = dto.getWaybillGoodDetailDtos();
            if (waybillGoodDetailDtos != null && waybillGoodDetailDtos.size() > 0) {
                if (waybillGoodDetailDtos != null && waybillGoodDetailDtos.size() > 0) {
                    BigDecimal amount = new BigDecimal(0);
                    for (WaybillGoodDetailDto waybillGoodDetailDto : waybillGoodDetailDtos) {
                        if (waybillGoodDetailDto != null) {
                            BigDecimal salePriceCny = waybillGoodDetailDto.getSalePriceCny();
                            if (salePriceCny != null) {
                                amount = amount.add(salePriceCny);
                            }
                        }
                    }
                    BigDecimal finalAmount = amount;
                    return this.getList().stream().anyMatch(vo -> vo.getStart().compareTo(finalAmount) <= 0 && vo.getEnd().compareTo(finalAmount) >= 0);
                    //  amount = waybillGoodDetailDtos.stream().map(vo -> vo.getSalePriceCny()).reduce((k, v) -> k.add(v)).orElse(new BigDecimal("0"));
                }
            }
        }
        return false;
    }

}
