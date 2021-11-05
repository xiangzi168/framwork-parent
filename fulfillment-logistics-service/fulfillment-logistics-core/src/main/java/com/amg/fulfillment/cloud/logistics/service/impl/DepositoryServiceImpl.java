package com.amg.fulfillment.cloud.logistics.service.impl;

import cn.hutool.core.lang.Pair;
import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.api.dto.depository.GoodSku;
import com.amg.fulfillment.cloud.logistics.api.enumeration.DeliveryPackageDeliveryStatusEnum;
import com.amg.fulfillment.cloud.logistics.api.enumeration.DeliveryPackageTypeEnum;
import com.amg.fulfillment.cloud.logistics.api.enumeration.PurchasePackageLabelTypeEnum;
import com.amg.fulfillment.cloud.logistics.api.enumeration.PurchasePackagePredictionTypeEnum;
import com.amg.fulfillment.cloud.logistics.dto.depository.DepositorySearchDto;
import com.amg.fulfillment.cloud.logistics.dto.depository.InventoryDto;
import com.amg.fulfillment.cloud.logistics.dto.depository.OutDepositoryResultDto;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPackageDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPackageItemDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPurchasePackageDO;
import com.amg.fulfillment.cloud.logistics.enumeration.LogisticNodeEnum;
import com.amg.fulfillment.cloud.logistics.enumeration.LogisticTypeEnum;
import com.amg.fulfillment.cloud.logistics.factory.DepositoryFactory;
import com.amg.fulfillment.cloud.logistics.manager.IDepositoryManager;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsPackageItemMapper;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsPackageMapper;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsPurchasePackageMapper;
import com.amg.fulfillment.cloud.logistics.model.vo.DepositoryDataStatisticsVO;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsDataStatisticsForAEVO;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsDataStatisticsForAbroadVO;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsPackageOperationerStatisticsVO;
import com.amg.fulfillment.cloud.logistics.service.IDepositoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Tom
 * @date 2021-04-14-11:27
 */
@Service
@Slf4j
public class DepositoryServiceImpl implements IDepositoryService {

    @Autowired
    private DepositoryFactory depositoryFactory;

    @Autowired
    private LogisticsPurchasePackageMapper logisticsPurchasePackageMapper;
    @Autowired
    private LogisticsPackageMapper logisticsPackageMapper;
    @Autowired
    private LogisticsPackageItemMapper logisticsPackageItemMapper;

    @Override
    public List<InventoryDto> getDepositoryCount(DepositorySearchDto depositorySearchDto) {
        IDepositoryManager depository = depositoryFactory.createDepositoryManager(depositorySearchDto.getDepositoryCode());
        List<InventoryDto> list = depository.getDepositoryCount(depositorySearchDto);
        return list;
    }

    @Override
    public OutDepositoryResultDto addMaterialToDepository(GoodSku goodSku) {
        IDepositoryManager depository = depositoryFactory.createDepositoryManager(goodSku.getDepositoryCode());
        OutDepositoryResultDto outDepositoryResultDto = depository.addMaterialToDepository(goodSku);
        return outDepositoryResultDto;
    }

    @Override
    public GoodSku getMaterialFromDepository(GoodSku goodSku) {
        IDepositoryManager depository = depositoryFactory.createDepositoryManager(goodSku.getDepositoryCode());
        GoodSku result = depository.getMaterialFromDepository(goodSku);
        return result;
    }

