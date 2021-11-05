package com.amg.fulfillment.cloud.logistics.api.util;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.amg.framework.boot.utils.thread.NamedThreadFactory;
import com.amg.framework.boot.utils.thread.decorator.ThreadPoolExecutorDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.*;

//
///**
// * 线程池工具类
// */
//public class LogisticsThreadPoolUtil {
//
//	private final static Logger LOGGER = LoggerFactory.getLogger(LogisticsThreadPoolUtil.class);
//
//	// 系统核数
//	private final static int SYSTEM_CORE_SIZE = Runtime.getRuntime().availableProcessors();
//
//	// 线程池核心线程数
//	private final static int CORE_POOL_SIZE = SYSTEM_CORE_SIZE;
//	// 线程池最大线程数
//	private final static int MAX_POOL_SIZE = SYSTEM_CORE_SIZE * 2;
//
//	// 线程池空闲时间
//	private final static long KEEP_ALIVE_TIME = 30;
//	// 队列 默认长度2048
//	private final static BlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<Runnable>(2048);
//	// 抛弃策略
//	private final static RejectedExecutionHandler POLICY = new ThreadPoolExecutor.DiscardPolicy();
//	// 线程工厂
//	private final static ThreadFactory THREAD_FACTORY = new LogisticsNamedThreadFactory("common");
//	// 线程池
//	private static ExecutorService threadPoolExecutor;
//
//	private LogisticsThreadPoolUtil() {
//
//	}
//
//	/**
//	 * 获取线程池实例
//	 *
//	 * @return {@link ThreadPoolExecutorDecorator}
//	 */
//	public static ExecutorService getInstance() {
//		if (threadPoolExecutor == null) {
//			synchronized (LogisticsThreadPoolUtil.class) {
//				if (threadPoolExecutor == null) {
//					threadPoolExecutor = new ThreadPoolExecutorDecorator(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
//							TimeUnit.SECONDS, WORK_QUEUE, THREAD_FACTORY, POLICY);
//					threadPoolExecutor = TtlExecutors.getTtlExecutorService(threadPoolExecutor);
//
//					LOGGER.info(
//							"common thread pool created: core_pool_size = {}, max_pool_size = {}, keep_alive_time = {}s, policy = {}",
//							CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, POLICY.getClass().getSimpleName());
//				}
//			}
//		}
//		return threadPoolExecutor;
//	}
//
//	/**
//	 * 执行线程
//	 *
//	 * @param thread 需要执行的线程
//	 */
//	public static void executeThread(Thread thread) {
//		int size = WORK_QUEUE.size();
//		if (size % 10 == 0) {
//			LOGGER.info("now thread queue size is : {}", size);
//		}
//		getInstance().execute(thread);
//	}
//
//	/**
//	 * 执行线程并返回指定结果
//	 *
//	 * @param task {@link Callable<T>}
//	 * @param      <T> T
//	 * @return {@link Future<T>}
//	 */
//	public static <T> Future<T> submitThread(Callable<T> task) {
//		return getInstance().submit(task);
//	}
//
//	public static void shutdown() {
//		getInstance().shutdownNow();
//	}
//}
