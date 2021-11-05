package com.amg.framework.boot.canal.annotation;

import com.amg.framework.boot.canal.config.CanalClientConfiguration;
import com.amg.framework.boot.canal.config.CanalConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author lyc
 * @date 2020/10/9 15:24
 * @describe
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({CanalConfig.class, CanalClientConfiguration.class})
public @interface EnableCanalClient {
}