    @Override
    public DepositoryDataStatisticsVO getDepositoryDataStatistics() {
        DepositoryDataStatisticsVO statisticsVO = new DepositoryDataStatisticsVO();
        LocalDate startDate = LocalDate.now().minusDays(90l);
        LocalDate endDate = LocalDate.now().plusDays(1L);
        ArrayList<Integer> statusList = new ArrayList<>();
        statusList.add(DeliveryPackageDeliveryStatusEnum.FAILPUSH.getCode());
        statusList.add(DeliveryPackageDeliveryStatusEnum.FAILSENDED.getCode());
        statusList.add(DeliveryPackageDeliveryStatusEnum.CANCELSEND.getCode());
        statusList.add(DeliveryPackageDeliveryStatusEnum.SENDING.getCode());
        statusList.add(DeliveryPackageDeliveryStatusEnum.PREPARATIONFAIL.getCode());
        statusList.add(DeliveryPackageDeliveryStatusEnum.PREPARATION.getCode());
        LambdaQueryWrapper<LogisticsPurchasePackageDO> queryForNotPrediction = Wrappers.<LogisticsPurchasePackageDO>lambdaQuery()
                .eq(LogisticsPurchasePackageDO::getValid,Boolean.TRUE)
                .eq(LogisticsPurchasePackageDO::getPrediction, PurchasePackagePredictionTypeEnum.NO.getType());
        Integer notPrediction = logisticsPurchasePackageMapper.selectCount(queryForNotPrediction);
        LambdaQueryWrapper<LogisticsPurchasePackageDO> queryForNotExceptionHandlded = Wrappers.<LogisticsPurchasePackageDO>lambdaQuery()
                .eq(LogisticsPurchasePackageDO::getValid,Boolean.TRUE)
                .eq(LogisticsPurchasePackageDO::getLabel, PurchasePackageLabelTypeEnum.PENDING_ERROR.getType());
        Integer notExceptionHandlded = logisticsPurchasePackageMapper.selectCount(queryForNotExceptionHandlded);
        LambdaQueryWrapper<LogisticsPackageDO> queryForDeliveryStatus = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .select(LogisticsPackageDO::getId, LogisticsPackageDO::getDeliveryStatus)
                .between(LogisticsPackageDO::getCreateTime, startDate, endDate)
                .eq(LogisticsPackageDO::getIsValid,Constant.YES)
                .eq(LogisticsPackageDO::getType, DeliveryPackageTypeEnum.ABROAD_DELIVERY_PACKAGE.getType())
                .in(LogisticsPackageDO::getDeliveryStatus, statusList);
        List<LogisticsPackageDO> logisticsPackageList = logisticsPackageMapper.selectList(queryForDeliveryStatus);
        Map<Integer, List<LogisticsPackageDO>> map = logisticsPackageList.stream().collect(Collectors.groupingBy(LogisticsPackageDO::getDeliveryStatus));
        for (Map.Entry<Integer, List<LogisticsPackageDO>> entry : map.entrySet()) {
            if (DeliveryPackageDeliveryStatusEnum.FAILPUSH.getCode().equals(entry.getKey())) {
                statisticsVO.setPushFail(entry.getValue().size());
            }
            if (DeliveryPackageDeliveryStatusEnum.FAILSENDED.getCode().equals(entry.getKey())) {
                statisticsVO.setSendFail(entry.getValue().size());
            }
            if (DeliveryPackageDeliveryStatusEnum.CANCELSEND.getCode().equals(entry.getKey())) {
                statisticsVO.setCancelSend(entry.getValue().size());
            }
            if (DeliveryPackageDeliveryStatusEnum.SENDING.getCode().equals(entry.getKey())) {
                statisticsVO.setSending(entry.getValue().size());
            }
            if (DeliveryPackageDeliveryStatusEnum.PREPARATIONFAIL.getCode().equals(entry.getKey())) {
                statisticsVO.setPreparationFail(entry.getValue().size());
            }
            if (DeliveryPackageDeliveryStatusEnum.PREPARATION.getCode().equals(entry.getKey())) {
                statisticsVO.setPreparation(entry.getValue().size());
            }
        }
        statisticsVO.setNotPrediction(notPrediction);
        statisticsVO.setNotExceptionHandlded(notExceptionHandlded);
        return statisticsVO;
    }


