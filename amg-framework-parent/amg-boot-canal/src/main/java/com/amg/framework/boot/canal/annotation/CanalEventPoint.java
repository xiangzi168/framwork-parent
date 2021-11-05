package com.amg.framework.boot.canal.annotation;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.amg.framework.boot.canal.enums.EventTypeEnum;

import java.lang.annotation.*;

/**
 * @author lyc
 * @date 2020/10/9 15:35
 * @describe
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CanalEventPoint {

    EventTypeEnum eventType() ;
}
