package com.amg.framework.cloud.zuul.strategy;


import com.netflix.zuul.context.RequestContext;



/**
 * Zuul Header 策略 实现并注解声明组件
 */
public interface ZuulHeaderStrategy {

    void apply(RequestContext requestContext);

}
