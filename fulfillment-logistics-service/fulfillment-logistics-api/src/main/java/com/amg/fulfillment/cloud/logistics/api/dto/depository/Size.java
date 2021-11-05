package com.amg.fulfillment.cloud.logistics.api.dto.depository;

import lombok.Data;

@Data
public class Size {
    private Double length;
    private Double width;
    private Double height;
    private String unit;
}
