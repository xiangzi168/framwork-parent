package com.amg.framework.boot.utils.thread.decorator;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;


/**
 * 线程池传递 MDC 包装器
 */
public class MDCThreadDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        return decorater(runnable);
    }


    public static Runnable decorater(Runnable runnable) {
        if (runnable == null) throw new NullPointerException();
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return () -> {
            if (contextMap != null) {
                MDC.setContextMap(contextMap);
            }
            runnable.run();
        };
    }


    public static <T> Callable<T> decorater(Callable<T> callable) {
        if (callable == null) throw new NullPointerException();
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return () -> {
            if (contextMap != null) {
                MDC.setContextMap(contextMap);
            }
            return callable.call();
        };
    }


    public static class MDCWrapper<T> implements Supplier<T> {

        private final Supplier<T> delegate;

        private final Map<String, String> mdc;

        public MDCWrapper(Supplier<T> delegate) {
            if (delegate == null) throw new NullPointerException();
            this.delegate = delegate;
            this.mdc = MDC.getCopyOfContextMap();
        }

        @Override
        public T get() {
            if (mdc != null) {
                MDC.setContextMap(mdc);
            }
            return delegate.get();
        }
    }

}
