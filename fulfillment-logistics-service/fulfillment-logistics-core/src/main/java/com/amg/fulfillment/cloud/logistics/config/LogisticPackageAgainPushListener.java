package com.amg.fulfillment.cloud.logistics.config;

import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.api.enumeration.DeliveryPackageDeliveryStatusEnum;
import com.amg.fulfillment.cloud.logistics.api.enumeration.DeliveryPackageLogisticsStatusEnum;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPackageDO;
import com.amg.fulfillment.cloud.logistics.model.req.DeliveryPackageOperationReq;
import com.amg.fulfillment.cloud.logistics.service.IDeliveryPackageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 对于因为获取物流面单超时导致“已制单“情况，进行再次推单。
 */
@Slf4j
@Component
public class LogisticPackageAgainPushListener implements ApplicationListener<LogisticPackageAgainPushEvent> {


    @Autowired
    private IDeliveryPackageService deliveryPackageService;

    @Async
    @Override
    public void onApplicationEvent(LogisticPackageAgainPushEvent event) {
        LogisticsPackageDO packageDO = (LogisticsPackageDO) event.getSource();
        if(packageDO.getLogisticsStatus()== DeliveryPackageLogisticsStatusEnum.CREATED.getCode()
                && packageDO.getDeliveryStatus() == DeliveryPackageDeliveryStatusEnum.PREPARATION.getCode()
                && packageDO.getIsValid() == Constant.YES){
            DeliveryPackageOperationReq deliveryPackageOperationReq = new DeliveryPackageOperationReq();
            deliveryPackageOperationReq.setLogisticsWayBillNo(packageDO.getLogisticsWayBillNo());
            deliveryPackageService.pushWarehouse(deliveryPackageOperationReq);
        }
    }
}
