package com.amg.fulfillment.cloud.logistics.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.amg.framework.boot.base.enums.ResponseCodeEnum;
import com.amg.framework.boot.base.exception.GlobalException;
import com.amg.framework.boot.utils.id.SnowflakeIdUtils;
import com.amg.framework.cloud.grpc.context.UserContext;
import com.amg.framework.cloud.grpc.utils.GrpcJsonFormatUtils;
import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb.DepositoryResponseMsgForWanB;
import com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb.PurchaseOrderExpressEditRequest;
import com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb.PurchaseOrderForWanB;
import com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb.PurchaseOrderProductContentEditRequest;
import com.amg.fulfillment.cloud.logistics.api.dto.msg.DepositoryPurchaseStatusMsgDto;
import com.amg.fulfillment.cloud.logistics.api.enumeration.*;
import com.amg.fulfillment.cloud.logistics.api.util.BeanConvertUtils;
import com.amg.fulfillment.cloud.logistics.api.util.LogisticsCommonUtils;
import com.amg.fulfillment.cloud.logistics.api.util.WanBDepistoryUtil;
import com.amg.fulfillment.cloud.logistics.entity.*;
import com.amg.fulfillment.cloud.logistics.enumeration.BaseLogisticsResponseCodeEnum;
import com.amg.fulfillment.cloud.logistics.grpc.ChannelOrderLogisticsService;
import com.amg.fulfillment.cloud.logistics.mapper.DepositorySaleOrderDetailMapper;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsPurchasePackageErrorProductMapper;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsPurchasePackageMapper;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsPurchaseProductMapper;
import com.amg.fulfillment.cloud.logistics.model.bo.LogisticsPurchasePackageBO;
import com.amg.fulfillment.cloud.logistics.model.excel.PurchasePackageExcel;
import com.amg.fulfillment.cloud.logistics.model.req.*;
import com.amg.fulfillment.cloud.logistics.model.vo.PurchasePackageLogisticsInfoVO;
import com.amg.fulfillment.cloud.logistics.model.vo.PurchasePackageProductVO;
import com.amg.fulfillment.cloud.logistics.model.vo.PurchasePackageVO;
import com.amg.fulfillment.cloud.logistics.mq.consumer.Purchase1688NoticeDepositoryConsumer;
import com.amg.fulfillment.cloud.logistics.service.ILogisticsStatusService;
import com.amg.fulfillment.cloud.logistics.service.IPurchasePackageService;
import com.amg.fulfillment.cloud.logistics.util.FilePlusUtils;
import com.amg.fulfillment.cloud.logistics.util.SendMsgUtils;
import com.amg.fulfillment.cloud.order.api.client.ChannelPurchaseProductClient;
import com.amg.fulfillment.cloud.order.api.proto.ChannelPurchaseProductGTO;
import com.amg.fulfillment.cloud.purchase.api.client.PurchaseIdInfoSynchronizeServiceGrpcClient;
import com.amg.fulfillment.cloud.purchase.api.enums.ChannelEnum;
import com.amg.fulfillment.cloud.purchase.api.proto.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Seraph on 2021/5/11
 */
@Slf4j
@Service
public class PurchasePackageServiceImpl extends ServiceImpl<LogisticsPurchasePackageMapper, LogisticsPurchasePackageDO> implements IPurchasePackageService {
    @Autowired
    private LogisticsPurchaseProductMapper logisticsPurchaseProductMapper;
    @Autowired
    private LogisticsPurchasePackageMapper logisticsPurchasePackageMapper;
    @Autowired
    private LogisticsPurchasePackageErrorProductMapper logisticsPurchasePackageErrorProductMapper;
    @Autowired
    private WanBDepistoryUtil wanBDepistoryUtil;
    @Autowired
    private SendMsgUtils sendMsgUtils;
    @Autowired
    private Purchase1688NoticeDepositoryConsumer purchase1688NoticeDepositoryConsumer;
    @Autowired
    private PurchaseIdInfoSynchronizeServiceGrpcClient purchaseIdInfoSynchronizeServiceGrpcClient;
    @Autowired
    private ChannelPurchaseProductClient channelPurchaseProductClient;
    @Autowired
    private LogisticsCommonUtils logisticsCommonUtils;
    @Autowired
    private DepositorySaleOrderDetailMapper depositorySaleOrderDetailMapper;
    @Autowired
    private ILogisticsStatusService logisticsStatusService;

