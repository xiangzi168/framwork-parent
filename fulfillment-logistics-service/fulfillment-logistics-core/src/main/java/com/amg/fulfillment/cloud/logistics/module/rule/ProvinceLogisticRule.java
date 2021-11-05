package com.amg.fulfillment.cloud.logistics.module.rule;

import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticOrderDto;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author Tom
 * @date 2021-05-25-15:58
 */
@ToString(callSuper = true)
public class ProvinceLogisticRule extends AbstractProvinceCityValidator<CountryProvinceCity, LogisticOrderDto> {

    public ProvinceLogisticRule(List<CountryProvinceCity> arr) {
        super(arr);
    }

    @Override
    public boolean validate(LogisticOrderDto logisticOrderDto) {
        if (logisticOrderDto != null && logisticOrderDto.getReceiverAddress() != null) {
            String countryCode = logisticOrderDto.getReceiverAddress().getCountryCode();
            String province = logisticOrderDto.getReceiverAddress().getProvince();
            if (StringUtils.isNotEmpty(countryCode) && StringUtils.isNotEmpty(province)) {
                return this.getArr().stream().anyMatch(vo -> vo.getCountryCode().equals(countryCode) && vo.getProvince().equals(province));
            }

        }
        return false;
    }
}
