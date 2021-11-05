package com.amg.framework.boot.logging.async;

import com.amg.framework.boot.logging.appender.TraceAppender;
import com.amg.framework.boot.logging.constant.LogTraceConstant;
import com.amg.framework.boot.logging.utils.LogParserUtil;
import com.amg.framework.boot.utils.spring.SpringContextUtil;
import com.amg.framework.cloud.rocketmq.utils.RocketmqUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


public class Handle {

    private static Logger logger = LoggerFactory.getLogger(TraceAppender.class);

    private static final int nThreads = (Runtime.getRuntime().availableProcessors() / 2) + 1;

    private static BlockingQueue<String> linkedBlockingQueue = new LinkedBlockingQueue();

    private static ExecutorService executorService = Executors.newFixedThreadPool(nThreads);

    static {
        for (int i = 0; i < nThreads; i ++) {
            executorService.execute(() -> {
                while (true) {
                    try {
                        String log = takeLogStr();
                        RocketmqUtils rocketmqUtils = SpringContextUtil.getBean(RocketmqUtils.class);
                        rocketmqUtils.asyncSend(LogTraceConstant.LOG_TRACE_TOPIC, log,
                                new SendCallback() {
                            @Override
                            public void onSuccess(SendResult sendResult) {}
                            @Override
                            public void onException(Throwable e) {
                                logger.error(LogParserUtil.parserTraceId(log), e);
                                rocketmqUtils.sendOneWay(LogTraceConstant.LOG_TRACE_TOPIC, log);
                            }
                        });
                    } catch (Exception e) {
                    }
                }
            });
        }
    }

    public static void offerLogStr(String log) {
        linkedBlockingQueue.offer(log);
    }

    public static String takeLogStr() {
        try {
            return linkedBlockingQueue.take();
        } catch (Exception e) {
            return null;
        }
    }

}
