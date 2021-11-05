package com.amg.fulfillment.cloud.logistics.config;

import org.springframework.context.ApplicationEvent;

public class LogisticPackageAgainPushEvent extends ApplicationEvent {

    private Object source;

    public LogisticPackageAgainPushEvent(Object source) {
        super(source);
        this.source = source;
    }

    @Override
    public String toString() {
        return source != null ? source.toString() : super.toString();
    }
}
