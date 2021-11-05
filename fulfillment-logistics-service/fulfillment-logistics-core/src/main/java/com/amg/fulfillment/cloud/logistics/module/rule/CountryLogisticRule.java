package com.amg.fulfillment.cloud.logistics.module.rule;

import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticOrderDto;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tom
 * @date 2021-05-24-15:13
 * 订单目的地为指定国家
 */
@ToString(callSuper = true)
public class CountryLogisticRule extends AbstractArrayRuleValidator<CountryProvinceCity,LogisticOrderDto> {

    public CountryLogisticRule(List<CountryProvinceCity> arr) {
        super(arr);
    }

    @Override
    public boolean validate(LogisticOrderDto dto)  {
        if (dto!=null&&dto.getReceiverAddress()!=null){
        String countryCode = dto.getReceiverAddress().getCountryCode();
        return this.getArr().stream().map(CountryProvinceCity::getCountryCode).collect(Collectors.toList()).contains(countryCode);}else{
            return false;
        }
    }
}
