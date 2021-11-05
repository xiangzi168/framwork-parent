package com.amg.fulfillment.cloud.logistics.factory;

import cn.hutool.core.util.StrUtil;
import com.amg.fulfillment.cloud.logistics.enumeration.DepositoryTypeEnum;
import com.amg.fulfillment.cloud.logistics.manager.IDepositoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author Tom
 * @date 2021-04-12-17:57
 */
@Component
public class DepositoryFactory {

    @Autowired
    private ApplicationContext applicationContext;


    public IDepositoryManager createDepositoryManager(String code) {
        if (StrUtil.isBlank(code)) {
            throw new RuntimeException("仓库类型【code】不能为null");
        }
        Class depositoryTypeFromCode = DepositoryTypeEnum.getDepositoryTypeFromCode(code);
        return (IDepositoryManager) applicationContext.getBean(depositoryTypeFromCode);
    }
}