    @Override
    public Boolean updateChannelOrderId(String oldChannelOrderId, String newChannelOrderId) {
        try {
            LambdaUpdateWrapper<LogisticsPurchasePackageDO> objectLambdaUpdateWrapper = Wrappers.lambdaUpdate();
            objectLambdaUpdateWrapper.set(LogisticsPurchasePackageDO::getChannelOrderId, newChannelOrderId)
                    .eq(LogisticsPurchasePackageDO::getChannelOrderId, oldChannelOrderId);

            List<String> strings = new ArrayList<>();
            strings.add(oldChannelOrderId);
            List<LogisticsPurchasePackageDO> logisticsPurchasePackageDOS = logisticsPurchasePackageMapper.selectListByFindInSet(strings);
            if (logisticsPurchasePackageDOS != null && logisticsPurchasePackageDOS.size() > 0) {
                for (LogisticsPurchasePackageDO lpp : logisticsPurchasePackageDOS) {
                    String channelOrderId = lpp.getChannelOrderId();
                    if (channelOrderId != null) {
                        lpp.setChannelOrderId(channelOrderId.replace(oldChannelOrderId, newChannelOrderId));
                    }
                }
            }
            saveOrUpdateBatch(logisticsPurchasePackageDOS);
        } catch (Exception e) {
            log.error("???????????????????????????old:{} new:{} ???????????????{}", oldChannelOrderId, newChannelOrderId, e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public Boolean updatePackageStatus(PackageStatusMarkReq packageStatusMarkReq) {
        LambdaUpdateWrapper<LogisticsPurchasePackageDO> objectLambdaUpdateWrapper = Wrappers.lambdaUpdate();
        objectLambdaUpdateWrapper.in(LogisticsPurchasePackageDO::getId, packageStatusMarkReq.getId());
        objectLambdaUpdateWrapper.set(LogisticsPurchasePackageDO::getValid, packageStatusMarkReq.isStatus());
        return update(objectLambdaUpdateWrapper);
    }


    @Override
    public Page<PurchasePackageVO> list(PurchasePackageReq purchasePackageReq) {
        long page = purchasePackageReq.getPage();
        Page<PurchasePackageVO> purchasePackageVOPage = new Page<>(page, purchasePackageReq.getRow());
        purchasePackageReq.setPage((purchasePackageReq.getPage() - 1) * purchasePackageReq.getRow());
        List<LogisticsPackageMergeStatusDO> logisticsPurchasePackageDOS = logisticsPurchasePackageMapper.selectLogisticsStatusList(purchasePackageReq);
        mergeLogisticsStatusDO(logisticsPurchasePackageDOS);
        List<PurchasePackageVO> purchasePackageVOList = BeanConvertUtils.copyProperties(logisticsPurchasePackageDOS, PurchasePackageVO.class);
        purchasePackageVOPage.setRecords(purchasePackageVOList);
        purchasePackageVOPage.setTotal(logisticsPurchasePackageMapper.selectLogisticsStatusCount(purchasePackageReq));
        return purchasePackageVOPage;
    }

    private void mergeLogisticsStatusDO(List<LogisticsPackageMergeStatusDO> logisticsPackageMergeStatusDOS) {
        if (logisticsPackageMergeStatusDOS != null && logisticsPackageMergeStatusDOS != null && logisticsPackageMergeStatusDOS.size() > 0 && logisticsPackageMergeStatusDOS.size() > 0) {
            for (LogisticsPackageMergeStatusDO logisticsPackageMergeStatusDO : logisticsPackageMergeStatusDOS) {
                if (logisticsPackageMergeStatusDO != null) {
                    logisticsPackageMergeStatusDO.setLogisticsStatus(logisticsStatusService.getLatestLogisticsStatus(logisticsPackageMergeStatusDO));
                }
            }
        }
    }
    @Override
    public PurchasePackageVO detail(Long purchasePackageId, String productType) {
        LogisticsPurchasePackageDO logisticsPurchasePackageDO = logisticsPurchasePackageMapper.selectById(purchasePackageId);
        PurchasePackageVO purchasePackageVO = new PurchasePackageVO();
        PurchaseIdInfoResult idInfoResult = null;
        List<LogisticsPurchasePackageErrorProductDO> logisticsPurchasePackageErrorProductDOList=null;
        List<PurchasePackageProductVO> purchasePackageProductVOList = new ArrayList<>();
        if (logisticsPurchasePackageDO != null) {
            purchasePackageVO = BeanConvertUtils.copyProperties(logisticsPurchasePackageDO, PurchasePackageVO.class);
            LambdaQueryWrapper<LogisticsPurchaseProductDO> logisticsProductDOLambdaQueryWrapper = Wrappers.<LogisticsPurchaseProductDO>lambdaQuery()
                    .eq(LogisticsPurchaseProductDO::getPackageId, purchasePackageId);
            List<LogisticsPurchaseProductDO> logisticsPurchaseProductDOList = logisticsPurchaseProductMapper.selectList(logisticsProductDOLambdaQueryWrapper);
            if (logisticsPurchaseProductDOList == null || logisticsPurchaseProductDOList.isEmpty()) {
                return purchasePackageVO;
            }
            LambdaQueryWrapper<LogisticsPurchasePackageErrorProductDO> logisticsPurchasePackageErrorProductDOLambdaQueryWrapper = Wrappers.<LogisticsPurchasePackageErrorProductDO>lambdaQuery()
                    .eq(LogisticsPurchasePackageErrorProductDO::getPurchasePackageId, purchasePackageId)
                    .isNotNull(LogisticsPurchasePackageErrorProductDO::getPurchaseId);
            logisticsPurchasePackageErrorProductDOList = logisticsPurchasePackageErrorProductMapper.selectList(logisticsPurchasePackageErrorProductDOLambdaQueryWrapper);
            if (logisticsPurchasePackageErrorProductDOList == null || logisticsPurchasePackageErrorProductDOList.isEmpty()) {
                return purchasePackageVO;
            }
            if (productType.equals("error")) {
                Map<Long, LogisticsPurchaseProductDO> logisticsProductDOMap = logisticsPurchaseProductDOList.stream().collect(Collectors.toMap(LogisticsPurchaseProductDO::getId, logisticsProductDO -> logisticsProductDO));
                for (LogisticsPurchasePackageErrorProductDO logisticsPurchasePackageErrorProductDO : logisticsPurchasePackageErrorProductDOList) {
                    LogisticsPurchaseProductDO logisticsPurchaseProductDO = Optional.ofNullable(logisticsProductDOMap.get(logisticsPurchasePackageErrorProductDO.getProductId())).orElse(new LogisticsPurchaseProductDO());
                    //????????????????????????????????????????????????
                    if (StringUtils.isBlank(logisticsPurchasePackageErrorProductDO.getErrorType()) || "none".equalsIgnoreCase(logisticsPurchasePackageErrorProductDO.getErrorType())) {
                        continue;
                    }
                    //????????????????????????????????????????????????
                    PurchasePackageProductVO purchasePackageProductVO = BeanConvertUtils.copyProperties(logisticsPurchaseProductDO, PurchasePackageProductVO.class);
                    purchasePackageProductVO.setPurchaseId(logisticsPurchasePackageErrorProductDO.getPurchaseId());
                    purchasePackageProductVO.setErrorPurchaseId(logisticsPurchasePackageErrorProductDO.getErrorPurchaseId());
                    purchasePackageProductVO.setErrorType(logisticsPurchasePackageErrorProductDO.getErrorType());
                    purchasePackageProductVO.setErrorHandle(PurchasePackageProductErrorHandleTypeEnum.getPurchasePackageProductErrorHandleTypeEnumByType(logisticsPurchasePackageErrorProductDO.getErrorHandle()).getMsg());
                    purchasePackageProductVO.setErrorMessage(logisticsPurchasePackageErrorProductDO.getErrorMessage());
                    purchasePackageProductVO.setErrorImg(logisticsPurchasePackageErrorProductDO.getErrorImg());
                    purchasePackageProductVO.setProductCount(1);
                    purchasePackageProductVO.setErrorId(logisticsPurchasePackageErrorProductDO.getId());
                    purchasePackageProductVOList.add(purchasePackageProductVO);
                    // gprc???????????????????????????
                    try {
                        PurchaseIdInfoRequest infoRequest = PurchaseIdInfoRequest.newBuilder().setPurchaseId(logisticsPurchasePackageErrorProductDO.getPurchaseId()).build();
                        GetPurchaseIdInfoRequest request = GetPurchaseIdInfoRequest.newBuilder().addAllPurchaseIdInfoRequest(Collections.singletonList(infoRequest)).build();
                        idInfoResult = purchaseIdInfoSynchronizeServiceGrpcClient.getPurchaseIdInfo(request);
                        log.info("1688?????????????????????---??????GRPC????????????????????????????????????????????????{}???????????????{}", GrpcJsonFormatUtils.toJsonString(request), GrpcJsonFormatUtils.toJsonString(idInfoResult));
                        if (idInfoResult.getSuccess() && !idInfoResult.getDataList().isEmpty()) {
                            String productStatus = idInfoResult.getData(0).getCommodityStatus();
                            String remarks = idInfoResult.getData(0).getRemarks();
                            purchasePackageProductVO.setProductStatus(productStatus);
                            purchasePackageProductVO.setRemarks(remarks);
                        }
                    } catch (Exception e) {
                        idInfoResult = PurchaseIdInfoResult.newBuilder().build();
                        log.error("1688?????????????????????---??????GRPC??????????????????????????????????????????????????????{}???????????????{}", logisticsPurchasePackageErrorProductDO.getPurchaseId(), e);
                    }
                }
            } else {
                Map<String, List<ChannelPurchaseProductGTO.ChannelPurchaseProductResponse>> channelOrderMapBySku = new HashMap<>();
                try {
                    // grpc????????????
                    ChannelPurchaseProductGTO.ChannelPurchaseProductRequest channelReq = ChannelPurchaseProductGTO.ChannelPurchaseProductRequest.newBuilder()
                            .setOrderChannel(ChannelEnum.CHANNEL_1688.getCode())
                            .setChannelOrderId(logisticsPurchasePackageDO.getChannelOrderId())
                            .build();
                    ChannelPurchaseProductGTO.ChannelPurchaseProductResult channelPurchaseProductResult = channelPurchaseProductClient.getChannelPurchaseProduct(channelReq);
                    log.info("1688?????????????????????---??????GRPC?????????????????????????????????????????????{}?????????????????????{}", GrpcJsonFormatUtils.toJsonString(channelReq), GrpcJsonFormatUtils.toJsonString(channelPurchaseProductResult));
                    if (channelPurchaseProductResult.getSuccess() && !channelPurchaseProductResult.getDataList().isEmpty()) {
                        Map<String, List<ChannelPurchaseProductGTO.ChannelPurchaseProductResponse>> map = channelPurchaseProductResult.getDataList().stream().collect(Collectors.groupingBy(ChannelPurchaseProductGTO.ChannelPurchaseProductResponse::getSku));
                        channelOrderMapBySku.putAll(map);
                    }
                } catch (Exception e) {
                    log.error("1688?????????????????????---??????GRPC????????????????????????????????????????????????????????????????????????{}???????????????{}", logisticsPurchasePackageDO.getChannelOrderId(), e);
                }

                Map<String, List<LogisticsPurchasePackageErrorProductDO>> errorMapBySku = logisticsPurchasePackageErrorProductDOList.stream()
                        .filter(item ->Constant.NO_0.equals(item.getHidden())).collect(Collectors.groupingBy(LogisticsPurchasePackageErrorProductDO::getSku));
                for (LogisticsPurchaseProductDO logisticsPurchaseProductDO : logisticsPurchaseProductDOList) {
                    PurchasePackageProductVO purchasePackageProductVO = BeanConvertUtils.copyProperties(logisticsPurchaseProductDO, PurchasePackageProductVO.class);
                    List<String> purchaseIds = Optional.ofNullable(errorMapBySku.get(logisticsPurchaseProductDO.getSku())).orElse(Collections.emptyList())
                            .stream().map(LogisticsPurchasePackageErrorProductDO::getPurchaseId)
                            .collect(Collectors.toList());
                    purchasePackageProductVO.setPurchaseId(purchaseIds.stream().collect(Collectors.joining(",")));
                    purchasePackageProductVO.setProductCount(purchaseIds.size());
                    // gprc???????????????????????????
                    try {
                        if (purchaseIds.isEmpty()) {
                            continue;
                        }
                        PurchaseIdInfoRequest infoRequest = PurchaseIdInfoRequest.newBuilder().setPurchaseId(purchaseIds.get(0)).build();
                        GetPurchaseIdInfoRequest request = GetPurchaseIdInfoRequest.newBuilder().addAllPurchaseIdInfoRequest(Collections.singletonList(infoRequest)).build();
                        idInfoResult = purchaseIdInfoSynchronizeServiceGrpcClient.getPurchaseIdInfo(request);
                        log.info("1688?????????????????????---??????GRPC????????????????????????????????????????????????{}???????????????{}", GrpcJsonFormatUtils.toJsonString(request), GrpcJsonFormatUtils.toJsonString(idInfoResult));
                        if (idInfoResult.getSuccess() && !idInfoResult.getDataList().isEmpty()) {
                            String productStatus = idInfoResult.getData(0).getCommodityStatus();
                            String remarks = idInfoResult.getData(0).getRemarks();
                            purchasePackageProductVO.setProductStatus(productStatus);
                            purchasePackageProductVO.setRemarks(remarks);
                        }
                    } catch (Exception e) {
                        idInfoResult = PurchaseIdInfoResult.newBuilder().build();
                        log.error("1688?????????????????????---??????GRPC??????????????????????????????????????????????????????{}???????????????{}", purchaseIds.get(0), e);
                    }
                    //????????????
                    String refundStatus = Optional.ofNullable(channelOrderMapBySku.get(logisticsPurchaseProductDO.getSku())).orElse(Collections.singletonList(ChannelPurchaseProductGTO.ChannelPurchaseProductResponse.newBuilder().build())).get(0).getRefundStatus();
                    purchasePackageProductVO.setRefundStatus(refundStatus);
                    purchasePackageProductVOList.add(purchasePackageProductVO);
                }
            }
        }
        // ??????????????????????????????t_logistics_purchase_product?????????????????????????????????????????????
        List<PurchasePackageProductVO> distinctPurchasePackageProductVOList = purchasePackageProductVOList.stream().collect(Collectors.groupingBy(PurchasePackageProductVO::getPurchaseId))
                .entrySet().stream().map(entry -> entry.getValue().get(0)).collect(Collectors.toList());
        purchasePackageVO.setProductList(distinctPurchasePackageProductVOList);
//        // ???????????? ?????????
//        try {
//            List<String> purchaseIdsForRemarks = logisticsPurchasePackageErrorProductDOList.stream().map(LogisticsPurchasePackageErrorProductDO::getPurchaseId).collect(Collectors.toList());
//            List<PurchaseIdInfoRequest> requestList = purchaseIdsForRemarks.stream().map(item -> PurchaseIdInfoRequest.newBuilder().setPurchaseId(item).build()).collect(Collectors.toList());
//            if (!requestList.isEmpty()) {
//                GetPurchaseIdInfoRequest requestForRemarks = GetPurchaseIdInfoRequest.newBuilder().addAllPurchaseIdInfoRequest(requestList).build();
//                log.info("1688?????????????????????---??????GRPC????????????remarks?????????????????????{}", purchaseIdsForRemarks);
//                PurchaseIdInfoResult infoResult = purchaseIdInfoSynchronizeServiceGrpcClient.getPurchaseIdInfo(requestForRemarks);
//                if (infoResult.getSuccess() && !Objects.isNull(infoResult.getDataList()) && !infoResult.getDataList().isEmpty()) {
//                    List<PurchaseIdInfo> dataList = infoResult.getDataList();
//                    Map<String, List<PurchaseIdInfo>> mapByPurchaseIdForRemarks = dataList.stream().collect(Collectors.groupingBy(PurchaseIdInfo::getPurchaseId));
//                    distinctPurchasePackageProductVOList.stream().forEach(item ->{
//                        List<PurchaseIdInfo> purchaseIdInfos = Optional.ofNullable(mapByPurchaseIdForRemarks.get(item.getPurchaseId())).orElseGet(() -> Collections.singletonList(PurchaseIdInfo.newBuilder().build()));
//                        item.setRemarks(purchaseIdInfos.get(0).getRemarks());
//                    });
//                }
//            }
//        } catch (Exception e) {
//            log.error("1688?????????????????????---??????GRPC????????????remarks???????????????{}", e);
//        }

        return purchasePackageVO;
    }

    @Override
    public Boolean errorHandle(PurchasePackageErrorHandleReq purchasePackageErrorHandleReq) {

        //??????????????????????????????
        LogisticsPurchasePackageErrorProductDO logisticsPurchasePackageErrorProductDO = checkPackageErrorExsit(purchasePackageErrorHandleReq);

        //??????????????????
        LogisticsPurchasePackageErrorProductDO tempLogisticsPurchasePackageProductDO = new LogisticsPurchasePackageErrorProductDO();
        tempLogisticsPurchasePackageProductDO.setId(logisticsPurchasePackageErrorProductDO.getId());
        tempLogisticsPurchasePackageProductDO.setErrorHandle(purchasePackageErrorHandleReq.getErrorHandle());
        if (PurchasePackageProductErrorHandleTypeEnum.DIRECT_WAREHOUSING.getType().equals(purchasePackageErrorHandleReq.getErrorHandle())) {
            if (logisticsPurchasePackageErrorProductDO.getWeight() == null || logisticsPurchasePackageErrorProductDO.getWeight().compareTo(new BigDecimal("0")) <= 0) {
                tempLogisticsPurchasePackageProductDO.setWeight(new BigDecimal("1"));
            } else {
                tempLogisticsPurchasePackageProductDO.setWeight(logisticsPurchasePackageErrorProductDO.getWeight());
            }
            tempLogisticsPurchasePackageProductDO.setStatus(DepositoryPurchaseStatusEnum.WAREHOUSING.getStatusCode());
        } else {
            tempLogisticsPurchasePackageProductDO.setStatus(DepositoryPurchaseStatusEnum.HANDLEDEXCEPTION.getStatusCode());
        }
        int rows = logisticsPurchasePackageErrorProductMapper.updateById(tempLogisticsPurchasePackageProductDO);
        if (rows != 0) {
            // ????????????????????????????????????????????????mq
            tempLogisticsPurchasePackageProductDO.setItemId(logisticsPurchasePackageErrorProductDO.getItemId());
            tempLogisticsPurchasePackageProductDO.setPurchaseId(logisticsPurchasePackageErrorProductDO.getPurchaseId());
            tempLogisticsPurchasePackageProductDO.setSalesOrderId(logisticsPurchasePackageErrorProductDO.getSalesOrderId());
            casecadeUpdateDepositoryStatus(tempLogisticsPurchasePackageProductDO);
            sendHandleExceptionMsgToMq(tempLogisticsPurchasePackageProductDO);
            return updatePurchasePackageLabel(logisticsPurchasePackageErrorProductDO);
        }
        return Boolean.TRUE;
    }


    @Override
    public Boolean prediction(PurchasePackagePredictionReq purchasePackagePredictionReq) {
        LambdaQueryWrapper<LogisticsPurchasePackageDO> wrapper = Wrappers.<LogisticsPurchasePackageDO>lambdaQuery()
                .in(LogisticsPurchasePackageDO::getId, purchasePackagePredictionReq.getIdList());
        List<LogisticsPurchasePackageDO> logisticsPurchasePackageDOList = logisticsPurchasePackageMapper.selectList(wrapper);
        if (logisticsPurchasePackageDOList.size() == 0) {
//            log.error("??????????????????????????????????????? %s ??????", purchasePackagePredictionReq.getIdList());
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "????????????????????????????????????");
        }

        logisticsPurchasePackageDOList.forEach(logisticsPurchasePackageDO -> {
            Integer prediction = logisticsPurchasePackageDO.getPrediction();
            if (prediction.equals(PurchasePackagePredictionTypeEnum.YES.getType())) {
//                log.error("????????????????????????????????? %s ??????", JSON.toJSON(logisticsPurchasePackageDO).toString());
                throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "?????????????????????????????????????????????????????????????????????????????????");
            }
        });
        /////////////////////////////////////////////////////////////////??????1688???????????????///////////////////////////////////////////
//        query1688IfPurchasePackageIsPrediction(logisticsPurchasePackageDOList);
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //????????????????????????????????????
        LambdaQueryWrapper<LogisticsPurchasePackageErrorProductDO> logisticsPurchasePackageErrorProductDOLambdaQueryWrapper = Wrappers.<LogisticsPurchasePackageErrorProductDO>lambdaQuery()
                .in(LogisticsPurchasePackageErrorProductDO::getPurchasePackageId, purchasePackagePredictionReq.getIdList())
                .isNotNull(LogisticsPurchasePackageErrorProductDO::getPurchaseId);
        List<LogisticsPurchasePackageErrorProductDO> logisticsPurchasePackageErrorProductDOList = logisticsPurchasePackageErrorProductMapper.selectList(logisticsPurchasePackageErrorProductDOLambdaQueryWrapper);
        if (logisticsPurchasePackageErrorProductDOList.isEmpty()) {
            // ????????????
            logisticsPurchasePackageDOList.stream().forEach(item ->{
                wanBDepistoryUtil.deletePurchaseorders(item.getPackageNo());
            });
//            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "??????????????????????????????????????????????????????null");
            return true;
        }
        Map<Long, List<LogisticsPurchasePackageErrorProductDO>> logisticsPurchasePackageErrorProductDOMap = logisticsPurchasePackageErrorProductDOList.stream().collect(Collectors.groupingBy(LogisticsPurchasePackageErrorProductDO::getPurchasePackageId));
        // ??????????????????(????????????purchaseId ???????????????????????????)
        logisticsPurchasePackageDOList.stream().forEach(item ->{
            List<LogisticsPurchasePackageErrorProductDO> logisticsPurchasePackageErrorProductDOS = logisticsPurchasePackageErrorProductDOMap.get(item.getId());
            if (Objects.isNull(logisticsPurchasePackageErrorProductDOS)) {
                wanBDepistoryUtil.deletePurchaseorders(item.getPackageNo());
            }
        });
        //????????????????????????
        List<String> errorMsgList = new ArrayList<>();
        List<Long> errorIdList = new ArrayList<>();
        for (LogisticsPurchasePackageDO logisticsPurchasePackageDO : logisticsPurchasePackageDOList) {
            List<PurchaseOrderProductContentEditRequest.PurchaseOrderItemEditRequest> purchaseOrderItemEditRequestList = new ArrayList<>();     //???????????????????????????????????????????????????????????????????????????????????????
            List<PurchaseOrderProductContentEditRequest.PurchaseOrderProductEditRequest> purchaseOrderProductEditRequestList = new ArrayList<>();       //?????????????????????????????????

            //??????????????????
            List<LogisticsPurchasePackageErrorProductDO> tempLogisticsPurchasePackageErrorProductDOList = Optional.ofNullable(logisticsPurchasePackageErrorProductDOMap.get(logisticsPurchasePackageDO.getId())).orElse(new ArrayList<>());
            tempLogisticsPurchasePackageErrorProductDOList.stream().forEach(errorProductDO -> {
                PurchaseOrderProductContentEditRequest.PurchaseOrderItemEditRequest purchaseOrderItemEditRequest = PurchaseOrderProductContentEditRequest.PurchaseOrderItemEditRequest.builder()
                        .Id(errorProductDO.getPurchaseId())
                        .SKU(errorProductDO.getSku())
                        .SalesOrderId(errorProductDO.getSalesOrderId())
                        .build();
                purchaseOrderItemEditRequestList.add(purchaseOrderItemEditRequest);
            });
            // ????????????????????????????????????
            if (purchaseOrderItemEditRequestList.isEmpty()) {
                log.info("????????????????????????????????????id???: {},???????????????{} ", logisticsPurchasePackageDO.getId(), logisticsPurchasePackageDO.getPackageNo());
                continue;
            }
            //??????????????????
            Map<String, List<PurchaseOrderProductContentEditRequest.PurchaseOrderItemEditRequest>> purchaseOrderItemEditRequestMap = purchaseOrderItemEditRequestList.stream().collect(Collectors.groupingBy(PurchaseOrderProductContentEditRequest.PurchaseOrderItemEditRequest::getSKU));
            purchaseOrderItemEditRequestMap.forEach((key, values) -> {
                PurchaseOrderProductContentEditRequest.PurchaseOrderProductEditRequest purchaseOrderProductEditRequest = PurchaseOrderProductContentEditRequest.PurchaseOrderProductEditRequest.builder()
                        .SKU(key)
//                        .Quantity(values.size())
                        .CheckTypes(Arrays.asList("Quality", "Photo", "GlobalTradeItemNo", "GlobalEquipmentId"))
                        .build();
                purchaseOrderProductEditRequestList.add(purchaseOrderProductEditRequest);
            });

            PurchaseOrderProductContentEditRequest purchaseOrderProductContentEditRequest = PurchaseOrderProductContentEditRequest.builder()
                    .Items(purchaseOrderItemEditRequestList)
                    .Products(purchaseOrderProductEditRequestList)
                    .build();

            PurchaseOrderExpressEditRequest purchaseOrderExpressEditRequest = PurchaseOrderExpressEditRequest.builder()
                    .ExpressId(logisticsPurchasePackageDO.getExpressBillNo())
                    .ExpressVendorId(logisticsPurchasePackageDO.getExpressCompanyCode())
                    .ExpectedArriveOn(logisticsPurchasePackageDO.getExpectedArrive())
                    .ShipFromProvince(logisticsPurchasePackageDO.getShipFromProvince())
                    .build();

            List<PurchaseOrderExpressEditRequest> purchaseOrderExpressEditRequestList = Collections.singletonList(purchaseOrderExpressEditRequest);

            PurchaseOrderForWanB purchaseOrderForWanB = PurchaseOrderForWanB.builder()
                    .PurchaseReason(Constant.PURCHASE_WAY_SALE)         //
                    .WarehouseCode(Constant.DEPOSITORY_WAREHOUSECODE)
                    .Notes(logisticsPurchasePackageDO.getRemark())
                    .ProductContent(purchaseOrderProductContentEditRequest)
                    .Expresses(purchaseOrderExpressEditRequestList)
                    .build();

            //??????????????????
            DepositoryResponseMsgForWanB depositoryResponseMsgForWanB = wanBDepistoryUtil.purchaseorders(logisticsPurchasePackageDO.getPackageNo(), purchaseOrderForWanB);
            if (depositoryResponseMsgForWanB.isSucceeded())      //???????????????????????????????????????????????????????????????????????????
            {
                LogisticsPurchasePackageDO tempLogisticsPurchasePackageDO = new LogisticsPurchasePackageDO();
                tempLogisticsPurchasePackageDO.setId(logisticsPurchasePackageDO.getId());
                tempLogisticsPurchasePackageDO.setPrediction(PurchasePackagePredictionTypeEnum.YES.getType());
                tempLogisticsPurchasePackageDO.setPredictionTime(DateUtil.date().toJdkDate());
                tempLogisticsPurchasePackageDO.setPredictionFailReason(StringUtils.isNotBlank(logisticsPurchasePackageDO.getPredictionFailReason()) ? "????????????" + logisticsPurchasePackageDO.getPredictionFailReason() : null);
                tempLogisticsPurchasePackageDO.setValid(true); //????????????????????????????????????????????????
                logisticsPurchasePackageMapper.updateById(tempLogisticsPurchasePackageDO);      //?????????????????????
            } else {
                errorMsgList.add(depositoryResponseMsgForWanB.getError().getMessage());
                errorIdList.add(logisticsPurchasePackageDO.getId());
            }
        }
        //?????????????????????????????????
        if (errorMsgList.size() != 0) {
            String errorMessage = StringUtils.join(errorMsgList, ",");
            log.error("?????????????????????????????????: {} ", errorMessage);
            LambdaUpdateWrapper<LogisticsPurchasePackageDO> updateWrapper = Wrappers.<LogisticsPurchasePackageDO>lambdaUpdate()
                    .set(LogisticsPurchasePackageDO::getPredictionFailReason, StringUtils.isNotBlank(errorMessage) ? (errorMessage.length() > 200 ? errorMessage.substring(0, 200) : errorMessage) : "?????????????????????????????????,???????????????")
                    .in(LogisticsPurchasePackageDO::getId, errorIdList);
            logisticsPurchasePackageMapper.update(null, updateWrapper);
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200101, errorMessage);
        }
        return true;
    }


