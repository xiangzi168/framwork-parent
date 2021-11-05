package com.amg.framework.boot.system.config;

import com.amg.framework.boot.utils.thread.decorator.MDCThreadDecorator;
import com.amg.framework.boot.utils.thread.decorator.ThreadPoolTaskSchedulerDecorator;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * 系统线程池配置
 */
@Configuration
public class ExecutorConfig implements WebMvcConfigurer, AsyncConfigurer, SchedulingConfigurer {

    /**
     * 异步线程池
     * @return
     */
    @Bean
    public ThreadPoolTaskExecutor asyncExecutor() {
        ThreadPoolTaskExecutor t = new ThreadPoolTaskExecutor();
        t.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        t.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 2);
        t.setQueueCapacity(200);
        t.setKeepAliveSeconds(60);
        t.setAwaitTerminationSeconds(60);
        t.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        t.setTaskDecorator(new MDCThreadDecorator());
        t.setThreadNamePrefix("Async-Thread-");
        t.initialize();
        return t;
    }


    /**
     * 定时任务线程池
     * @return
     */
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskSchedulerDecorator();
        scheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
        scheduler.setThreadNamePrefix("TaskScheduler-Thread-");
        scheduler.setAwaitTerminationSeconds(60);
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        return scheduler;
    }


    /**
     * 异步线程池 & 超时配置
     * @param configurer
     */
    @Override
    public void configureAsyncSupport(final AsyncSupportConfigurer configurer) {
        configurer.setDefaultTimeout(60 * 1000L);
        configurer.registerCallableInterceptors(timeoutInterceptor());
        configurer.setTaskExecutor(asyncExecutor());
    }


    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.setTaskScheduler(taskScheduler());
    }


    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }


    @Bean
    public TimeoutCallableProcessingInterceptor timeoutInterceptor() {
        return new TimeoutCallableProcessingInterceptor();
    }


    @Override
    public Executor getAsyncExecutor() {
        return asyncExecutor();
    }

}
