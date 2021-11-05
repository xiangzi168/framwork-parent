package com.amg.framework.boot.swagger.load;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class LoadCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        try {
            return Boolean.valueOf(conditionContext.getEnvironment().getProperty("swagger.isZuul"));
        } catch (Exception e) {
        }
        return false;
    }

}
