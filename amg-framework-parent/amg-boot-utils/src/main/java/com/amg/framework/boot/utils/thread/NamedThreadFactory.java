package com.amg.framework.boot.utils.thread;

import com.amg.framework.boot.utils.thread.decorator.MDCThreadDecorator;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 多线程命名工厂
 * 
 */
public class NamedThreadFactory implements ThreadFactory {

	private final ThreadGroup group;
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	private final String namePrefix;

	NamedThreadFactory(String name) {
		SecurityManager s = System.getSecurityManager();
		group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
		namePrefix = name + "-thread-pool-";
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
		if (t.isDaemon()) {
			t.setDaemon(false);
		}
		if (t.getPriority() != Thread.NORM_PRIORITY) {
			t.setPriority(Thread.NORM_PRIORITY);
		}
		return t;
	}
}
