package com.amg.fulfillment.cloud.logistics.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * 物流包裹发布事件
 */
@Slf4j
public class LogisticsPackageValidEvent extends ApplicationEvent {

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    private Object source;

    public LogisticsPackageValidEvent(Object source) {
        super(source);
        this.source = source;
    }

    @Override
    public String toString() {
        return source != null ? source.toString() : super.toString();
    }
}
