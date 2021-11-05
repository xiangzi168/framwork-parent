package com.amg.fulfillment.cloud.logistics.module.rule;

import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticOrderDto;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author Tom
 * @date 2021-05-25-16:20
 * 订单目的地为指定城市
 */
@ToString(callSuper = true)
public class CityLogisticRule extends AbstractProvinceCityValidator<CountryProvinceCity, LogisticOrderDto> {
    public CityLogisticRule(List<CountryProvinceCity> arr) {
        super(arr);
    }

    @Override
    public boolean validate(LogisticOrderDto logisticOrderDto) {
        if (logisticOrderDto != null && logisticOrderDto.getReceiverAddress() != null) {
            String countryCode = logisticOrderDto.getReceiverAddress().getCountryCode();
            String province = logisticOrderDto.getReceiverAddress().getProvince();
            String city = logisticOrderDto.getReceiverAddress().getCity();
            if (StringUtils.isNotEmpty(countryCode) && StringUtils.isNotEmpty(province) && StringUtils.isNotEmpty(city)) {
                return this.getArr().stream()
                        .anyMatch(vo -> vo.getCountryCode().equals(countryCode) && vo.getProvince().equals(province) && vo.getCity().equals(city));
            } else {
                return false;
            }
        }
        return false;
    }
}
