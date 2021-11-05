package com.amg.framework.boot.security.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;


@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Value("${system.security.httpBasic:false}")
    private boolean httpBasic;


    /**
     * 开启 HttpSecurity 策略
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
        http.csrf().disable();
        if (httpBasic) {
            http.authorizeRequests().anyRequest().authenticated().and().httpBasic(); // 开启 HttpBasic 认证
        } else {
            // actuator相关请求要求认证
            http.authorizeRequests().antMatchers("/**/actuator/**").authenticated().anyRequest().permitAll().and().httpBasic();
           // http.authorizeRequests().anyRequest().permitAll().and().logout().permitAll(); // 放行所有请求
        }
    }

}
