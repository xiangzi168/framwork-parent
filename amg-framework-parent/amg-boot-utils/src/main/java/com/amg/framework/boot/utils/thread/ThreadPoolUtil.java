package com.amg.framework.boot.utils.thread;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.amg.framework.boot.utils.thread.decorator.ThreadPoolExecutorDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.*;


/**
 * 线程池工具类
 */
public class ThreadPoolUtil {

	private final static Logger LOGGER = LoggerFactory.getLogger(ThreadPoolUtil.class);

	// 系统核数
	private final static int SYSTEM_CORE_SIZE = Runtime.getRuntime().availableProcessors();

	// 线程池核心线程数
	private final static int CORE_POOL_SIZE = SYSTEM_CORE_SIZE;

	// 线程池最大线程数
	private final static int MAX_POOL_SIZE = SYSTEM_CORE_SIZE * 2;
	
	// 线程池空闲时间
	private final static long KEEP_ALIVE_TIME = 30;

	// 阻塞队列长度
	private final static int QUEUE_SIZE = 5000;

	// 阻塞队列
	private final static BlockingQueue<Runnable> WORK_QUEUE = new LinkedBlockingQueue(QUEUE_SIZE);

	// 拒绝策略 由主线程处理
	private final static RejectedExecutionHandler POLICY = new ThreadPoolExecutor.CallerRunsPolicy();

	// 线程工厂
	private final static ThreadFactory THREAD_FACTORY = new NamedThreadFactory("common");

	// 默认线程池
	private static ExecutorService threadPoolExecutor;

	private ThreadPoolUtil() {

	}

	/**
	 * 获取默认线程池实例
	 *
	 * @return {@link ThreadPoolExecutorDecorator}
	 */
	public static ExecutorService getInstance() {
		if (threadPoolExecutor == null) {
			synchronized (ThreadPoolUtil.class) {
				if (threadPoolExecutor == null) {
					threadPoolExecutor = getDefaultNewInstance();
				}
			}
		}
		return threadPoolExecutor;
	}

	/**
	 * 获取新的线程池实例
	 * @return
	 */
	public static ExecutorService getDefaultNewInstance() {
		return getNewInstance(CORE_POOL_SIZE, MAX_POOL_SIZE);
	}

	/**
	 * 获取新的线程池实例
	 * @param nThreads 核心线程数
	 * @return
	 */
	public static ExecutorService getNewInstance(int nThreads) {
		return getNewInstance(nThreads, nThreads);
	}

	/**
	 * 获取新的线程池实例
	 * @param nThreads 核心线程数
	 * @param maxThreads 最大线程数
	 * @return
	 */
	public static ExecutorService getNewInstance(int nThreads, int maxThreads) {
		ExecutorService threadPoolExecutor = new ThreadPoolExecutorDecorator(nThreads, maxThreads, KEEP_ALIVE_TIME,
				TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(QUEUE_SIZE), THREAD_FACTORY, POLICY);
		threadPoolExecutor = TtlExecutors.getTtlExecutorService(threadPoolExecutor);
		LOGGER.info(
				"common thread pool created: core_pool_size = {}, max_pool_size = {}, keep_alive_time = {}s, policy = {}",
				nThreads, maxThreads, KEEP_ALIVE_TIME, POLICY.getClass().getSimpleName());
		return threadPoolExecutor;
	}

	/**
	 * 获取新的线程池实例
	 * @param nThreads 核心线程数
	 * @param maxThreads 最大线程数
	 * @return
	 */
	public static ExecutorService getNewInstance(int nThreads, int maxThreads, String namedThread) {
		ExecutorService threadPoolExecutor = new ThreadPoolExecutorDecorator(nThreads, maxThreads, KEEP_ALIVE_TIME,
				TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(QUEUE_SIZE), new NamedThreadFactory(namedThread), POLICY);
		threadPoolExecutor = TtlExecutors.getTtlExecutorService(threadPoolExecutor);
		LOGGER.info(
				"common thread pool created: core_pool_size = {}, max_pool_size = {}, keep_alive_time = {}s, policy = {}",
				nThreads, maxThreads, KEEP_ALIVE_TIME, POLICY.getClass().getSimpleName());
		return threadPoolExecutor;
	}

	/**
	 * 获取新的线程池实例
	 * @param nThreads 核心线程数
	 * @param nThreads 最大线程数
	 * @return
	 */
	public static ExecutorService getNewInstance(int nThreads, String namedThread) {
		ExecutorService threadPoolExecutor = new ThreadPoolExecutorDecorator(nThreads, nThreads, KEEP_ALIVE_TIME,
				TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(QUEUE_SIZE), new NamedThreadFactory(namedThread), POLICY);
		threadPoolExecutor = TtlExecutors.getTtlExecutorService(threadPoolExecutor);
		LOGGER.info(
				"common thread pool created: core_pool_size = {}, max_pool_size = {}, keep_alive_time = {}s, policy = {}",
				nThreads, nThreads, KEEP_ALIVE_TIME, POLICY.getClass().getSimpleName());
		return threadPoolExecutor;
	}

	/**
	 * 获取新的线程池实例
	 * @param nThreads 核心线程数
	 * @param maxThreads 最大线程数
	 * @param queueSize 队列长度
	 * @return
	 */
	public static ExecutorService getNewInstance(int nThreads, int maxThreads, int queueSize, String namedThread) {
		ExecutorService threadPoolExecutor = new ThreadPoolExecutorDecorator(nThreads, maxThreads, KEEP_ALIVE_TIME,
				TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(queueSize), new NamedThreadFactory(namedThread), POLICY);
		threadPoolExecutor = TtlExecutors.getTtlExecutorService(threadPoolExecutor);
		LOGGER.info(
				"common thread pool created: core_pool_size = {}, max_pool_size = {}, keep_alive_time = {}s, policy = {}",
				nThreads, maxThreads, KEEP_ALIVE_TIME, POLICY.getClass().getSimpleName());
		return threadPoolExecutor;
	}

	/**
	 * 获取新的线程池实例
	 * @param nThreads 核心线程数
	 * @return
	 */
	public static ExecutorService getNewInstance(int nThreads, int maxThreads, BlockingQueue blockingQueue, String namedThread) {
		ExecutorService threadPoolExecutor = new ThreadPoolExecutorDecorator(nThreads, maxThreads, KEEP_ALIVE_TIME,
				TimeUnit.SECONDS, blockingQueue, new NamedThreadFactory(namedThread), POLICY);
		threadPoolExecutor = TtlExecutors.getTtlExecutorService(threadPoolExecutor);
		LOGGER.info(
				"common thread pool created: core_pool_size = {}, max_pool_size = {}, keep_alive_time = {}s, policy = {}",
				nThreads, maxThreads, KEEP_ALIVE_TIME, POLICY.getClass().getSimpleName());
		return threadPoolExecutor;
	}

	/**
	 * 执行线程
	 *
	 * @param thread 需要执行的线程
	 */
	public static void executeThread(Thread thread) {
		int size = WORK_QUEUE.size();
		if (size % 10 == 0) {
			LOGGER.info("now thread queue size is : {}", size);
		}
		getInstance().execute(thread);
	}

	/**
	 * 执行线程并返回指定结果
	 *
	 * @param task {@link Callable<T>}
	 * @param      <T> T
	 * @return {@link Future<T>}
	 */
	public static <T> Future<T> submitThread(Callable<T> task) {
		return getInstance().submit(task);
	}

	public static void shutdown() {
		getInstance().shutdownNow();
	}
}
