package com.amg.framework.cloud.zuul.filter;

import com.amg.framework.cloud.zuul.handle.Processes;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.stereotype.Component;


/**
 * 请求过滤器
 */
@Component
public class RequestFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        Processes.collect(ctx);
        return null;
    }

}
