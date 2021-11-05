package com.amg.fulfillment.cloud.logistics.api.dto.logistic.yanwen;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Seraph on 2021/4/29
 */

@Data
@Builder
public class LogisticLabelForYanWen {

    private String epCode;
    private String labelSize;
}
