package com.amg.framework.boot.canal.hander;

import com.amg.framework.boot.canal.annotation.CanalEventPoint;
import com.amg.framework.boot.canal.annotation.CanalListener;
import com.amg.framework.boot.canal.entity.Canal;
import com.amg.framework.boot.canal.enums.EventTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author lyc
 * @date 2020/9/27 17:52
 * @describe
 */

public class SimpleOption implements Option {

    private static Logger log = LoggerFactory.getLogger(SimpleMessageHandler.class);

    private List<ListenerPoint> annoListeners = ListenerContext.getAnnoListeners();

    @Override
    public void deleteBefore(Canal canal) {
       doExecute(canal,EventTypeEnum.DELETE.getMsg());
    }

    @Override
    public void updateBefore(Canal canal) {
        doExecute(canal,EventTypeEnum.UPDATE_BEFORE.getMsg());
    }

    @Override
    public void updateAfter(Canal canal) {
        doExecute(canal,EventTypeEnum.UPDATE_AFTER.getMsg());
    }

    @Override
    public void insertAfter(Canal canal) {
        doExecute(canal,EventTypeEnum.INSERT.getMsg());
    }

    private void doExecute(Canal canal,String eventMsg){
        if (!CollectionUtils.isEmpty(annoListeners)) {
            annoListeners.forEach(l -> {
                CanalListener annotation = l.getTarget().getClass().getAnnotation(CanalListener.class);
                // 获取 注解 值
                String[] tables = annotation.table();
                Arrays.stream(tables).forEach(t -> {
                    String table = canal.getTable();
                    if (Objects.equals(table, t)) {
                        Map<Method, CanalEventPoint> invokeMap = l.getInvokeMap();
                        for (Map.Entry<Method, CanalEventPoint> entry : invokeMap.entrySet()) {
                            Method method = entry.getKey();
                            CanalEventPoint listenPoint = entry.getValue();
                            String msg = listenPoint.eventType().getMsg();
                            if (Objects.equals(msg, eventMsg)) {
                                try {
                                    // 调用 对应方法
                                    method.invoke(l.getTarget(), canal);
                                } catch (IllegalAccessException ex) {
                                    log.info("error {}",ex);
                                } catch (InvocationTargetException ex) {
                                    log.info("error {}",ex);
                                }
                            }

                        }
                    }
                });
            });
        }
    }
}