    @Override
    public File exportPurchasePackageExcel(ExportExcelReq<PurchasePackageReq> exportExcelReq) {
        List<PurchasePackageExcel> purchasePackageExcelList = this.listPurchasePackage(exportExcelReq);
        return FilePlusUtils.exportExcel(exportExcelReq, purchasePackageExcelList, PurchasePackageExcel.class);
    }

    private List<PurchasePackageExcel> listPurchasePackage(ExportExcelReq<PurchasePackageReq> exportExcelReq) {
        PurchasePackageReq purchasePackageReq = Optional.ofNullable(exportExcelReq.getData()).orElse(new PurchasePackageReq());
        List<LogisticsPackageMergeStatusDO> logisticsPackageMergeStatusDOS = null;
        if (exportExcelReq.getIdList() != null && exportExcelReq.getIdList().size() != 0) {
            List<String> idList = exportExcelReq.getIdList();
            logisticsPackageMergeStatusDOS = logisticsPurchasePackageMapper.selectLogisticsStatusOnIdList(idList);
        } else {
            logisticsPackageMergeStatusDOS = logisticsPurchasePackageMapper.selectLogisticsStatusExportList(purchasePackageReq);
        }


        // ????????????
        Map<Long, List<LogisticsPurchasePackageErrorProductDO>> errorMapByPackageId = null;
        if (!logisticsPackageMergeStatusDOS.isEmpty()) {
            Long start = logisticsPackageMergeStatusDOS.get(logisticsPackageMergeStatusDOS.size() - 1).getId();
            Long end = logisticsPackageMergeStatusDOS.get(0).getId();
            LambdaQueryWrapper<LogisticsPurchasePackageErrorProductDO> queryWrapperForError = Wrappers.<LogisticsPurchasePackageErrorProductDO>lambdaQuery()
                    .between(LogisticsPurchasePackageErrorProductDO::getPurchasePackageId, start, end);
            List<LogisticsPurchasePackageErrorProductDO> errorProductDOS = logisticsPurchasePackageErrorProductMapper.selectList(queryWrapperForError);
            errorMapByPackageId = errorProductDOS.stream().collect(Collectors.groupingBy(LogisticsPurchasePackageErrorProductDO::getPurchasePackageId));
        }
        List<PurchasePackageExcel> purchasePackageExcelList = new ArrayList<>();
        for (LogisticsPackageMergeStatusDO logisticsPackageMergeStatusDO : logisticsPackageMergeStatusDOS) {

            PurchasePackageExcel purchasePackageExcel = BeanConvertUtils.copyProperties(logisticsPackageMergeStatusDO, PurchasePackageExcel.class);
            PurchasePackageAddTypeEnum purchasePackageAddTypeEnum = PurchasePackageAddTypeEnum.getPurchasePackageAddTypeEnum(logisticsPackageMergeStatusDO.getTypeAdd());
            if (purchasePackageAddTypeEnum != null) {
                purchasePackageExcel.setTypeAdd(purchasePackageAddTypeEnum.getMsg());
            }
            purchasePackageExcel.setAcceptTime(DateUtil.formatDateTime(logisticsPackageMergeStatusDO.getAcceptTime()));
            purchasePackageExcel.setPackageSourceType(ChannelEnum.getNameByCode(logisticsPackageMergeStatusDO.getPackageSourceType()));
            purchasePackageExcel.setSignTime(DateUtil.formatDateTime(logisticsPackageMergeStatusDO.getSignTime()));
            purchasePackageExcel.setLabel(PurchasePackageLabelTypeEnum.getPurchasePackageLabelTypeEnumByType(logisticsPackageMergeStatusDO.getLabel()).getMsg());
            purchasePackageExcel.setPrediction(PurchasePackagePredictionTypeEnum.getPurchasePackagePredictionTypeEnumByType(logisticsPackageMergeStatusDO.getPrediction()).getMsg());
            purchasePackageExcel.setWarehousing(PurchasePackageWarehousingTypeEnum.getPurchasePackageWarehousingTypeEnumByType(logisticsPackageMergeStatusDO.getWarehousing()).getName());
            purchasePackageExcel.setCreateTime(DateUtil.formatDateTime(logisticsPackageMergeStatusDO.getCreateTime()));
            if (logisticsPackageMergeStatusDO.getLogisticsStatus() != null) {
                LogisticsStatusFor1688Enum logisticsStatusEnum = LogisticsStatusFor1688Enum.getLogisticsStatusEnum(logisticsPackageMergeStatusDO.getLogisticsStatus());
                if (logisticsStatusEnum != null) {
                    purchasePackageExcel.setLogisticsStatus(logisticsStatusEnum.getName());
                }
            }
            if (logisticsPackageMergeStatusDO.getReceivingGoodTime() != null)
                purchasePackageExcel.setReceivingGoodTime(DateUtil.formatDateTime(logisticsPackageMergeStatusDO.getReceivingGoodTime()));
            purchasePackageExcelList.add(purchasePackageExcel);
            if (errorMapByPackageId != null && !errorMapByPackageId.isEmpty()) {
                List<LogisticsPurchasePackageErrorProductDO> logisticsPurchasePackageErrorProductDOS = Optional.ofNullable(errorMapByPackageId.get(logisticsPackageMergeStatusDO.getId())).orElse(Collections.singletonList(new LogisticsPurchasePackageErrorProductDO()));
                purchasePackageExcel.setSaleOrder(logisticsPurchasePackageErrorProductDOS.get(0).getSalesOrderId());
                purchasePackageExcel.setPurchaseIds(logisticsPurchasePackageErrorProductDOS.stream().map(item -> item.getPurchaseId()).collect(Collectors.joining(",")));
            }
        }
        return purchasePackageExcelList;
    }


