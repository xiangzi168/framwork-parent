package com.amg.fulfillment.cloud.logistics.factory;

import cn.hutool.core.util.StrUtil;
import com.amg.fulfillment.cloud.logistics.enumeration.LogisticTypeEnum;
import com.amg.fulfillment.cloud.logistics.manager.ILogisticManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author Tom
 * @date 2021-04-16-15:50
 * 物流公司工厂类
 */
@Component
public class LogisticFactory {
    @Autowired
    private ApplicationContext applicationContext;


    public ILogisticManager getLogisticTypeFromCode(String code) {
        if (StrUtil.isBlank(code)) {
            throw new RuntimeException("物流公司类型【code】不能为null");
        }
        Class logisticTypeFromCode = LogisticTypeEnum.getLogisticTypeFromCode(code);
        return (ILogisticManager) applicationContext.getBean(logisticTypeFromCode);
    }
}
