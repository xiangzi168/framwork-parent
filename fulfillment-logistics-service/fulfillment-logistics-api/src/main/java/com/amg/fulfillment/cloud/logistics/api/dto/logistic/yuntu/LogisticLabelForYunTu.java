package com.amg.fulfillment.cloud.logistics.api.dto.logistic.yuntu;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Created by Seraph on 2021/4/29
 */

@Data
@Builder
public class LogisticLabelForYunTu {

    @JSONField(name = "OrderNumbers")
    private List<String> orderNumbers;        //物流系统运单号，客户订单或跟踪号
}
