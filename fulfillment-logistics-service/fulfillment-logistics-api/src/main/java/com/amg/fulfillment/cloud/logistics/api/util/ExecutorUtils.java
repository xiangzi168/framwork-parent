package com.amg.fulfillment.cloud.logistics.api.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Seraph on 2021/5/25
 */

@Slf4j
public class ExecutorUtils {


    //	private static final int CORE_POOL_SIZE = 4;//常驻核心线程数
//	private static final int MAX_POOL_SIZE = 30;//总线程数
//	private static ExecutorService EXECUTORS;
    static {
//		EXECUTORS = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    }

    public static <S,T> List<S> reconvert(final BeanConvertUtils<S, T> convert, List<T> list) {
        if(CollectionUtils.isEmpty(list)) {
            return null;
        }
        if(convert == null) {
            return null;
        }
        List<Future<S>> flist = new ArrayList<Future<S>>();
        ExecutorService executors = ExecutorUtils.newFixedThreadPool();
        for(final T t : list) {
            flist.add(executors.submit(new Callable<S>() {
                @Override
                public S call() throws Exception {
                    return convert.reconvert(t);
                }
            }));
        }
        List<S> slist = new ArrayList<S>();
        for(Future<S> f : flist) {
            if(f == null) {
                continue;
            }
            try {
                S s = f.get();
                if(s == null) {
                    continue;
                }
                slist.add(s);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        ExecutorUtils.shutdown(executors);
        return slist;
    }
    public static <S,T> List<T> convert(final BeanConvertUtils<S, T> convert, List<S> list) {
        if(CollectionUtils.isEmpty(list)) {
            return null;
        }
        if(convert == null) {
            return null;
        }
        List<Future<T>> flist = new ArrayList<Future<T>>();
        ExecutorService executors = ExecutorUtils.newFixedThreadPool();
        for(final S s : list) {
            flist.add(executors.submit(new Callable<T>() {
                @Override
                public T call() throws Exception {
                    return convert.convert(s);
                }
            }));
        }
        List<T> tlist = new ArrayList<T>();
        for(Future<T> f : flist) {
            if(f == null) {
                continue;
            }
            try {
                T t = f.get();
                if(t == null) {
                    continue;
                }
                tlist.add(t);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        ExecutorUtils.shutdown(executors);
        return tlist;
    }
    public static ExecutorService newFixedThreadPool() {
        return Executors.newFixedThreadPool(4);
    }
    public static void shutdown(ExecutorService executors) {
        if(executors == null) {
            return ;
        }
        if(!executors.isShutdown()) {
            executors.shutdown();
        }
    }
}
