package com.amg.fulfillment.cloud.logistics.api.dto.wanb;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Created by Seraph on 2021/5/28
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Money {

    @JSONField(name = "Code")
    private String code;
    @JSONField(name = "Value")
    private BigDecimal value;
}
