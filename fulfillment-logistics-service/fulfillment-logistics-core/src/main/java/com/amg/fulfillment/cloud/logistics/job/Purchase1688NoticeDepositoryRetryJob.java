package com.amg.fulfillment.cloud.logistics.job;

import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPurchasePackageDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPurchasePackageErrorProductDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPurchaseProductDO;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsPurchasePackageErrorProductMapper;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsPurchasePackageMapper;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsPurchaseProductMapper;
import com.amg.fulfillment.cloud.logistics.model.bo.LogisticsPurchasePackageBO;
import com.amg.fulfillment.cloud.logistics.model.req.PurchasePackagePredictionReq;
import com.amg.fulfillment.cloud.logistics.mq.consumer.Purchase1688NoticeDepositoryConsumer;
import com.amg.fulfillment.cloud.logistics.service.IPurchasePackageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 重新推送失败的1688包裹单到仓库
 */
@Slf4j
@JobHandler(value = "depository-1688purchase-retry-job")
@Component
public class Purchase1688NoticeDepositoryRetryJob extends IJobHandler {

    @Autowired
    private LogisticsPurchasePackageMapper logisticsPurchasePackageMapper;
    @Autowired
    private LogisticsPurchaseProductMapper logisticsPurchaseProductMapper;
    @Autowired
    private LogisticsPurchasePackageErrorProductMapper logisticsPurchasePackageErrorProductMapper;
    @Autowired
    private IPurchasePackageService purchasePackageService;
    @Autowired
    private Purchase1688NoticeDepositoryConsumer purchase1688NoticeDepositoryConsumer;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        log.info("--------------------------------------开始执行1688采购推送失败订单任务----------------------------------------------");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LambdaQueryWrapper<LogisticsPurchasePackageDO> queryWrapperForPackage = Wrappers.<LogisticsPurchasePackageDO>lambdaQuery()
                .eq(LogisticsPurchasePackageDO::getPrediction, Constant.NO_0)
                .eq(LogisticsPurchasePackageDO::getValid, Boolean.TRUE)
                .between(LogisticsPurchasePackageDO::getCreateTime, LocalDateTime.now().minusMonths(3L), LocalDateTime.now());
        List<LogisticsPurchasePackageDO> packageDOList = logisticsPurchasePackageMapper.selectList(queryWrapperForPackage);
        if (Objects.isNull(packageDOList) && packageDOList.size() < 1) {
            return ReturnT.SUCCESS;
        }
        for (LogisticsPurchasePackageDO item : packageDOList) {
            LambdaQueryWrapper<LogisticsPurchasePackageErrorProductDO> queryWrapperForError = Wrappers.<LogisticsPurchasePackageErrorProductDO>lambdaQuery()
                    .eq(LogisticsPurchasePackageErrorProductDO::getPurchasePackageId, item.getId());
            List<LogisticsPurchasePackageErrorProductDO> errorProductDOS = logisticsPurchasePackageErrorProductMapper.selectList(queryWrapperForError);
            if (Objects.isNull(errorProductDOS) || errorProductDOS.size() < 1) {
                // 将空包（没有itemId）的数据设置成无效
                LambdaUpdateWrapper<LogisticsPurchasePackageDO> updateWrapperForPackage = Wrappers.<LogisticsPurchasePackageDO>lambdaUpdate()
                        .set(LogisticsPurchasePackageDO::getValid,Boolean.FALSE)
                        .eq(LogisticsPurchasePackageDO::getId,item.getId());
                try {
                    logisticsPurchasePackageMapper.update(null, updateWrapperForPackage);
                } catch (Exception e) {
                    log.error("将空包（没有itemId）的数据设置成无效 error update ~~~~~~~~ {},原因：{}", item.getId(), e);
                }
                continue;
            }
            List<Long> predictionList = new ArrayList<>();
            long purchaseIdIsNullCount = errorProductDOS.stream().filter(vo -> StringUtils.isBlank(vo.getPurchaseId())).count();
            if (purchaseIdIsNullCount > 0) {
                // 需要再次查询1688分配采购订单
                LambdaQueryWrapper<LogisticsPurchaseProductDO> queryWrapperForProduct = Wrappers.<LogisticsPurchaseProductDO>lambdaQuery()
                        .eq(LogisticsPurchaseProductDO::getPackageId, item.getId());
                List<LogisticsPurchaseProductDO> productDOList = logisticsPurchaseProductMapper.selectList(queryWrapperForProduct);

                List<LogisticsPurchasePackageBO.LogisticsProductBO> productBOList = productDOList.stream().map(product -> {
                    LogisticsPurchasePackageBO.LogisticsProductBO logisticsProductBO = new LogisticsPurchasePackageBO.LogisticsProductBO();
                    logisticsProductBO.setId(product.getId());
                    logisticsProductBO.setPackageId(product.getPackageId());
                    logisticsProductBO.setSkuChannel(product.getSkuChannel());
                    logisticsProductBO.setProductCount(product.getProductCount());
                    return logisticsProductBO;
                }).collect(Collectors.toList());

                List<LogisticsPurchasePackageBO.LogisticsPurchasePackageErrorProductBO> errorProductBOList = errorProductDOS.stream().map(errorProduct -> {
                    LogisticsPurchasePackageBO.LogisticsPurchasePackageErrorProductBO errorProductBO = new LogisticsPurchasePackageBO.LogisticsPurchasePackageErrorProductBO();
                    errorProductBO.setId(errorProduct.getId());
                    errorProductBO.setProductId(errorProduct.getProductId());
                    errorProductBO.setPackageId(errorProduct.getPurchasePackageId());
                    return errorProductBO;
                }).collect(Collectors.toList());
                // 组装数据
                errorProductBOList.stream().forEach(errorProductBO -> {
                    productBOList.stream().forEach(productBO -> {
                        if (productBO.getId().compareTo(errorProductBO.getProductId()) == 0) {
                            productBO.getLogisticsPurchasePackageErrorProductList().add(errorProductBO);
                        }
                    });
                });
                LogisticsPurchasePackageBO packageBO = new LogisticsPurchasePackageBO();
                packageBO.setId(item.getId());
                packageBO.getLogisticsProductList().addAll(productBOList);
                List<Long> list = purchase1688NoticeDepositoryConsumer.translate1688SkuToLocate(item.getChannelOrderId(), Collections.singletonList(packageBO));
                predictionList.addAll(list);
            } else {
                predictionList.add(item.getId());
            }
            if (!predictionList.isEmpty()) {
                // 直接預報
                PurchasePackagePredictionReq purchasePackagePredictionReq = new PurchasePackagePredictionReq();
                purchasePackagePredictionReq.setIdList(predictionList);
                try {
                    purchasePackageService.prediction(purchasePackagePredictionReq);
                } catch (Exception e) {
                    log.error("执行1688采购订单预报仓库失败，失败原因：{}",e);
                }
            }
        }
        stopWatch.stop();
        log.info("--------------------------------------执行1688采购订单推送失败任务结束，总共花费时间是：{} s------------------------------",stopWatch.getTotalTimeSeconds());
        return ReturnT.SUCCESS;
    }


}