    public LambdaQueryWrapper<LogisticsPurchasePackageDO> doConditionHandler(PurchasePackageReq purchasePackageReq) {
        LambdaQueryWrapper<LogisticsPurchasePackageDO> wrapper = Wrappers.lambdaQuery();
        if (purchasePackageReq.getId() != null)     //????????????
        {
            wrapper.eq(LogisticsPurchasePackageDO::getId, purchasePackageReq.getId());
        }
        if (StringUtils.isNotBlank(purchasePackageReq.getPackageNo()))     //????????????
        {
            wrapper.eq(LogisticsPurchasePackageDO::getPackageNo, purchasePackageReq.getPackageNo());
        }
        if (purchasePackageReq.getLabel() != null)      //????????????
        {
            wrapper.eq(LogisticsPurchasePackageDO::getLabel, purchasePackageReq.getLabel());
        }

        if (purchasePackageReq.getPrediction() != null)     //????????????
        {
            wrapper.eq(LogisticsPurchasePackageDO::getPrediction, purchasePackageReq.getPrediction());
        }

        if (purchasePackageReq.getWarehousing() != null)        //????????????
        {
            wrapper.eq(LogisticsPurchasePackageDO::getWarehousing, purchasePackageReq.getWarehousing());
        }

        if (StringUtils.isNotBlank(purchasePackageReq.getExpressName()))        //????????????
        {
            wrapper.eq(LogisticsPurchasePackageDO::getExpressCompanyName, purchasePackageReq.getExpressName());
        }

        if (StringUtils.isNotBlank(purchasePackageReq.getExpressCode()))        //??????code
        {
            wrapper.likeRight(LogisticsPurchasePackageDO::getExpressCode, purchasePackageReq.getExpressCode());
        }
        if (StringUtils.isNotBlank(purchasePackageReq.getExpressBillNo()))        //????????????
        {
            wrapper.likeRight(LogisticsPurchasePackageDO::getExpressBillNo, purchasePackageReq.getExpressBillNo());
        }

        if (purchasePackageReq.getStartTime() != null)      //????????????
        {
            wrapper.ge(LogisticsPurchasePackageDO::getCreateTime, purchasePackageReq.getStartTime());
        }

        if (purchasePackageReq.getEndTime() != null)        //????????????
        {
            wrapper.le(LogisticsPurchasePackageDO::getCreateTime, purchasePackageReq.getEndTime());
        }

        if (purchasePackageReq.getStartWarehousingTime() != null)      //??????????????????
        {
            wrapper.ge(LogisticsPurchasePackageDO::getReceivingGoodTime, purchasePackageReq.getStartWarehousingTime());
        }

        if (purchasePackageReq.getEndWarehousingTime() != null)        //??????????????????
        {
            wrapper.le(LogisticsPurchasePackageDO::getReceivingGoodTime, purchasePackageReq.getEndWarehousingTime());
        }
        if (purchasePackageReq.getTypeAdd() != null)        // ????????????
        {
            wrapper.eq(LogisticsPurchasePackageDO::getTypeAdd, purchasePackageReq.getTypeAdd());
        }

        wrapper.orderByDesc(LogisticsPurchasePackageDO::getCreateTime);
        return wrapper;
    }

