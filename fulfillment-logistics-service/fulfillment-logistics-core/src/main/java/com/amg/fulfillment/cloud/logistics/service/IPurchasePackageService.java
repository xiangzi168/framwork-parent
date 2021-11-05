package com.amg.fulfillment.cloud.logistics.service;

import com.amg.fulfillment.cloud.logistics.entity.LogisticsPurchasePackageDO;
import com.amg.fulfillment.cloud.logistics.model.req.*;
import com.amg.fulfillment.cloud.logistics.model.vo.PurchasePackageLogisticsInfoVO;
import com.amg.fulfillment.cloud.logistics.model.vo.PurchasePackageVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.File;
import java.util.List;

/**
 * Created by Seraph on 2021/5/11
 */
public interface IPurchasePackageService extends IService<LogisticsPurchasePackageDO> {


    /**
     * @param oldChannelOrderId 老的渠道订单号
     * @param newChannelOrderId 新的渠道订单号
     * @return 是否正常执行无异常返回TRUE
     */
    Boolean updateChannelOrderId(String oldChannelOrderId,String newChannelOrderId);

    /**
     * @param packageStatusMarkReq 请求数据
     * @return 返回更新结果
     */
    Boolean updatePackageStatus(PackageStatusMarkReq packageStatusMarkReq);

    Page<PurchasePackageVO> list(PurchasePackageReq purchasePackageReq);

    PurchasePackageVO detail(Long purchasePackageId, String productType);

    Boolean errorHandle(PurchasePackageErrorHandleReq purchasePackageErrorHandleReq);

    Boolean prediction(PurchasePackagePredictionReq purchasePackagePredictionReq);

    File exportPurchasePackageExcel(ExportExcelReq<PurchasePackageReq> exportExcelReq);

    List<PurchasePackageLogisticsInfoVO> getPurchasePackageLogisticsInfo(List<String> purchaseIdList);

    Boolean againPurchase(PurchasePackageErrorHandleReq purchasePackageErrorHandleReq);

    Boolean pullPruchaseOrderAndPrediction(PruchaseOrderAndPredictionReq pruchaseOrderAndPredictionReq);

    Boolean manualPredictionPurchaseId(ManualPredictionReq manualPredictionReq);

    boolean updatePurchasePackageByPurchaseId(String purchaseId);
}