    @Override
    public LogisticsDataStatisticsForAbroadVO getLogisticsAboardStatistics() {
        LogisticsDataStatisticsForAbroadVO statisticsVO = new LogisticsDataStatisticsForAbroadVO();
        LocalDate startDate = LocalDate.now().minusDays(90l);
        LocalDate endDate = LocalDate.now().plusDays(1L);
        ArrayList<String> statusList = new ArrayList<>();
        statusList.add(LogisticNodeEnum.ABROAD_CARRIER_RECEIVED.getNodeEn());
        statusList.add(LogisticNodeEnum.ABROAD_DOMESTIC_RETURN.getNodeEn());
        statusList.add(LogisticNodeEnum.ABROAD_TRANSIT_DELAY.getNodeEn());
        statusList.add(LogisticNodeEnum.ABROAD_CUSTOMS_ABNORMAL.getNodeEn());
        statusList.add(LogisticNodeEnum.ABROAD_PICK_UP.getNodeEn());
        statusList.add(LogisticNodeEnum.ABROAD_UNDELIVERED.getNodeEn());
        statusList.add(LogisticNodeEnum.ABROAD_ALERT.getNodeEn());
        statusList.add(LogisticNodeEnum.ABROAD_RETURNED.getNodeEn());
        List<Pair<String, Long>> packageDOList = logisticsPackageMapper.selectAboardStatistics(startDate,endDate,statusList,null);
        Map<String, List<Pair<String, Long>>> map = packageDOList.stream().collect(Collectors.groupingBy(Pair::getKey));
        for (Map.Entry<String, List<Pair<String, Long>>> entry : map.entrySet()) {
            if (LogisticNodeEnum.ABROAD_CARRIER_RECEIVED.getNodeEn().equals(entry.getKey())) {
                statisticsVO.setCarrierReceived(entry.getValue().get(0).getValue().intValue());
            }
            if (LogisticNodeEnum.ABROAD_DOMESTIC_RETURN.getNodeEn().equals(entry.getKey())) {
                statisticsVO.setDomesticReturn(entry.getValue().get(0).getValue().intValue());
            }
            if (LogisticNodeEnum.ABROAD_TRANSIT_DELAY.getNodeEn().equals(entry.getKey())) {
                statisticsVO.setTransitDelay(entry.getValue().get(0).getValue().intValue());
            }
            if (LogisticNodeEnum.ABROAD_CUSTOMS_ABNORMAL.getNodeEn().equals(entry.getKey())) {
                statisticsVO.setCustomsAbnormal(entry.getValue().get(0).getValue().intValue());
            }
            if (LogisticNodeEnum.ABROAD_PICK_UP.getNodeEn().equals(entry.getKey())) {
                statisticsVO.setPickUp(entry.getValue().get(0).getValue().intValue());
            }
            if (LogisticNodeEnum.ABROAD_UNDELIVERED.getNodeEn().equals(entry.getKey())) {
                statisticsVO.setUndelivered(entry.getValue().get(0).getValue().intValue());
            }
            if (LogisticNodeEnum.ABROAD_ALERT.getNodeEn().equals(entry.getKey())) {
                statisticsVO.setAlert(entry.getValue().get(0).getValue().intValue());
            }
            if (LogisticNodeEnum.ABROAD_RETURNED.getNodeEn().equals(entry.getKey())) {
                statisticsVO.setReturned(entry.getValue().get(0).getValue().intValue());
            }
        }
        LambdaQueryWrapper<LogisticsPackageDO> queryForDeliveryNotRecieved = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .between(LogisticsPackageDO::getCreateTime, startDate, endDate)
                .eq(LogisticsPackageDO::getType, DeliveryPackageTypeEnum.ABROAD_DELIVERY_PACKAGE.getType())
                .eq(LogisticsPackageDO::getIsValid,Constant.YES)
                .eq(LogisticsPackageDO::getDeliveryStatus,DeliveryPackageDeliveryStatusEnum.SENDED.getCode())
                .eq(LogisticsPackageDO::getLogisticsReceived, Constant.NO_0);
        Integer count = logisticsPackageMapper.selectCount(queryForDeliveryNotRecieved);
        statisticsVO.setNotReceive(count);
        // 增加未通知的统计
        ArrayList<String> statusList_no = new ArrayList<>();
        statusList_no.add(LogisticNodeEnum.ABROAD_DOMESTIC_RETURN.getNodeEn());
        statusList_no.add(LogisticNodeEnum.ABROAD_PICK_UP.getNodeEn());
        List<Pair<String, Long>> packageDOList_no = logisticsPackageMapper.selectAboardStatistics(startDate,endDate,statusList_no,Constant.NO_0);
        Map<String, List<Pair<String, Long>>> map_no = packageDOList_no.stream().collect(Collectors.groupingBy(Pair::getKey));
        for (Map.Entry<String, List<Pair<String, Long>>> entry : map_no.entrySet()) {
            if (LogisticNodeEnum.ABROAD_PICK_UP.getNodeEn().equals(entry.getKey())) {
                statisticsVO.setPickUpNotNotice(entry.getValue().get(0).getValue().intValue());
            }
            if (LogisticNodeEnum.ABROAD_DOMESTIC_RETURN.getNodeEn().equals(entry.getKey())) {
                statisticsVO.setDomesticReturnNotNotice(entry.getValue().get(0).getValue().intValue());
            }
        }
        return statisticsVO;
    }

