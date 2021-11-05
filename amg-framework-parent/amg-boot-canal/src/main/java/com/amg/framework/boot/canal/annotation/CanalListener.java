package com.amg.framework.boot.canal.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author lyc
 * @date 2020/10/9 15:11
 * @describe
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface CanalListener {
    String[] table();
}
