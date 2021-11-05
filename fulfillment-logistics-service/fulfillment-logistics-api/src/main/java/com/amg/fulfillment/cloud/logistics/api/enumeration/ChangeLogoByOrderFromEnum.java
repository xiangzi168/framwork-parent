package com.amg.fulfillment.cloud.logistics.api.enumeration;

import com.amg.fulfillment.cloud.logistics.api.dto.depository.ProductVariant;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ChangeLogoByOrderFromEnum {
    HPIOS("HPIOS"),
    HPANDROID("HPANDROID"),
    HPWEB("HPWEB")
    ;

    private String orderFrom;
    ChangeLogoByOrderFromEnum(String orderFrom) {
        this.orderFrom = orderFrom;
    }

    public static boolean validateChangeLogoByOrderFrom(String orderFrom) {
        return Arrays.stream(ChangeLogoByOrderFromEnum.values()).anyMatch(item -> item.getOrderFrom().equals(orderFrom));
    }

    public static ProductVariant getChangeLogoVariants() {
       return new ProductVariant("包装","不带logo包装");
    }

}