    @Override
    public LogisticsDataStatisticsForAEVO getLogisticsAEStatistics() {
        LogisticsDataStatisticsForAEVO statisticsVO = new LogisticsDataStatisticsForAEVO();
        LocalDate startDate = LocalDate.now().minusDays(90l);
        LocalDate endDate = LocalDate.now().plusDays(1L);
        ArrayList<String> statusList = new ArrayList<>();
        statusList.add(LogisticNodeEnum.AE_DELIVERED.getNodeEn());
        statusList.add(LogisticNodeEnum.AE_ARRIVE.getNodeEn());
        statusList.add(LogisticNodeEnum.AE_CANNCEL.getNodeEn());
        statusList.add(LogisticNodeEnum.AE_BACK.getNodeEn());
        LambdaQueryWrapper<LogisticsPackageDO> queryForDeliveryAe = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .select(LogisticsPackageDO::getId, LogisticsPackageDO::getLogisticsNode)
                .between(LogisticsPackageDO::getCreateTime, startDate, endDate)
                .eq(LogisticsPackageDO::getType, DeliveryPackageTypeEnum.AE_DELIVERY_PACKAGE.getType())
                .in(LogisticsPackageDO::getLogisticsNode, statusList);
        List<LogisticsPackageDO> packageDOList = logisticsPackageMapper.selectList(queryForDeliveryAe);
        Map<String, List<LogisticsPackageDO>> map = packageDOList.stream().collect(Collectors.groupingBy(LogisticsPackageDO::getLogisticsNode));
        for (Map.Entry<String, List<LogisticsPackageDO>> entry : map.entrySet()) {
            if (LogisticNodeEnum.AE_DELIVERED.getNodeEn().equals(entry.getKey())) {
                statisticsVO.setDelivered(entry.getValue().size());
            }
            if (LogisticNodeEnum.AE_ARRIVE.getNodeEn().equals(entry.getKey())) {
                statisticsVO.setArrive(entry.getValue().size());
            }
            if (LogisticNodeEnum.AE_CANNCEL.getNodeEn().equals(entry.getKey())) {
                statisticsVO.setCanncel(entry.getValue().size());
            }
            if (LogisticNodeEnum.AE_BACK.getNodeEn().equals(entry.getKey())) {
                statisticsVO.setBack(entry.getValue().size());
            }
        }
        return statisticsVO;
    }

    @Override
    public LogisticsPackageOperationerStatisticsVO getLogisticsPackageStatistics() {
        LocalDateTime startDate = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endDate = LocalDateTime.now().withHour(23).withMinute(23).withSecond(23).withNano(0);
        LogisticsPackageOperationerStatisticsVO operationerStatisticsVO = new LogisticsPackageOperationerStatisticsVO();
        // 查询包裹
        LambdaQueryWrapper<LogisticsPackageDO> queryForOperationer = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .select(LogisticsPackageDO::getId, LogisticsPackageDO::getCreateBy, LogisticsPackageDO::getCreateTime)
                .between(LogisticsPackageDO::getCreateTime, startDate, endDate)
                .eq(LogisticsPackageDO::getIsValid, Constant.YES);
        List<LogisticsPackageDO> logisticsPackageDOS = logisticsPackageMapper.selectList(queryForOperationer);
        if (logisticsPackageDOS.isEmpty()) {
            return operationerStatisticsVO;
        }
        // 查询商品
        LambdaQueryWrapper<LogisticsPackageItemDO> queryForPacakgeItem = Wrappers.<LogisticsPackageItemDO>lambdaQuery()
                .select(LogisticsPackageItemDO::getId, LogisticsPackageItemDO::getPackageId, LogisticsPackageItemDO::getPurchaseId, LogisticsPackageItemDO::getCreateBy)
                .in(LogisticsPackageItemDO::getPackageId, logisticsPackageDOS.stream().map(LogisticsPackageDO::getId).collect(Collectors.toList()));
        List<LogisticsPackageItemDO> logisticsPackageItemDOS = logisticsPackageItemMapper.selectList(queryForPacakgeItem);
        Map<String, List<LogisticsPackageDO>> packageMap = logisticsPackageDOS.stream().collect(Collectors.groupingBy(LogisticsPackageDO::getCreateBy));
        Map<String, List<LogisticsPackageItemDO>> packageItemMap = logisticsPackageItemDOS.stream().collect(Collectors.groupingBy(LogisticsPackageItemDO::getCreateBy));
        List<LogisticsPackageOperationerStatisticsVO.LogisticsPackageOperationer> packageOperationers = packageMap.entrySet().stream().map(packageOperationer -> {
            LogisticsPackageOperationerStatisticsVO.LogisticsPackageOperationer operationer = new LogisticsPackageOperationerStatisticsVO.LogisticsPackageOperationer();
            operationer.setOperationer(packageOperationer.getKey());
            operationer.setPackageCount(packageOperationer.getValue().size());
            // 查询商品数量
            Optional<Map.Entry<String, List<LogisticsPackageItemDO>>> optional = packageItemMap.entrySet().stream()
                    .filter(item -> item.getKey().equals(packageOperationer.getKey())).findFirst();
            operationer.setProductCount(optional.isPresent() ? optional.get().getValue().size() : 0);
            return operationer;
        }).collect(Collectors.toList());
        operationerStatisticsVO.getLogisticsPackageOperationers().addAll(packageOperationers);
        return operationerStatisticsVO;
    }
}