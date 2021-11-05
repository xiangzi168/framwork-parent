package com.amg.framework.boot.utils.thread.decorator;

import org.springframework.lang.Nullable;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.concurrent.ListenableFuture;
import java.util.Date;
import java.util.concurrent.*;


public class ThreadPoolTaskSchedulerDecorator extends ThreadPoolTaskScheduler {

    @Override
    @Nullable
    public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
        return super.schedule(MDCThreadDecorator.decorater(task), trigger);
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable task, Date startTime) {
        return super.schedule(MDCThreadDecorator.decorater(task), startTime);
    }

    @Override
    public void execute(Runnable task) {
        super.execute(MDCThreadDecorator.decorater(task));
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        execute(MDCThreadDecorator.decorater(task));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(MDCThreadDecorator.decorater(task));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(MDCThreadDecorator.decorater(task));
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        return super.submitListenable(MDCThreadDecorator.decorater(task));
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        return super.submitListenable(MDCThreadDecorator.decorater(task));
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
        return super.scheduleAtFixedRate(MDCThreadDecorator.decorater(task), startTime, period);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
        return super.scheduleAtFixedRate(MDCThreadDecorator.decorater(task), period);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, Date startTime, long delay) {
        return super.scheduleWithFixedDelay(MDCThreadDecorator.decorater(task), startTime, delay);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
        return super.scheduleWithFixedDelay(MDCThreadDecorator.decorater(task), delay);
    }

}