    @Override
    public List<PurchasePackageLogisticsInfoVO> getPurchasePackageLogisticsInfo(List<String> purchaseIdList) {
        LambdaQueryWrapper<LogisticsPurchasePackageErrorProductDO> queryWrapper = Wrappers.lambdaQuery(LogisticsPurchasePackageErrorProductDO.class)
                .in(LogisticsPurchasePackageErrorProductDO::getPurchaseId, purchaseIdList);
        List<LogisticsPurchasePackageErrorProductDO> errorProductDOS_1 = logisticsPurchasePackageErrorProductMapper.selectList(queryWrapper);
        // ????????????id????????????????????????????????????????????????purchaseId
        Collections.sort(errorProductDOS_1, ((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())));
        Map<String, List<LogisticsPurchasePackageErrorProductDO>> mapByPurchaseId = errorProductDOS_1.stream().collect(Collectors.groupingBy(LogisticsPurchasePackageErrorProductDO::getPurchaseId));
        ArrayList<LogisticsPurchasePackageErrorProductDO> errorProductDOS = new ArrayList<>();
        mapByPurchaseId.entrySet().stream().forEach(entry -> {
            errorProductDOS.add(entry.getValue().get(0));
        });
        Map<Long, List<LogisticsPurchasePackageErrorProductDO>> map = errorProductDOS.stream().collect(Collectors.groupingBy(LogisticsPurchasePackageErrorProductDO::getPurchasePackageId));
        if (map.keySet().size() < 1) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<LogisticsPurchasePackageDO> queryWrapperForLogisticsPurchasePackage = Wrappers.lambdaQuery(LogisticsPurchasePackageDO.class)
                .in(LogisticsPurchasePackageDO::getId, map.keySet().stream().collect(Collectors.toList()));
        List<LogisticsPurchasePackageDO> logisticsPurchasePackageDOS = logisticsPurchasePackageMapper.selectList(queryWrapperForLogisticsPurchasePackage);
        //????????????
        List<PurchasePackageLogisticsInfoVO> list = new ArrayList<>();
        for (Map.Entry<Long, List<LogisticsPurchasePackageErrorProductDO>> entry : map.entrySet()) {
            Optional<LogisticsPurchasePackageDO> optional = logisticsPurchasePackageDOS.stream().filter(vo -> vo.getId().compareTo(entry.getKey()) == 0).findFirst();
            if (optional.isPresent()) {
                entry.getValue().stream().forEach(item -> {
                    LogisticsPurchasePackageDO packageDO = optional.get();
                    PurchasePackageLogisticsInfoVO info = new PurchasePackageLogisticsInfoVO();
                    info.setSalesOrderId(item.getSalesOrderId());
                    info.setItemId(item.getItemId());
                    info.setPurchaseId(item.getPurchaseId());
                    info.setPackageNo(packageDO.getPackageNo());
                    info.setExpressCompanyCode(packageDO.getExpressCompanyCode());
                    info.setExpressCompanyName(packageDO.getExpressCompanyName());
                    info.setExpressBillNo(packageDO.getExpressBillNo());
                    info.setExpressStatus(packageDO.getStatus());
                    list.add(info);
                });
            }
        }
        return list;
    }

    private void query1688IfPurchasePackageIsPrediction(List<LogisticsPurchasePackageDO> logisticsPurchasePackageDOList) {
        for (LogisticsPurchasePackageDO item : logisticsPurchasePackageDOList) {
            LambdaQueryWrapper<LogisticsPurchasePackageErrorProductDO> queryWrapperForError = Wrappers.<LogisticsPurchasePackageErrorProductDO>lambdaQuery()
                    .eq(LogisticsPurchasePackageErrorProductDO::getPurchasePackageId, item.getId());
            List<LogisticsPurchasePackageErrorProductDO> errorProductDOS = logisticsPurchasePackageErrorProductMapper.selectList(queryWrapperForError);
            if (Objects.isNull(errorProductDOS) || errorProductDOS.size() < 1) {
                continue;
            }
            long purchaseIdIsNullCount = errorProductDOS.stream().filter(vo -> StringUtils.isBlank(vo.getPurchaseId())).count();
            long purchaseIdIsNotNullCount = errorProductDOS.stream().filter(vo -> StringUtils.isNotBlank(vo.getPurchaseId())).count();
            // ??????????????????????????????????????????????????????1688??????????????????????????????????????????purchaseId????????????????????????????????????????????????
            if (purchaseIdIsNullCount > 0 && purchaseIdIsNotNullCount < 1) {
                LogisticsPurchasePackageBO packageBO = translateLogisticPurchasePackageDoToLogisticsPurchasePackageBO(item, errorProductDOS);
                purchase1688NoticeDepositoryConsumer.translate1688SkuToLocate(item.getChannelOrderId(), Collections.singletonList(packageBO));
            }
        }
    }


