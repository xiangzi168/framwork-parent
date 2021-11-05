package com.amg.fulfillment.cloud.logistics.module.rule;

import lombok.ToString;

import java.util.List;

/**
 * @author Tom
 * @date 2021-05-25-16:11
 */
@ToString
public abstract class AbstractProvinceCityValidator<E, T> implements RuleValidator<T> {
    private List<E> arr;

    public AbstractProvinceCityValidator(List<E> arr) {
        this.arr = arr;
    }

    public List<E> getArr() {
        return arr;
    }

    public void setArr(List<E> arr) {
        this.arr = arr;
    }
}
