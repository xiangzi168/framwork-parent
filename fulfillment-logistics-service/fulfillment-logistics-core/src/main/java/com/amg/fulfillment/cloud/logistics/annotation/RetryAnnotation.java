package com.amg.fulfillment.cloud.logistics.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RetryAnnotation {

    long waitTimeForMilliSeconds() default 1000;

    int retryTimes() default 3;

    String description() default "";

}
