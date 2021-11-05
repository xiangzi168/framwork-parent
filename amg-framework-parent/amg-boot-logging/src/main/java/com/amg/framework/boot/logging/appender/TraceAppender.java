package com.amg.framework.boot.logging.appender;

import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.alibaba.fastjson.JSON;
import com.amg.framework.boot.logging.async.Handle;
import com.amg.framework.boot.logging.config.TraceConfig;
import com.amg.framework.boot.logging.constant.LogTraceConstant;
import com.amg.framework.boot.logging.dto.TraceDTO;
import com.amg.framework.boot.logging.encoder.LayoutEncoder;
import com.amg.framework.boot.logging.utils.LogParserUtil;
import com.amg.framework.boot.utils.date.DateUtils;
import com.amg.framework.boot.utils.spring.SpringContextUtil;
import com.amg.framework.cloud.rocketmq.utils.RocketmqUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import java.util.Calendar;


/**
 * 日志追踪 Appender
 * @param <E>
 */
public class TraceAppender<E> extends UnsynchronizedAppenderBase<E> {

    private static Logger logger = LoggerFactory.getLogger(TraceAppender.class);

    private LayoutEncoder encoder;


    @Override
    protected void append(E eventObject) {
        try {
            TraceConfig traceConfig = SpringContextUtil.getBean(TraceConfig.class);
            boolean enable = traceConfig.isEnable();
            String requestId = MDC.get(LogTraceConstant.TRACE_ID);
            String stageId = MDC.get(LogTraceConstant.TRACE_STAGE_ID);
            String stageParentId = MDC.get(LogTraceConstant.TRACE_STAGE_PARENT_ID);
            String methodName = MDC.get(LogTraceConstant.TRACE_METHOD_NAME);
            String executTime = null;
            if (enable && StringUtils.isNotBlank(requestId) && StringUtils.isNotBlank(methodName)
                    && StringUtils.isNotBlank(stageId) && StringUtils.isNotBlank(stageParentId)) { // 开启追踪
                // 解析日志
                String log = encoder.doEncode(eventObject);
                if (StringUtils.isNotBlank(log) && log.contains("executeTime#")) {
                    executTime = log.split("executeTime#")[1].trim();
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(DateUtils.parseDate(LogParserUtil.parserDate(log), DateUtils.FORMAT_7));

                if (traceConfig.isAsync()) {
                    Handle.offerLogStr(JSON.toJSONString(
                            new TraceDTO(requestId, SpringContextUtil.appName, stageId,
                                    stageParentId, methodName, executTime == null ? 0 : Integer.valueOf(executTime), log, calendar.getTimeInMillis())));
                } else {
                    RocketmqUtils rocketmqUtils = SpringContextUtil.getBean(RocketmqUtils.class);
                    String finalExecutTime = executTime;
                    rocketmqUtils.asyncSend(LogTraceConstant.LOG_TRACE_TOPIC, JSON.toJSON(
                            new TraceDTO(requestId, SpringContextUtil.appName, stageId,
                                    stageParentId, methodName, executTime == null ? 0 : Integer.valueOf(executTime), log, calendar.getTimeInMillis())),
                            new SendCallback() {
                        @Override
                        public void onSuccess(SendResult sendResult) {}
                        @Override
                        public void onException(Throwable e) {
                            logger.error(requestId, e);
                            rocketmqUtils.sendOneWay(LogTraceConstant.LOG_TRACE_TOPIC, JSON.toJSON(
                                    new TraceDTO(requestId, SpringContextUtil.appName, stageId,
                                            stageParentId, methodName, finalExecutTime == null ? 0 : Integer.valueOf(finalExecutTime), log, calendar.getTimeInMillis())));
                        }
                    });
                }
            }
        } catch (Exception e) {
        }
    }


    public LayoutEncoder getEncoder() {
        return encoder;
    }


    public void setEncoder(LayoutEncoder encoder) {
        this.encoder = encoder;
    }

}
