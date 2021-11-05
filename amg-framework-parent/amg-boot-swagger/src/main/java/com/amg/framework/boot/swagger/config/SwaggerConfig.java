package com.amg.framework.boot.swagger.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.amg.framework.boot.utils.git.GitUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableSwagger2
@EnableKnife4j
@PropertySource("classpath:swagger.properties")
public class SwaggerConfig implements WebMvcConfigurer {

    @Value("${swagger.enable:false}")
    public boolean isDev;
    
    @Value("${swagger.basePackage:com}")
    private String basePackage;

    @Value("${swagger.basePath:}")
    private String basePath;

    @Value("${swagger.apiTitle:系统API管理}")
    private String apiTitle;

    @Autowired
    private GitUtils gitUtils;


    // 有继承 WebMvcConfigurationSupport 需如下配置（不建议）
    //@Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        if (isDev) {
//            // 开放系统静态资源
//            registry.addResourceHandler("/**")
//                    .addResourceLocations("classpath:/static/");
//            // 开放swagger页面
//            registry.addResourceHandler("/swagger-ui.html")
//                    .addResourceLocations("classpath:/META-INF/resources/");
//            // 开放swagger的js文件
//            registry.addResourceHandler("/webjars/**")
//                    .addResourceLocations("classpath:/META-INF/resources/webjars/");
//        }
//    }


    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(isDev)
                .apiInfo(apiInfo())
                .select().apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .paths(PathSelectors.any())
                .build();
    }


    private ApiInfo apiInfo() {
        String version = StringUtils.isBlank(gitUtils.getBranch()) ? "1.0" : gitUtils.getBranch().replaceAll("origin/", "");
        return new ApiInfoBuilder()
                .title(apiTitle)
                .description("API 版本： " + version)
                .version(version)
                .license("查看word文档")
                .licenseUrl(basePath + "/app/word")
                .build();
    }


    @Bean
    UiConfiguration uiConfig() {
        return new UiConfiguration(null, "list", "alpha", "schema",
                UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS, false, true, 60000L);
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!isDev) {
            registry.addResourceHandler("swagger-ui.html", "/webjars/**")
                    .addResourceLocations("classpath");
        }
    }

}
