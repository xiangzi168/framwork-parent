package com.amg.fulfillment.cloud.logistics.service;


import com.amg.fulfillment.cloud.logistics.api.proto.ChannelOrderLogisticsGTO;
import com.amg.fulfillment.cloud.logistics.api.proto.SalesOrderLogisticsGTO;
import com.amg.fulfillment.cloud.logistics.dto.logistic.*;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPackageDO;
import com.amg.fulfillment.cloud.logistics.model.req.*;
import com.amg.fulfillment.cloud.logistics.model.vo.DeliveryPackageCannelVO;
import com.amg.fulfillment.cloud.logistics.model.vo.DeliveryPackageVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.io.File;
import java.util.List;

/**
 * Created by Seraph on 2021/5/13
 */
public interface IDeliveryPackageService {
    void syncUnfinishedPackageList();

    Page<DeliveryPackageVO> list(DeliveryPackageReq deliveryPackageReq);

    DeliveryPackageVO detail(Integer type, Long packageId);

    List<DeliveryPackageVO> salesOrderPackage(String salesOrderId);

    Boolean pushWarehouse(DeliveryPackageOperationReq deliveryPackageOperationReq);

    Boolean pushWarehouseBatch(DeliveryPackageOperationReq deliveryPackageOperationReq);

    DeliveryPackageCannelVO cancel(DeliveryPackageOperationReq deliveryPackageOperationReq);

    List<DeliveryPackageCannelVO> cancelBatch(DeliveryPackageOperationReq deliveryPackageOperationReq);

    Boolean deliveredCreatePackage(DeliveryPackageDeliveredCreateReq deliveryPackageDeliveredCreateReq);

    Boolean refresh(DeliveryPackageOperationReq deliveryPackageOperationReq);

    Boolean refreshDeliveryPackage(List<LogisticsPackageDO> list);

    Boolean disable(DeliveryPackageOperationReq deliveryPackageOperationReq);

    File exportDeliveryPackageExcel(ExportExcelReq<DeliveryPackageReq> exportExcelReq);

    Boolean updateAeDeliveryPackage(DeliveryPackageDto deliveryPackageDto);

    List<SalesOrderLogisticsGTO.SalesOrderLogisticsResponse> getSalesOrderLogistics(SalesOrderLogisticsDto salesOrderLogisticsDto);

    List<SalesOrderLogisticsGTO.SalesOrderLogisticsListResponse> getSalesOrderLogisticsList(SalesOrderLogisticsDto salesOrderLogisticsDto);

    List<ChannelOrderLogisticsGTO.ChannelOrderLogisticsResponse> getChannelLogistics(ChannelOrderLogisticsDto channelOrderLogisticsDto);

    List<String> getChannerOrderIdByPackageId(List<String> packageIdList);

    List<String> getSalesOrderIdByPackageId(List<String> packageIdList);

    Boolean save(LogisticsPackageAddReq logisticsPackageAddReq);

    Boolean updatePackage(LogisticsPackageAddReq logisticsPackageAddReq);

    Boolean insertDirectSendPackage(DeliveryPackageDto deliveryPackageDto);

    Boolean noticeUser(List<Long> idList);

    Boolean updateLogisticsTrackingCode(List<LogisticsTrackingCodeUpdateReq> logisticsTrackingCodeUpdateReqList);

    File importLogisticsTrackingCodeExcel(String fileUrl, Integer type);
}
