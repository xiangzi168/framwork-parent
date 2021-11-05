package com.amg.fulfillment.cloud.logistics.module.rule;

public interface RuleValidator<T> {

     boolean validate(T t);
}
