package com.amg.fulfillment.cloud.logistics.aop;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.amg.fulfillment.cloud.logistics.annotation.RetryAnnotation;
import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticPrintLabelResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
public class RetryHandlerAop {

    @Autowired
    private RestTemplate restTemplate;

    @Pointcut(value = "@annotation(com.amg.fulfillment.cloud.logistics.annotation.RetryAnnotation)")
    public void retryPiont() {
    }

    @Around("retryPiont()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        log.debug(" RetryHandlerAop 开始时间： {}", LocalDateTime.now());
        long start=System.currentTimeMillis();
        MethodInvocationProceedingJoinPoint mjp = (MethodInvocationProceedingJoinPoint) pjp;
        MethodSignature signature = (MethodSignature) mjp.getSignature();
        Object mjpTarget = mjp.getTarget();
        RetryAnnotation retryAnnotation = signature.getMethod().getAnnotation(RetryAnnotation.class);
        long waitTime = retryAnnotation.waitTimeForMilliSeconds();
        int times = retryAnnotation.retryTimes();
        for (int i = 0; i < times - 1; i++) {
            log.info("RetryAnnotation  运行第【{}】次数，运行的类名是：{},当前时间是：{}",(i+1),mjpTarget, DateUtil.now());
            try {
                Object obj = pjp.proceed();
                if (obj instanceof LogisticPrintLabelResponseDto) {
                    LogisticPrintLabelResponseDto labelResponseDto = (LogisticPrintLabelResponseDto) obj;
                    String logisticsLabel = labelResponseDto.getLogisticsLabel();
                    if (StringUtils.isNotBlank(logisticsLabel)) {
                        // 因为验证面单是否可以成功打开耗时2000ms,注释掉
//                        log.info("RetryAnnotation  运行第【{}】次数，验证面单是否可以成功打开，地址是：{}",(i+1),logisticsLabel);
//                        ResponseEntity<String> entity = restTemplate.getForEntity(logisticsLabel, String.class);
//                        if (entity.getStatusCode()== HttpStatus.OK) {
//                            long end1=System.currentTimeMillis();
//                            log.info(" RetryHandlerAop 结束时间end1： {}，花费：{}", LocalDateTime.now(), end1 - start);
//                            return obj;
//                        }
                        log.info("RetryAnnotation 获取面单对象是：{}", JSON.toJSONString(obj));
                        return obj;
                    }
                }
            } catch (Throwable throwable) {
                log.error("RetryAnnotation  发送异常，异常信息是：{}",throwable);
            }
            long end2=System.currentTimeMillis();
            log.debug(" RetryHandlerAop 结束时间end2： {}，花费：{}", LocalDateTime.now(), end2 - start);
            log.info("RetryAnnotation  运行第【{}】次数，运行的类名是：{},当前时间是：{},睡眠：{}-ms", (i + 1), mjpTarget, DateUtil.now(), waitTime);
            TimeUnit.MILLISECONDS.sleep(waitTime);
        }
        log.info("RetryAnnotation  运行第【{}】次数，运行的类名是：{},当前时间是：{}",times,mjpTarget, DateUtil.now());
        return pjp.proceed();
    }
}
