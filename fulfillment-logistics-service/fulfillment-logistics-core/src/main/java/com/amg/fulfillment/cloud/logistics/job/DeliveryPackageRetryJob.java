package com.amg.fulfillment.cloud.logistics.job;

import com.amg.fulfillment.cloud.logistics.service.IDeliveryPackageService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Created by Seraph on 2021/5/22
 */
@Slf4j
@JobHandler(value = "deliveryPackage-retry-job")
@Component
public class DeliveryPackageRetryJob extends IJobHandler {

    @Autowired
    private IDeliveryPackageService deliveryPackageService;

    @Override
    public ReturnT<String> execute(String s)  {
        log.info("--------------------------------------开始执行deliveryPackage-retry-job----------------------------------------------");
        this.deliveryPackageService.syncUnfinishedPackageList();
        log.info("--------------------------------------结束执行deliveryPackage-retry-job----------------------------------------------");
        return ReturnT.SUCCESS;
    }
}
