package com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Tom
 * @date 2021-04-23-17:24
 */
@Data
public class DepositoryPackMsg {

    @JsonProperty(value = "PackOn")
    private String packOn;
}
