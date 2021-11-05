package com.amg.fulfillment.cloud.logistics.module.rule;

import lombok.ToString;

import java.util.List;

/**
 * @author Tom
 * @date 2021-05-25-14:51
 */
@ToString
public abstract class AbstractSectionRuleValidator<E extends Number,T> implements RuleValidator<T> {

    private List<Section<E>> list;

    public AbstractSectionRuleValidator(List<Section<E>> list) {
        this.list = list;
    }

    public List<Section<E>> getList() {
        return list;
    }

    public void setList(List<Section<E>> list) {
        this.list = list;
    }
}
