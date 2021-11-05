package com.amg.fulfillment.cloud.logistics.module.rule;

import lombok.Data;

/**
 * @author Tom
 * @date 2021-05-24-20:22
 */
@Data
public class Section<T extends Number> {
    private T start;
    private T end;
}