    @Transactional
    @Override
    public Boolean againPurchase(PurchasePackageErrorHandleReq purchasePackageErrorHandleReq) {
        LogisticsPurchasePackageErrorProductDO packageErrorProductDO = checkPackageErrorExsit(purchasePackageErrorHandleReq);
        Integer channelType = purchasePackageErrorHandleReq.getChannelType();
        // 1688
        PurchaseIdRepurchaseForLogisticsResult result1 = PurchaseIdRepurchaseForLogisticsResult.newBuilder().setSuccess(false).build();
        // pdd ??? taobao
        PurchaseIdRepurchaseForOtherOrderResult result2 = PurchaseIdRepurchaseForOtherOrderResult.newBuilder().setSuccess(false).build();
        log.info("GRPC ???????????????????????????????????????{}", JSON.toJSONString(packageErrorProductDO));
        if (DeliveryPackagePurchaseChannelEnum.ALIBABA.getType().equals(channelType)) {
            PurchaseIdRepurchaseForLogisticsRequest request = PurchaseIdRepurchaseForLogisticsRequest.newBuilder().addAllPurchaseId(Collections.singletonList(packageErrorProductDO.getPurchaseId())).build();
            result1 = purchaseIdInfoSynchronizeServiceGrpcClient.purchaseIdRepurchaseForLogistics(request);
            log.info("GRPC ??????????????????????????????????????????{}", GrpcJsonFormatUtils.toJsonString(result1));
        } else if (DeliveryPackagePurchaseChannelEnum.PDD.getType().equals(channelType) || DeliveryPackagePurchaseChannelEnum.TAOBAO.getType().equals(channelType)) {
            PurchaseIdRepurchaseForOtherOrder otherOrder = PurchaseIdRepurchaseForOtherOrder.newBuilder().setPurchaseId(packageErrorProductDO.getPurchaseId()).build();
            PurchaseIdRepurchaseForOtherOrderRequest orderRequest = PurchaseIdRepurchaseForOtherOrderRequest.newBuilder().addAllPurchaseIdOtherOrder(Collections.singletonList(otherOrder))
                    .setLockPeople(Optional.ofNullable(UserContext.getCurrentUserName()).orElseGet(() -> Constant.SYSTEM_ROOT)).build();
            result2 =purchaseIdInfoSynchronizeServiceGrpcClient.purchaseIdRepurchaseForOtherOrder(orderRequest);
            log.info("GRPC ??????????????????????????????????????????{}", GrpcJsonFormatUtils.toJsonString(result2));
        }
        if ((result1.getSuccess() && result1.getData()) || (result2.getSuccess() && result2.getData())) {
            LogisticsPurchasePackageErrorProductDO tempLogisticsPurchasePackageProductDO = new LogisticsPurchasePackageErrorProductDO();
            tempLogisticsPurchasePackageProductDO.setId(packageErrorProductDO.getId());
            tempLogisticsPurchasePackageProductDO.setErrorHandle(PurchasePackageProductErrorHandleTypeEnum.REPURCHASED.getType());
            tempLogisticsPurchasePackageProductDO.setStatus(DepositoryPurchaseStatusEnum.HANDLEDEXCEPTION.getStatusCode());
            int rows = logisticsPurchasePackageErrorProductMapper.updateById(tempLogisticsPurchasePackageProductDO);
            if (rows > 0) {
                tempLogisticsPurchasePackageProductDO.setItemId(packageErrorProductDO.getItemId());
                tempLogisticsPurchasePackageProductDO.setPurchaseId(packageErrorProductDO.getPurchaseId());
                tempLogisticsPurchasePackageProductDO.setSalesOrderId(packageErrorProductDO.getSalesOrderId());
                casecadeUpdateDepositoryStatus(tempLogisticsPurchasePackageProductDO);
                sendHandleExceptionMsgToMq(tempLogisticsPurchasePackageProductDO);
                updatePurchasePackageLabel(packageErrorProductDO);
                return Boolean.TRUE;
            }
        }
        throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500, "??????????????????");
    }


    private LogisticsPurchasePackageErrorProductDO checkPackageErrorExsit(PurchasePackageErrorHandleReq purchasePackageErrorHandleReq) {
        LambdaQueryWrapper<LogisticsPurchasePackageErrorProductDO> logisticsPurchasePackageErrorProductDOLambdaQueryWrapper = Wrappers.<LogisticsPurchasePackageErrorProductDO>lambdaQuery()
                .eq(LogisticsPurchasePackageErrorProductDO::getId, purchasePackageErrorHandleReq.getId());
        LogisticsPurchasePackageErrorProductDO logisticsPurchasePackageErrorProductDO = logisticsPurchasePackageErrorProductMapper.selectOne(logisticsPurchasePackageErrorProductDOLambdaQueryWrapper);
        if (logisticsPurchasePackageErrorProductDO == null) {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "?????????????????????");
        }

        String errorType = logisticsPurchasePackageErrorProductDO.getErrorType();
        Integer errorHandle = logisticsPurchasePackageErrorProductDO.getErrorHandle();
        if (errorType.equals(PurchasePackageProductErrorTypeEnum.NONE.getType()) || errorHandle == null)     //??????????????? NONE ?????? ????????????????????????
        {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "??????????????????");
        }

        if (!errorHandle.equals(PurchasePackageProductErrorHandleTypeEnum.PENDING_ERROR.getType()))      //?????????????????? != ?????????????????????
        {
            throw new GlobalException(BaseLogisticsResponseCodeEnum.RESPONSE_CODE_200100, "??????????????????");
        }
        return logisticsPurchasePackageErrorProductDO;
    }

    private boolean updatePurchasePackageLabel(LogisticsPurchasePackageErrorProductDO logisticsPurchasePackageErrorProductDO) {
        //?????????????????????????????????
        LambdaQueryWrapper<LogisticsPurchasePackageErrorProductDO> lambdaQueryWrapper = Wrappers.<LogisticsPurchasePackageErrorProductDO>lambdaQuery()
                .eq(LogisticsPurchasePackageErrorProductDO::getPurchasePackageId, logisticsPurchasePackageErrorProductDO.getPurchasePackageId());
        List<LogisticsPurchasePackageErrorProductDO> logisticsPurchasePackageProductDOList = logisticsPurchasePackageErrorProductMapper.selectList(lambdaQueryWrapper);
        logisticsPurchasePackageProductDOList.stream().forEach(item -> {
            if (Constant.YES.equals(item.getHidden())) {
                // ?????????????????????????????????????????????????????????????????????
                item.setStatus(DepositoryPurchaseStatusEnum.HANDLEDEXCEPTION.getStatusCode());
                item.setErrorHandle(PurchasePackageProductErrorHandleTypeEnum.DIRECT_WAREHOUSING.getType());
            }
        });
        boolean logisticsPurchasePackageLabelFlag = logisticsPurchasePackageProductDOList.stream().anyMatch(lpppd -> lpppd.getErrorHandle() != null && lpppd.getErrorHandle().equals(PurchasePackageProductErrorHandleTypeEnum.PENDING_ERROR.getType()));
        //???????????????????????????????????? sku ???????????? ??????????????? ???sku, ????????????????????????
        if (logisticsPurchasePackageLabelFlag) {
            return Boolean.TRUE;
        }
        //??????????????????????????????????????? 2 ???????????????
        LogisticsPurchasePackageDO logisticsPurchasePackageDO = new LogisticsPurchasePackageDO();
        logisticsPurchasePackageDO.setId(logisticsPurchasePackageErrorProductDO.getPurchasePackageId());
        logisticsPurchasePackageDO.setLabel(PurchasePackageLabelTypeEnum.HANDLED_ERROR.getType());
        logisticsPurchasePackageDO.setUpdateBy(UserContext.getCurrentUserName());
        int updateById = logisticsPurchasePackageMapper.updateById(logisticsPurchasePackageDO);
        return updateById > 0 ? true : false;
    }

    public LogisticsPurchasePackageBO translateLogisticPurchasePackageDoToLogisticsPurchasePackageBO(LogisticsPurchasePackageDO logisticsPurchasePackageDO, List<LogisticsPurchasePackageErrorProductDO> errorProductDOS) {
        // ??????????????????1688??????????????????
        LambdaQueryWrapper<LogisticsPurchaseProductDO> queryWrapperForProduct = Wrappers.<LogisticsPurchaseProductDO>lambdaQuery()
                .eq(LogisticsPurchaseProductDO::getPackageId, logisticsPurchasePackageDO.getId());
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
        // ????????????
        errorProductBOList.stream().forEach(errorProductBO -> {
            productBOList.stream().forEach(productBO -> {
                if (productBO.getId().compareTo(errorProductBO.getProductId()) == 0) {
                    productBO.getLogisticsPurchasePackageErrorProductList().add(errorProductBO);
                }
            });
        });
        LogisticsPurchasePackageBO packageBO = new LogisticsPurchasePackageBO();
        packageBO.setId(logisticsPurchasePackageDO.getId());
        packageBO.getLogisticsProductList().addAll(productBOList);
        return packageBO;
    }

    @Transactional
    @Override
    public Boolean pullPruchaseOrderAndPrediction(PruchaseOrderAndPredictionReq pruchaseOrderAndPredictionReq) {
        boolean prediction = false;
        String pacakgeNo = logisticsCommonUtils.generateDepositoryPredictionPackageNo();
        pacakgeNo = "TS" + pacakgeNo.replace(Constant.DEPOSITORY_LOGO, "");
        if (StringUtils.isBlank(pruchaseOrderAndPredictionReq.getExpressBillNo())) {
            pruchaseOrderAndPredictionReq.setExpressBillNo(pacakgeNo);
        }
        if (pruchaseOrderAndPredictionReq.getPrediction() == PurchasePackagePredictionTypeEnum.NO.getType()) {
            prediction = true;
        }
        // ???????????????
        LogisticsPurchasePackageDO packageDO = new LogisticsPurchasePackageDO();
        packageDO.setId(SnowflakeIdUtils.getId());
        packageDO.setChannelOrderId(pruchaseOrderAndPredictionReq.getChannelOrderId());
        packageDO.setPrediction(pruchaseOrderAndPredictionReq.getPrediction());
        packageDO.setExpressCompanyName(pruchaseOrderAndPredictionReq.getExpressCompanyName());
        packageDO.setExpressCompanyCode(pruchaseOrderAndPredictionReq.getExpressCompanyCode());
        packageDO.setPackageNo(pacakgeNo);
        packageDO.setExpressBillNo(pruchaseOrderAndPredictionReq.getExpressBillNo());
        logisticsPurchasePackageMapper.insert(packageDO);

        // ???????????????
        Map<String, List<PruchaseOrderAndPredictionReq.Detail>> details = pruchaseOrderAndPredictionReq.getList().stream().collect(Collectors.groupingBy(PruchaseOrderAndPredictionReq.Detail::getSku));
        List<LogisticsPurchaseProductDO> productList = details.entrySet().stream().map(entity -> {
            List<PruchaseOrderAndPredictionReq.Detail> items = entity.getValue();
            PruchaseOrderAndPredictionReq.Detail item = items.get(0);
            LogisticsPurchaseProductDO productDO = new LogisticsPurchaseProductDO();
            productDO.setId(SnowflakeIdUtils.getId());
            productDO.setPackageId(packageDO.getId());
            productDO.setProductImg(item.getProductImg());
            productDO.setProductName(item.getProductName());
            productDO.setProductAttribute(item.getProductAttribute());
            productDO.setProductCount(items.size());
            productDO.setSku(item.getSku());
            return productDO;
        }).collect(Collectors.toList());
        productList.stream().forEach(item -> {
            logisticsPurchaseProductMapper.insert(item);
        });

        // ????????????
        pruchaseOrderAndPredictionReq.getList().stream().forEach(item -> {
            LogisticsPurchaseProductDO productDO = productList.stream().filter(product -> product.getSku() != null)
                    .filter(product -> product.getSku().equals(item.getSku())).findFirst().orElse(null);
            if (productDO != null) {
                LogisticsPurchasePackageErrorProductDO errorProductDO = new LogisticsPurchasePackageErrorProductDO();
                errorProductDO.setId(SnowflakeIdUtils.getId());
                errorProductDO.setPurchasePackageId(packageDO.getId());
                errorProductDO.setProductId(productDO.getId());
                errorProductDO.setItemId(item.getItemId());
                errorProductDO.setPurchaseId(item.getPurchaseId());
                errorProductDO.setSalesOrderId(item.getSalesOrderId());
                errorProductDO.setSku(item.getSku());
                logisticsPurchasePackageErrorProductMapper.insert(errorProductDO);
            }
        });
        if (prediction) {
            PurchasePackagePredictionReq purchasePackagePredictionReq = new PurchasePackagePredictionReq();
            purchasePackagePredictionReq.setIdList(Collections.singletonList(packageDO.getId()));
            prediction(purchasePackagePredictionReq);
        }
        return Boolean.TRUE;
    }

    @Transactional
    @Override
    public Boolean manualPredictionPurchaseId(ManualPredictionReq manualPredictionReq) {
        String expressBillNo = manualPredictionReq.getExpressBillNo();
        LambdaQueryWrapper<LogisticsPurchasePackageDO> queryWrapperForDepositorySaleOrderPackage = Wrappers.<LogisticsPurchasePackageDO>lambdaQuery()
                .in(LogisticsPurchasePackageDO::getExpressBillNo, expressBillNo);
        LogisticsPurchasePackageDO logisticsPurchasePackageDO = logisticsPurchasePackageMapper.selectOne(queryWrapperForDepositorySaleOrderPackage);
        PurchasePackagePredictionReq purchasePackagePredictionReq = new PurchasePackagePredictionReq();
        if (logisticsPurchasePackageDO == null || logisticsPurchasePackageDO.getId() == null) {
            // ??????
            List<Long> predictionList = addManualPredictionPackage(manualPredictionReq);
            purchasePackagePredictionReq.setIdList(predictionList);
            try {
                if (purchasePackagePredictionReq.getIdList().isEmpty()) {
                    return false;
                }
                return prediction(purchasePackagePredictionReq);
            } catch (Exception e) {
                log.error("???????????????{}", e);
            }
        } else {
            // ??????
            if (logisticsPurchasePackageDO.getWarehousing() == PurchasePackageWarehousingTypeEnum.YES.getType()) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "?????????????????????????????????????????????????????????");
            }
            List<Long> predictionList = updateManualPredictionPackage(manualPredictionReq, logisticsPurchasePackageDO);
            purchasePackagePredictionReq.setIdList(predictionList);
            try {
                if (purchasePackagePredictionReq.getIdList().isEmpty()) {
                    return false;
                }
                return updatePrediction(purchasePackagePredictionReq);
            } catch (Exception e) {
                log.error("???????????????{}", e);
            }
        }
        return false;
    }

    @Override
    public boolean updatePurchasePackageByPurchaseId(String purchaseId) {
        LambdaQueryWrapper<LogisticsPurchasePackageErrorProductDO> queryPurchaseId = Wrappers.<LogisticsPurchasePackageErrorProductDO>lambdaQuery().eq(LogisticsPurchasePackageErrorProductDO::getPurchaseId, purchaseId)
                .eq(LogisticsPurchasePackageErrorProductDO::getStatus, DepositoryPurchaseStatusEnum.WAITINGWAREHOUSING.getStatusCode());
        LogisticsPurchasePackageErrorProductDO logisticsPurchasePackageErrorProductDO = logisticsPurchasePackageErrorProductMapper.selectOne(queryPurchaseId);
        if (Objects.isNull(logisticsPurchasePackageErrorProductDO)) {
            return true;
        }
        logisticsPurchasePackageErrorProductDO.setIsDeleted(Constant.YES);
        LambdaUpdateWrapper<LogisticsPurchasePackageErrorProductDO> updateForError = Wrappers.<LogisticsPurchasePackageErrorProductDO>lambdaUpdate().set(LogisticsPurchasePackageErrorProductDO::getIsDeleted, Constant.YES)
                .eq(LogisticsPurchasePackageErrorProductDO::getId, logisticsPurchasePackageErrorProductDO.getId());
        int updateToDelete = logisticsPurchasePackageErrorProductMapper.update(null,updateForError);
        if (updateToDelete > 0) {
            LogisticsPurchasePackageDO logisticsPurchasePackageDO = logisticsPurchasePackageMapper.selectById(logisticsPurchasePackageErrorProductDO.getPurchasePackageId());
            logisticsPurchasePackageDO.setPrediction(Constant.NO_0);
            logisticsPurchasePackageMapper.updateById(logisticsPurchasePackageDO);
            PurchasePackagePredictionReq purchasePackagePredictionReq = new PurchasePackagePredictionReq();
            purchasePackagePredictionReq.setIdList(Collections.singletonList(logisticsPurchasePackageDO.getId()));
            return prediction(purchasePackagePredictionReq);
        }
        return false;
    }

    private Boolean updatePrediction(PurchasePackagePredictionReq purchasePackagePredictionReq) {
        return prediction(purchasePackagePredictionReq);
    }

    private List<Long> updateManualPredictionPackage(ManualPredictionReq manualPredictionReq, LogisticsPurchasePackageDO logisticsPurchasePackageDO) {
        Map<String, List<ManualPredictionReq.PredictionDetail>> manualMapBySku = checkManualPredictionPackage(manualPredictionReq);
        return updatePurchasePackageAndItem(manualPredictionReq, manualMapBySku, logisticsPurchasePackageDO);
    }

    private List<Long> addManualPredictionPackage(ManualPredictionReq manualPredictionReq) {
        Map<String, List<ManualPredictionReq.PredictionDetail>> manualMapBySku = checkManualPredictionPackage(manualPredictionReq);
        return savePurchasePackageAndItem(manualPredictionReq, manualMapBySku);
    }

    private Map<String, List<ManualPredictionReq.PredictionDetail>> checkManualPredictionPackage(ManualPredictionReq manualPredictionReq) {
        // ?????????purchaseId
        List<ManualPredictionReq.PredictionDetail> predictionDetails = manualPredictionReq.getPredictionDetails();
        // sku?????????????????????
        List<ManualPredictionReq.SkuAmount> skuAmounts = manualPredictionReq.getSkus();
        Map<String, List<ManualPredictionReq.PredictionDetail>> manMapBySkuPrimary = predictionDetails.stream().collect(Collectors.groupingBy(ManualPredictionReq.PredictionDetail::getSku));
        // ????????????????????????
        if (skuAmounts != null && !skuAmounts.isEmpty()) {
            skuAmounts.stream().forEach(item -> {
                if (item.getAmount() > manMapBySkuPrimary.get(item.getName()).size()) {
                    throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "???????????????????????????????????????????????????");
                }
            });
        }
        List<String> predictionPurchaseList_primary = predictionDetails.stream().map(ManualPredictionReq.PredictionDetail::getPurchaseId).collect(Collectors.toList());
        List<String> predictionPurchaseList = new ArrayList<>();
        // ??????purchaseId-1??????
        predictionPurchaseList_primary.stream().forEach(purchaseId -> {
            predictionPurchaseList.add(purchaseId);
//            if (purchaseId.contains("-")) {
//                predictionPurchaseList.add(StringUtils.substring(purchaseId, 0, purchaseId.indexOf("-")));
//            }
        });
        LambdaQueryWrapper<LogisticsPurchasePackageErrorProductDO> queryWrapperForErrorProduct = Wrappers.<LogisticsPurchasePackageErrorProductDO>lambdaQuery()
                .in(LogisticsPurchasePackageErrorProductDO::getPurchaseId, predictionPurchaseList);
        List<LogisticsPurchasePackageErrorProductDO> logisticsPurchasePackageErrorProductDOS = logisticsPurchasePackageErrorProductMapper.selectList(queryWrapperForErrorProduct);
        // ?????????????????????
        List<String> predictionedPurchaseId = logisticsPurchasePackageErrorProductDOS.stream().filter(item -> item.getStatus() == DepositoryPurchaseStatusEnum.WAREHOUSING.getStatusCode())
                .map(LogisticsPurchasePackageErrorProductDO::getPurchaseId).collect(Collectors.toList());
        List<LogisticsPurchasePackageErrorProductDO> predictionedButErrorPurchase = logisticsPurchasePackageErrorProductDOS.stream().filter(item -> item.getStatus() != DepositoryPurchaseStatusEnum.WAREHOUSING.getStatusCode())
                .collect(Collectors.toList());
        // ?????????????????????
        List<ManualPredictionReq.PredictionDetail> needPredictionDetails = predictionDetails.stream().filter(item -> !predictionedPurchaseId.contains(item.getPurchaseId())).collect(Collectors.toList());
        if (needPredictionDetails.isEmpty()) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "???????????????id???????????????????????????????????????????????????id?????????id ??????" + predictionedPurchaseId);
        }
        Map<String, List<ManualPredictionReq.PredictionDetail>> manualMapBySku = needPredictionDetails.stream().collect(Collectors.groupingBy(ManualPredictionReq.PredictionDetail::getSku));
        List<LogisticsPurchasePackageErrorProductDO> needDeletedPredictionedButErrorPurchase = new ArrayList<>();
        // sku??????????????????
        if (skuAmounts != null && !skuAmounts.isEmpty()) {
            skuAmounts.stream().forEach(item -> {
                if (item.getAmount() >= 0) {
                    // ????????????????????????????????????
                    List<ManualPredictionReq.PredictionDetail> predictionIdList = manualMapBySku.get(item.getName()).stream().limit(item.getAmount()).collect(Collectors.toList());
                    if (predictionIdList.isEmpty()) {
                        manualMapBySku.remove(item.getName());
                    } else {
                        // ????????? ??????????????????????????????????????????????????????
                        List<ManualPredictionReq.PredictionDetail> predictionDetailsNotSort = manualMapBySku.get(item.getName());
                        List<String> predictionedButErrorPurchaseId = predictionedButErrorPurchase.stream().filter(error -> error.getSku().equals(item.getName())).map(LogisticsPurchasePackageErrorProductDO::getPurchaseId).collect(Collectors.toList());
                        List<ManualPredictionReq.PredictionDetail> predictionDetailsExclusionPredictionedButErrorPurchaseId = predictionDetailsNotSort.stream().filter(detail -> !predictionedButErrorPurchaseId.contains(detail.getPurchaseId())).collect(Collectors.toList());
                        if (predictionDetailsExclusionPredictionedButErrorPurchaseId.size() < item.getAmount()) {
                            int count = item.getAmount() - predictionDetailsExclusionPredictionedButErrorPurchaseId.size();
                            List<LogisticsPurchasePackageErrorProductDO> addPredictionedButErrorPurchase = predictionedButErrorPurchase.stream().filter(error -> error.getSku().equals(item.getName())).limit(count).collect(Collectors.toList());
                            List<String> addPredictionedButErrorPurchaseId = addPredictionedButErrorPurchase.stream().map(LogisticsPurchasePackageErrorProductDO::getPurchaseId).collect(Collectors.toList());
                            needDeletedPredictionedButErrorPurchase.addAll(addPredictionedButErrorPurchase);
                            List<ManualPredictionReq.PredictionDetail> addPredictionedButErrorPurchaseIdPredictionDetail = predictionDetailsNotSort.stream().filter(detail -> addPredictionedButErrorPurchaseId.contains(detail.getPurchaseId())).limit(count).collect(Collectors.toList());
                            predictionDetailsExclusionPredictionedButErrorPurchaseId.addAll(addPredictionedButErrorPurchaseIdPredictionDetail);
                        }
                        manualMapBySku.put(item.getName(), predictionIdList);
                    }
                }
            });
            long count = manualMapBySku.entrySet().stream().filter(entry -> entry.getValue() == null || entry.getValue().isEmpty()).count();
            if (count == manualMapBySku.keySet().size()) {
                throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "???????????????????????????????????????????????????????????????");
            }
        } else {
            needDeletedPredictionedButErrorPurchase.addAll(predictionedButErrorPurchase);
        }
        // ?????? saleOrder
        List<ManualPredictionReq.PredictionDetail> needPredictionList = new ArrayList<>();
        for (Map.Entry<String, List<ManualPredictionReq.PredictionDetail>> entry : manualMapBySku.entrySet()) {
            needPredictionList.addAll(entry.getValue());
        }
        // ?????????????????????????????????????????????????????????
        LambdaQueryWrapper<DepositorySaleOrderDetailDO> queryWrapperForDepositorySaleOrderDetail = Wrappers.<DepositorySaleOrderDetailDO>lambdaQuery()
                .in(DepositorySaleOrderDetailDO::getPurchaseId, needPredictionList.stream().map(ManualPredictionReq.PredictionDetail::getPurchaseId).collect(Collectors.toList()));
        List<DepositorySaleOrderDetailDO> depositorySaleOrderDetailDOS = depositorySaleOrderDetailMapper.selectList(queryWrapperForDepositorySaleOrderDetail);
        needPredictionList.stream().forEach(item -> {
            depositorySaleOrderDetailDOS.stream().forEach(detail -> {
                if (detail.getPurchaseId().equals(item.getPurchaseId())) {
                    item.setSalesOrderId(detail.getSaleOrder());
                    item.setItemId(detail.getItemId());
                }
            });
        });
        // ????????????????????????????????????????????????
        if (!needDeletedPredictionedButErrorPurchase.isEmpty()) {
            List<ManualPredictionReq.PredictionDetail> newPredictionTORequiredDeleted = needDeletedPredictionedButErrorPurchase.stream().map(item -> {
                ManualPredictionReq.PredictionDetail detail = new ManualPredictionReq.PredictionDetail();
                detail.setId(item.getId());
                detail.setPurchaseId(item.getPurchaseId());
                detail.setStatus(item.getStatus());
                detail.setPurchasePackageId(item.getPurchasePackageId());
                return detail;
            }).collect(Collectors.toList());
            // newPredictionTORequiredDeleted ??????????????????????????????????????????????????????
            manualMapBySku.put("newPredictionTORequiredDeleted", newPredictionTORequiredDeleted);
        }
        return manualMapBySku;
    }

    private List<Long> savePurchasePackageAndItem(ManualPredictionReq manualPredictionReq, Map<String, List<ManualPredictionReq.PredictionDetail>> manualMapBySku) {
        List<Long> againPrediction = updatePurchasePackageErrorProductErrorTableToDeleted(manualMapBySku);
        String expressBillNo = manualPredictionReq.getExpressBillNo();
        String expressCompanyName = manualPredictionReq.getExpressCompanyName();
        String operationer = manualPredictionReq.getOperationer();
        String channelOrders = manualPredictionReq.getPredictionDetails().stream().map(ManualPredictionReq.PredictionDetail::getChannelOrderId).distinct().collect(Collectors.joining(","));
        // ???????????????
        LogisticsPurchasePackageDO packageDO = new LogisticsPurchasePackageDO();
        packageDO.setPackageNo(logisticsCommonUtils.generateDepositoryPredictionPackageNo());
        packageDO.setExpressCompanyName(expressCompanyName);
        packageDO.setExpressBillNo(expressBillNo);
        packageDO.setChannelOrderId(channelOrders);
        packageDO.setPrediction(PurchasePackagePredictionTypeEnum.NO.getType());
        packageDO.setWarehousing(PurchasePackageWarehousingTypeEnum.NO.getType());
        packageDO.setDeliverGoodTime(new Date());
        packageDO.setCreateBy(operationer);
        // cause: ??????????????????????????????
        packageDO.setTypeAdd(Objects.isNull(ChannelOrderLogisticsService.getAddType()) ? PurchasePackageAddTypeEnum.MANUAL_ADD.getType() : ChannelOrderLogisticsService.getAddType());
        packageDO.setPackageSourceType(manualPredictionReq.getPackageSourceType());
        logisticsPurchasePackageMapper.insert(packageDO);
        insertProductAndErrorItem(packageDO, manualMapBySku, operationer);
        againPrediction.add(packageDO.getId());
        return againPrediction;
    }

    private List<Long> updatePurchasePackageAndItem(ManualPredictionReq manualPredictionReq, Map<String, List<ManualPredictionReq.PredictionDetail>> manualMapBySku, LogisticsPurchasePackageDO packageDO) {
        List<Long> againPrediction = updatePurchasePackageErrorProductErrorTableToDeleted(manualMapBySku);
        String operationer = manualPredictionReq.getOperationer();
        Set<String> channelOrders = manualPredictionReq.getPredictionDetails().stream().map(ManualPredictionReq.PredictionDetail::getChannelOrderId).collect(Collectors.toSet());
        channelOrders.addAll(Arrays.asList(packageDO.getChannelOrderId().split(",")));
        LambdaUpdateWrapper<LogisticsPurchasePackageDO> updateWrapper = Wrappers.<LogisticsPurchasePackageDO>lambdaUpdate()
                .set(LogisticsPurchasePackageDO::getChannelOrderId, channelOrders.stream().collect(Collectors.joining(",")))
                .set(LogisticsPurchasePackageDO::getPrediction, PurchasePackagePredictionTypeEnum.NO.getType())
                .set(LogisticsPurchasePackageDO::getUpdateBy, operationer)
                .set(LogisticsPurchasePackageDO::getPackageSourceType, manualPredictionReq.getPackageSourceType())
                .eq(LogisticsPurchasePackageDO::getId, packageDO.getId());
        int update = logisticsPurchasePackageMapper.update(null, updateWrapper);
        if (update > 0) {
            insertProductAndErrorItem(packageDO, manualMapBySku, operationer);
            againPrediction.add(packageDO.getId());
        }
        return againPrediction;
    }

    private void insertProductAndErrorItem(LogisticsPurchasePackageDO packageDO, Map<String, List<ManualPredictionReq.PredictionDetail>> manualMapBySku, String operationer) {
        manualMapBySku.entrySet().stream().forEach(skuItem -> {
            LogisticsPurchaseProductDO logisticsPurchaseProduct = new LogisticsPurchaseProductDO();
            logisticsPurchaseProduct.setProductName(skuItem.getValue().get(0).getProductName());
            logisticsPurchaseProduct.setPackageId(packageDO.getId());
            logisticsPurchaseProduct.setSkuChannel(skuItem.getKey());
            logisticsPurchaseProduct.setSku(skuItem.getKey());
            logisticsPurchaseProduct.setProductImg(Objects.isNull(skuItem.getValue().get(0).getProductImg()) ? null : skuItem.getValue().get(0).getProductImg());
            logisticsPurchaseProduct.setProductAttribute(Objects.isNull(skuItem.getValue().get(0).getProductAttribute()) ? null : skuItem.getValue().get(0).getProductAttribute());
            logisticsPurchaseProduct.setProductCount(skuItem.getValue().size());
            logisticsPurchaseProductMapper.insert(logisticsPurchaseProduct);
            skuItem.getValue().stream()
                    .forEach(v -> {
                        LogisticsPurchasePackageErrorProductDO errorProductDO = new LogisticsPurchasePackageErrorProductDO();
                        errorProductDO.setProductId(logisticsPurchaseProduct.getId());
                        errorProductDO.setPurchasePackageId(packageDO.getId());
                        errorProductDO.setSalesOrderId(v.getSalesOrderId());
                        errorProductDO.setPurchaseId(v.getPurchaseId());
                        errorProductDO.setItemId(v.getItemId());
                        errorProductDO.setCreateBy(operationer);
                        errorProductDO.setSku(String.valueOf(v.getSku()).length() > 90 ? String.valueOf(v.getSku()).substring(0, 90) : String.valueOf(v.getSku()));
                        errorProductDO.setChannleSku(String.valueOf(v.getSku()).length() > 90 ? String.valueOf(v.getSku()).substring(0, 90) : String.valueOf(v.getSku()));
                        logisticsPurchasePackageErrorProductMapper.insert(errorProductDO);
                    });
        });
    }

    private List<Long> updatePurchasePackageErrorProductErrorTableToDeleted(Map<String, List<ManualPredictionReq.PredictionDetail>> manualMapBySku) {
        List<Long> againPrediction = new ArrayList<>();
        boolean isUpdatePredictionStatus = false;
        int update = 0;
        List<ManualPredictionReq.PredictionDetail> newPredictionTORequiredDeleted = manualMapBySku.get("newPredictionTORequiredDeleted");
        if (newPredictionTORequiredDeleted != null && !newPredictionTORequiredDeleted.isEmpty()) {
            for (ManualPredictionReq.PredictionDetail predictionDetail : newPredictionTORequiredDeleted) {
                if (predictionDetail.getStatus() == DepositoryPurchaseStatusEnum.HANLDINGEXCEPTION.getStatusCode()) {
                    // ??????????????????????????????????????????0 ????????? 1 ??????
                    LambdaUpdateWrapper<LogisticsPurchasePackageErrorProductDO> updateWrapper = Wrappers.<LogisticsPurchasePackageErrorProductDO>lambdaUpdate()
                            .set(LogisticsPurchasePackageErrorProductDO::getHidden, Constant.YES)
                            .set(LogisticsPurchasePackageErrorProductDO::getStatus,DepositoryPurchaseStatusEnum.HANDLEDEXCEPTION.getStatusCode())
                            .set(LogisticsPurchasePackageErrorProductDO::getErrorHandle,PurchasePackageProductErrorHandleTypeEnum.VOID_PROCESSING.getType())
                            .set(LogisticsPurchasePackageErrorProductDO::getUpdateBy, StringUtils.isNotBlank(UserContext.getCurrentUserName()) ? UserContext.getCurrentUserName() : Constant.SYSTEM_ROOT)
                            .eq(LogisticsPurchasePackageErrorProductDO::getId, predictionDetail.getId());
                    int updateError = logisticsPurchasePackageErrorProductMapper.update(null, updateWrapper);
                    if (updateError > 0) {
                        LogisticsPurchasePackageErrorProductDO tempLogisticsPurchasePackageProductDO = new LogisticsPurchasePackageErrorProductDO();
                        tempLogisticsPurchasePackageProductDO.setPurchaseId(predictionDetail.getSalesOrderId());
                        tempLogisticsPurchasePackageProductDO.setStatus(DepositoryPurchaseStatusEnum.HANDLEDEXCEPTION.getStatusCode());
                        casecadeUpdateDepositoryStatus(tempLogisticsPurchasePackageProductDO);
                        LogisticsPurchasePackageErrorProductDO logisticsPurchasePackageErrorProductDO = new LogisticsPurchasePackageErrorProductDO();
                        logisticsPurchasePackageErrorProductDO.setPurchasePackageId(predictionDetail.getPurchasePackageId());
                        updatePurchasePackageLabel(logisticsPurchasePackageErrorProductDO);
                    }
                    continue;
                }
                isUpdatePredictionStatus = !DepositoryPurchaseStatusEnum.WAITINGWAREHOUSING.getStatusCode().equals(predictionDetail.getStatus()) ? false : true;
                LambdaUpdateWrapper<LogisticsPurchasePackageErrorProductDO> updateWrapper = Wrappers.<LogisticsPurchasePackageErrorProductDO>lambdaUpdate()
                        .set(LogisticsPurchasePackageErrorProductDO::getIsDeleted, Constant.YES)
                        .set(LogisticsPurchasePackageErrorProductDO::getUpdateBy, StringUtils.isNotBlank(UserContext.getCurrentUserName()) ? UserContext.getCurrentUserName() : Constant.SYSTEM_ROOT)
                        .eq(LogisticsPurchasePackageErrorProductDO::getId, predictionDetail.getId());
                update = logisticsPurchasePackageErrorProductMapper.update(null, updateWrapper);
                // ????????????????????????
                if (!isUpdatePredictionStatus) {
                    LogisticsPurchasePackageErrorProductDO logisticsPurchasePackageErrorProductDO = new LogisticsPurchasePackageErrorProductDO();
                    logisticsPurchasePackageErrorProductDO.setPurchasePackageId(predictionDetail.getPurchasePackageId());
                    updatePurchasePackageLabel(logisticsPurchasePackageErrorProductDO);
                }
                if (update > 0 && isUpdatePredictionStatus) {
                    // ?????????????????????
                    LambdaUpdateWrapper<LogisticsPurchasePackageDO> updateWrapperForPackage = Wrappers.<LogisticsPurchasePackageDO>lambdaUpdate()
                            .set(LogisticsPurchasePackageDO::getPrediction, PurchasePackagePredictionTypeEnum.NO.getType())
                            .set(LogisticsPurchasePackageDO::getUpdateBy,StringUtils.isNotBlank(UserContext.getCurrentUserName()) ? UserContext.getCurrentUserName() : Constant.SYSTEM_ROOT)
                            .eq(LogisticsPurchasePackageDO::getId, predictionDetail.getPurchasePackageId());
                    int updateForPacakge = logisticsPurchasePackageMapper.update(null, updateWrapperForPackage);
                    if (updateForPacakge > 0) {
                        againPrediction.add(predictionDetail.getPurchasePackageId());
                    }
                }
            }
        }
        manualMapBySku.remove("newPredictionTORequiredDeleted");
        return againPrediction;
    }

    private void sendHandleExceptionMsgToMq(LogisticsPurchasePackageErrorProductDO logisticsPurchasePackageErrorProductDO) {
        DepositoryPurchaseStatusMsgDto statusMsgDto = new DepositoryPurchaseStatusMsgDto();
        statusMsgDto.setItemId(logisticsPurchasePackageErrorProductDO.getItemId());
        statusMsgDto.setPurchaseId(logisticsPurchasePackageErrorProductDO.getPurchaseId());
        statusMsgDto.setSalesOrderId(logisticsPurchasePackageErrorProductDO.getSalesOrderId());
        Integer status = PurchasePackageProductErrorHandleTypeEnum.DIRECT_WAREHOUSING.getType().equals(logisticsPurchasePackageErrorProductDO.getErrorHandle())
                ? DepositoryPurchaseStatusEnum.WAREHOUSING.getStatusCode() : DepositoryPurchaseStatusEnum.HANDLEDEXCEPTION.getStatusCode();
        sendMsgUtils.sendPurchaseIntoDepositoryMsg(Collections.singletonList(statusMsgDto), status);
    }

    private void casecadeUpdateDepositoryStatus(LogisticsPurchasePackageErrorProductDO tempLogisticsPurchasePackageProductDO) {
        LambdaQueryWrapper<DepositorySaleOrderDetailDO> queryWrapper = Wrappers.<DepositorySaleOrderDetailDO>lambdaQuery()
//                .eq(DepositorySaleOrderDetailDO::getSaleOrder, tempLogisticsPurchasePackageProductDO.getSalesOrderId())
                .eq(DepositorySaleOrderDetailDO::getPurchaseId, tempLogisticsPurchasePackageProductDO.getPurchaseId())
//                .eq(DepositorySaleOrderDetailDO::getItemId, tempLogisticsPurchasePackageProductDO.getItemId())
                .lt(DepositorySaleOrderDetailDO::getReceiveState, DepositoryPurchaseStatusEnum.WAREHOUSING.getStatusCode());
        DepositorySaleOrderDetailDO orderDetailDO = depositorySaleOrderDetailMapper.selectOne(queryWrapper);
        if (orderDetailDO != null && orderDetailDO.getId() != null) {
            DepositorySaleOrderDetailDO depositorySaleOrderDetailDO = new DepositorySaleOrderDetailDO();
            depositorySaleOrderDetailDO.setId(orderDetailDO.getId());
            depositorySaleOrderDetailDO.setReceiveState(tempLogisticsPurchasePackageProductDO.getStatus());
            depositorySaleOrderDetailDO.setWeight(Objects.isNull(tempLogisticsPurchasePackageProductDO.getWeight()) ? new BigDecimal("1") : tempLogisticsPurchasePackageProductDO.getWeight());
            depositorySaleOrderDetailMapper.updateById(depositorySaleOrderDetailDO);
        }
    }

}
