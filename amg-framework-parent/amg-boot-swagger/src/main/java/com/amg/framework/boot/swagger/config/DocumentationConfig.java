package com.amg.framework.boot.swagger.config;

import com.amg.framework.boot.swagger.filter.ResponseFilter;
import com.amg.framework.boot.swagger.load.LoadCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



@Conditional({LoadCondition.class})
@Configuration
@Primary
public class DocumentationConfig implements SwaggerResourcesProvider {

    @Value("${swagger.resourceList:}") // 目录资源，逗号分割，服务名#服务跟路径(单应用)
    private String[] resourceList;

    @Value("${swagger.isZuul}")
    private boolean isZuul;

    @Autowired
    private ZuulProperties zuulProperties;

    @Autowired
    private Environment environment;

    @Override
    public List<SwaggerResource> get() {
        List resources = new ArrayList<>();
        if (isZuul) {
            resources.add(swaggerResource("网关api接口", "/v2/api-docs", "1.0"));
            for (String str : zuulProperties.getRoutes().keySet()) {
                String path = environment.getProperty("zuul.routes." + str + ".path").replaceAll("\\/\\*\\*", "");
                resources.add(swaggerResource(environment.getProperty("zuul.routes." + str + ".comment"),
                        path + "/v2/api-docs", "1.0"));
            }
        } else {
            for (String s : resourceList) {
                if ("/".equals(s.split(":")[1].trim())) {
                    resources.add(swaggerResource(s.split(":")[0].trim(), "/v2/api-docs", "1.0"));
                } else {
                    resources.add(swaggerResource(s.split(":")[0].trim(), "/v2/api-docs?path=" + s.trim(), "1.0"));
                }
            }
        }
        // resources.add(swaggerResource("调试授权api接口", "/v2/api-docs", "1.0"));
        // resources.add(swaggerResource("用户服务api接口", "/user/rest/api/doc", "1.0"));
        return resources;
    }

    private SwaggerResource swaggerResource(String name, String location, String version) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion(version);
        return swaggerResource;
    }


    @Bean
    @ConditionalOnProperty(name = "swagger.isZuul", havingValue = "false")
    public FilterRegistrationBean ResponseFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new ResponseFilter());
        registrationBean.setUrlPatterns(Arrays.asList("/v2/api-docs"));
        return registrationBean;
    }

}
