package com.amg.fulfillment.cloud.logistics.module.rule;

import com.amg.fulfillment.cloud.logistics.dto.depository.AddressDto;
import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticOrderDto;
import com.amg.fulfillment.cloud.logistics.dto.logistic.WaybillGoodDetailDto;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created by Seraph on 2021/5/31
 */

@Data
public class LogisticRuleFreightMatching {

    private String name;
    private String logisticsCode;
    private String channelCode;
    private String countryAbbre;
    private BigDecimal startWeight;
    private BigDecimal endWeight;
    private BigDecimal pegistrationMoney;
    //物流单价
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    public Boolean cacl(LogisticOrderDto logisticOrderDto)
    {
        String tempLogisticsCode = logisticOrderDto.getLogisticCode();
        AddressDto addressDto = logisticOrderDto.getReceiverAddress();
        List<WaybillGoodDetailDto> waybillGoodDetailDtoList = logisticOrderDto.getWaybillGoodDetailDtos();
        if(StringUtils.isNotBlank(tempLogisticsCode) && !tempLogisticsCode.equals(logisticsCode))     //如果选择了物流商
        {
            return false;
        }

        if(!addressDto.getCountryCode().equals(countryAbbre))       //如果国家不符合，过滤
        {
            return false;
        }
        //总重量
        BigDecimal totalWeight = waybillGoodDetailDtoList.stream().map(WaybillGoodDetailDto::getWeight).reduce(BigDecimal.ZERO, BigDecimal::add);
        if(totalWeight.compareTo(startWeight) < 0 || totalWeight.compareTo(endWeight) > 0)        //重量不符合
        {
            return false;
        }

        totalPrice = totalWeight.multiply(unitPrice);
        totalPrice = totalPrice.divide(new BigDecimal(1000));
        totalPrice = totalPrice.add(pegistrationMoney);
        totalPrice =totalPrice.setScale(2, BigDecimal.ROUND_DOWN);
        return true;
    }
}
