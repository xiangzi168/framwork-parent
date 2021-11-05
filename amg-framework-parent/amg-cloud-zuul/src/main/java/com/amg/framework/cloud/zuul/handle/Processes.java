package com.amg.framework.cloud.zuul.handle;


import com.amg.framework.boot.utils.spring.SpringContextUtil;
import com.netflix.zuul.context.RequestContext;
import com.amg.framework.cloud.zuul.strategy.ZuulHeaderStrategy;

import java.util.Map;


public class Processes {

    public static void collect(RequestContext requestContext) {
        Map<String, ZuulHeaderStrategy> map = SpringContextUtil.getApplicationContext().getBeansOfType(ZuulHeaderStrategy.class);
        for (Map.Entry<String, ZuulHeaderStrategy> entry : map.entrySet()) {
            ZuulHeaderStrategy zuul = entry.getValue();
            if (zuul != null)
                zuul.apply(requestContext);
        }
    }

}
