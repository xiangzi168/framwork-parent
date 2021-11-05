package com.amg.fulfillment.cloud.logistics.config;

import com.alibaba.fastjson.JSON;
import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticOrderDto;
import com.amg.fulfillment.cloud.logistics.dto.logistic.WaybillGoodDetailDto;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPackageDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPackageItemDO;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsPackageItemMapper;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsPackageMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class LogisticsPackageEventListener implements ApplicationListener<LogisticsPackageValidEvent> {
    @Autowired
    private LogisticsPackageMapper logisticsPackageMapper;
    @Autowired
    private LogisticsPackageItemMapper logisticsPackageItemMapper;

    @Async
    @Override
    public void onApplicationEvent(LogisticsPackageValidEvent event) {
        log.info("监听到新的制单事件：对象是：{} ", JSON.toJSONString(event));
        LogisticOrderDto logisticOrderDto = (LogisticOrderDto)event.getSource();
        String salesOrderId = logisticOrderDto.getSalesOrderId();
        Long id = logisticOrderDto.getLogisticsPackageId();
        List<WaybillGoodDetailDto> waybillGoodDetailDtos = logisticOrderDto.getWaybillGoodDetailDtos();
        List<String> itemList = waybillGoodDetailDtos.stream().map(WaybillGoodDetailDto::getItemId).collect(Collectors.toList());
        LambdaQueryWrapper<LogisticsPackageDO> queryWrapperForPackage = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .eq(LogisticsPackageDO::getIsValid, Constant.YES)
                .eq(LogisticsPackageDO::getSalesOrderId, salesOrderId);
        List<LogisticsPackageDO> logisticsPackageDOS_Primary = logisticsPackageMapper.selectList(queryWrapperForPackage);
        if (logisticsPackageDOS_Primary.isEmpty()) {
            return;
        }
        List<LogisticsPackageDO> logisticsPackageDOS = logisticsPackageDOS_Primary.stream().filter(item -> item.getId().compareTo(id) != 0).collect(Collectors.toList());
        for (LogisticsPackageDO logisticsPackageDO : logisticsPackageDOS) {
            LambdaQueryWrapper<LogisticsPackageItemDO> queryWrapperForPackageItem = Wrappers.<LogisticsPackageItemDO>lambdaQuery()
                    .eq(LogisticsPackageItemDO::getPackageId,logisticsPackageDO.getId());
            List<LogisticsPackageItemDO> logisticsPackageItemDOList = logisticsPackageItemMapper.selectList(queryWrapperForPackageItem);
            if (logisticsPackageItemDOList.isEmpty()) {
                continue;
            }
            List<String> sentItemList = logisticsPackageItemDOList.stream().map(LogisticsPackageItemDO::getItemId).collect(Collectors.toList());
            long count = sentItemList.stream().filter(item -> itemList.contains(item)).count();
            if (count < 1) {
                continue;
            }
            // 將该包裹改为“无效”
            LambdaUpdateWrapper<LogisticsPackageDO> updateWrapperForPackage = Wrappers.<LogisticsPackageDO>lambdaUpdate()
                    .set(LogisticsPackageDO::getIsValid, Constant.NO_0)
                    .eq(LogisticsPackageDO::getId, logisticsPackageDO.getId());
            logisticsPackageMapper.update(null,updateWrapperForPackage);
            log.info("监听到新的制单事件：将该包裹【{}】修改成无效,具体内容是：{} ",logisticsPackageDO.getLogisticsOrderNo(),JSON.toJSONString(logisticsPackageDO));
        }
    }
}
