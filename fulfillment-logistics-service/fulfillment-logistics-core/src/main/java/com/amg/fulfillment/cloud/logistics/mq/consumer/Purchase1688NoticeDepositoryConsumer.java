package com.amg.fulfillment.cloud.logistics.mq.consumer;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.trade.param.AlibabaOpenplatformTradeModelNativeLogisticsInfo;
import com.alibaba.trade.param.AlibabaOpenplatformTradeModelNativeLogisticsItemsInfo;
import com.alibaba.trade.param.AlibabaOpenplatformTradeModelProductItemInfo;
import com.alibaba.trade.param.AlibabaOpenplatformTradeModelTradeInfo;
import com.amg.framework.boot.base.exception.GlobalException;
import com.amg.fulfillment.cloud.logistics.api.enumeration.DeliveryPackagePurchaseChannelEnum;
import com.amg.fulfillment.cloud.logistics.api.enumeration.PurchasePackagePredictionTypeEnum;
import com.amg.fulfillment.cloud.logistics.api.enumeration.PurchasePackageWarehousingTypeEnum;
import com.amg.fulfillment.cloud.logistics.api.util.LogisticsCommonUtils;
import com.amg.fulfillment.cloud.logistics.entity.DepositorySaleOrderDetailDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPurchasePackageDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPurchasePackageErrorProductDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPurchaseProductDO;
import com.amg.fulfillment.cloud.logistics.mapper.DepositorySaleOrderDetailMapper;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsPurchasePackageErrorProductMapper;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsPurchasePackageMapper;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsPurchaseProductMapper;
import com.amg.fulfillment.cloud.logistics.model.bo.LogisticsPurchasePackageBO;
import com.amg.fulfillment.cloud.logistics.model.req.PurchasePackagePredictionReq;
import com.amg.fulfillment.cloud.logistics.service.IPurchasePackageService;
import com.amg.fulfillment.cloud.logistics.service.impl.PurchasePackageServiceImpl;
import com.amg.fulfillment.cloud.logistics.util.GrpcJsonUtil;
import com.amg.fulfillment.cloud.order.api.client.ChannelPurchaseProductClient;
import com.amg.fulfillment.cloud.order.api.enums.OrderApiResponseCodeEnum;
import com.amg.fulfillment.cloud.order.api.proto.ChannelPurchaseProductGTO;
import com.amg.fulfillment.cloud.order.api.util.AlibabaOpenUtils;
import com.amg.fulfillment.cloud.purchase.api.enums.ChannelEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Tom
 * @date 2021-04-12-16:22
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "${mq.order.order-aliChannelOrderInfo-all.topic}",
        selectorExpression = "*", consumeThreadMax = 5)
public class Purchase1688NoticeDepositoryConsumer implements RocketMQListener<String> {

    @Autowired
    AlibabaOpenUtils alibabaOpenUtils;
    @Autowired
    private LogisticsPurchasePackageMapper logisticsPurchasePackageMapper;
    @Autowired
    private LogisticsPurchasePackageErrorProductMapper logisticsPurchasePackageErrorProductMapper;
    @Autowired
    private IPurchasePackageService purchasePackageService;
    @Autowired
    private ChannelPurchaseProductClient channelPurchaseProductClient;
    @Autowired
    private DepositorySaleOrderDetailMapper depositorySaleOrderDetailMapper;
    @Autowired
    private LogisticsPurchaseProductMapper logisticsPurchaseProductMapper;
    @Autowired
    private PurchasePackageServiceImpl purchasePackageServiceImpl;
    @Autowired
    private LogisticsCommonUtils logisticsCommonUtils;

    private static final String SUCCESS_EXCHANGE = "????????????";
    private static final String ORIGON_PATTERN = "\\d+";
    private static Pattern pattern = Pattern.compile(ORIGON_PATTERN);

    private static final String LOGISTICSWAYBILLRULE="\\w+";

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void onMessage(String message) {
        if (message != null) {
            log.info("?????????1688???????????????{}", message);
            AlibabaOpenplatformTradeModelTradeInfo info = JSON.parseObject(message,AlibabaOpenplatformTradeModelTradeInfo.class);
            AlibabaOpenplatformTradeModelNativeLogisticsInfo logistics = info.getNativeLogistics();
            if (Objects.isNull(logistics)) {
                log.debug("?????????1688??????????????????????????????null");
                return;
            }
            AlibabaOpenplatformTradeModelNativeLogisticsItemsInfo[] logisticsItems = logistics.getLogisticsItems();
            if (Objects.isNull(logisticsItems) || logisticsItems.length < 1) {
                log.debug("?????????1688??????????????????????????????null");
                return;
            }
            purchase(info);
        } else {
            log.info("?????????1688???????????????{}", "????????????null??????");
        }
    }



    /**
     * https://open.1688.com/doc/topicDetail.htm?spm=a260s.11630592.0.0.555655edQMhFRp&topicGroup=ORDER&id=ORDER_BUYER_VIEW_ANNOUNCE_SENDGOODS
     * {
     * "orderId": 167539019420540000,
     * "currentStatus": "waitbuyerreceive",
     * "msgSendTime": "2018-05-30 19: 34: 27",
     * "buyerMemberId": "b2b-665170100",
     * "sellerMemberId": "b2b-1676547900b7bb3"
     * }
     */


    public void purchase(AlibabaOpenplatformTradeModelTradeInfo info) {
        // ??????orderId???1688????????????????????????
//        AlibabaTradeGetBuyerViewParam param = new AlibabaTradeGetBuyerViewParam();
//        param.setOrderId(Long.parseLong(orderId));
//        param.setWebSite(Constant.WEBSITE);
//        OpenPlatformDTO<AlibabaOpenplatformTradeModelTradeInfo> info = alibabaOpenUtils.getOrderInfo(param);
        log.info("??????????????????-??????1688??????????????????????????????{}", JSON.toJSONString(info));
        Long orderId = info.getBaseInfo().getId();
        String orderIDString = null;
        if (orderId != null) {
            orderIDString = String.valueOf(orderId);
        }
//        if (!info.getSuccess()) {
////            log.error("??????????????????-??????1688???????????????????????????????????????????????????{}", JSON.toJSONString(info));
//            log.error("??????????????????-??????1688????????????????????????????????????????????????{}", JSON.toJSONString(info));
//            throw new GlobalException(OrderApiResponseCodeEnum.RETURN_CODE_200202, "??????????????????-??????1688?????????????????????????????????");
//        }
//        AlibabaOpenplatformTradeModelTradeInfo tradeModel = info.getResult();
        // ????????????????????????
        List<LogisticsPurchasePackageBO> purchasePackageList = insertLocalDatebase(orderIDString, info);
        // ?????????????????????
        List<Long> list = translate1688SkuToLocate(orderIDString, purchasePackageList);
        // ??????
        if (!list.isEmpty()) {
            try {
                PurchasePackagePredictionReq predictionReq = new PurchasePackagePredictionReq();
                predictionReq.setIdList(list);
                // ????????????
                purchasePackageService.prediction(predictionReq);
            } catch (Exception e) {
                log.error("?????????????????????????????????{}", e);
            }
        }
    }


    // expressBillNo ????????????????????????????????????????????????????????????????????????????????????????????????????????????expressBillNo???null???
    private LogisticsPurchasePackageDO existPackage(String companyName, String expressBillNo,String orderId) {
        if (StringUtils.isNotBlank(expressBillNo)) {
            LambdaQueryWrapper<LogisticsPurchasePackageDO> queryWrapper = Wrappers.<LogisticsPurchasePackageDO>lambdaQuery()
//                    .eq(StringUtils.isNotBlank(companyName),LogisticsPurchasePackageDO::getExpressCompanyName, companyName)
                    .eq(LogisticsPurchasePackageDO::getExpressBillNo, expressBillNo);
            LogisticsPurchasePackageDO logisticsPurchasePackageDO = logisticsPurchasePackageMapper.selectOne(queryWrapper);
            if (logisticsPurchasePackageDO != null && logisticsPurchasePackageDO.getId() !=null) {
                return logisticsPurchasePackageDO;
            }
        }else {
            LambdaQueryWrapper<LogisticsPurchasePackageDO> queryWrapper = Wrappers.<LogisticsPurchasePackageDO>lambdaQuery()
                    .eq(LogisticsPurchasePackageDO::getChannelOrderId, orderId);
            List<LogisticsPurchasePackageDO> logisticsPurchasePackageDOs = logisticsPurchasePackageMapper.selectList(queryWrapper);
            if (!logisticsPurchasePackageDOs.isEmpty()) {
                return logisticsPurchasePackageDOs.stream().filter(item -> StringUtils.isBlank(item.getExpressBillNo())).findFirst().orElseGet(() -> null);
            }
        }
        return null;
    }


    private List<LogisticsPurchasePackageBO> insertLocalDatebase(String orderId, AlibabaOpenplatformTradeModelTradeInfo tradeModel) {
        // ??????????????????????????????????????????????????????
        AlibabaOpenplatformTradeModelNativeLogisticsItemsInfo[] logisticsItems = tradeModel.getNativeLogistics().getLogisticsItems();
        // ??????????????????????????????
        AlibabaOpenplatformTradeModelProductItemInfo[] productItems = tradeModel.getProductItems();
        List<LogisticsPurchasePackageBO> packageBOList = new ArrayList<>(); // ???????????????????????????
        if (Objects.isNull(logisticsItems) || logisticsItems.length < 1) {
            log.error("?????????????????????{}?????????1688???????????????????????????null", orderId);
            throw new GlobalException(OrderApiResponseCodeEnum.RETURN_CODE_200202.getCode(), "?????????????????????" + orderId + "?????????1688???????????????????????????null");
        }
        for (AlibabaOpenplatformTradeModelNativeLogisticsItemsInfo item : logisticsItems) {
            // ????????????
            String logisticsBillNo = StringUtils.isNotBlank(item.getLogisticsBillNo()) ? (item.getLogisticsBillNo().matches(LOGISTICSWAYBILLRULE) ? item.getLogisticsBillNo() : item.getNoLogisticsBillNo()) : item.getNoLogisticsBillNo();
            LogisticsPurchasePackageDO existPackage = existPackage(item.getLogisticsCompanyName(), logisticsBillNo, orderId);
            if (!Objects.isNull(existPackage)) {
                LogisticsPurchasePackageBO logisticsPurchasePackageBO = queryChannelSkuUnbindPurchaseId(existPackage);
                if (logisticsPurchasePackageBO != null && existPackage.getPrediction() == PurchasePackagePredictionTypeEnum.NO.getType()) {
                    packageBOList.add(logisticsPurchasePackageBO);
                }
                continue;
            }
            LogisticsPurchasePackageBO packageBO = new LogisticsPurchasePackageBO();
            // ???????????????
            LogisticsPurchasePackageDO packageDO = new LogisticsPurchasePackageDO();
            packageDO.setPackageNo(logisticsCommonUtils.generateDepositoryPredictionPackageNo());
            packageDO.setExpressCompanyName(StringUtils.isNotBlank(item.getLogisticsCompanyName()) ? item.getLogisticsCompanyName() : "??????");
            packageDO.setExpressCompanyCode(item.getLogisticsCompanyNo());
            String realBillNo = StringUtils.isNotBlank(item.getLogisticsBillNo()) ? (item.getLogisticsBillNo().matches(LOGISTICSWAYBILLRULE) ? item.getLogisticsBillNo() : item.getNoLogisticsBillNo()) : item.getNoLogisticsBillNo();
            packageDO.setExpressBillNo(realBillNo);
            packageDO.setChannelOrderId(orderId);
            packageDO.setStatus(item.getStatus());
            packageDO.setPrediction(PurchasePackagePredictionTypeEnum.NO.getType());
            packageDO.setWarehousing(PurchasePackageWarehousingTypeEnum.NO.getType());
            packageDO.setExpressCode(item.getLogisticsCode());
            packageDO.setShipFromProvince(item.getFromProvince());
            packageDO.setExpectedArrive(item.getArriveTime());
            packageDO.setDeliverGoodTime(item.getDeliveredTime());
            packageDO.setPackageSourceType(DeliveryPackagePurchaseChannelEnum.ALIBABA.getType());
            logisticsPurchasePackageMapper.insert(packageDO);
            packageBO.setId(packageDO.getId());
            packageBOList.add(packageBO);
            // ??????????????????????????????????????????
            List<String> subItemIds = Arrays.asList(item.getSubItemIds().split(","));
            Arrays.stream(productItems).filter(v -> subItemIds.contains(v.getSubItemIDString()))
                    .forEach(v -> {
                        LogisticsPurchaseProductDO logisticsPurchaseProduct = new LogisticsPurchaseProductDO();
                        logisticsPurchaseProduct.setProductName(v.getName());
                        logisticsPurchaseProduct.setPackageId(packageDO.getId());
                        logisticsPurchaseProduct.setSkuChannel(Objects.isNull(v.getSkuID()) ? String.valueOf(v.getProductID()) : String.valueOf(v.getSkuID()));
                        logisticsPurchaseProduct.setProductImg(Objects.isNull(v.getProductImgUrl()) ? null : JSON.toJSONString(v.getProductImgUrl()));
                        logisticsPurchaseProduct.setProductAttribute(Objects.isNull(v.getSkuInfos()) ? null : JSON.toJSONString(v.getSkuInfos()));
                        logisticsPurchaseProduct.setProductCount(v.getQuantity().intValue());
                        logisticsPurchaseProduct.setProductWeight(StringUtils.isBlank(v.getWeight()) ? null : new BigDecimal(v.getWeight()));
                        logisticsPurchaseProductMapper.insert(logisticsPurchaseProduct);
                        LogisticsPurchasePackageBO.LogisticsProductBO logisticsProductBO = new LogisticsPurchasePackageBO.LogisticsProductBO();
                        logisticsProductBO.setId(logisticsPurchaseProduct.getId());
                        logisticsProductBO.setPackageId(packageBO.getId());
                        logisticsProductBO.setSkuChannel(logisticsPurchaseProduct.getSkuChannel());
                        logisticsProductBO.setProductCount(logisticsPurchaseProduct.getProductCount());
                        logisticsProductBO.setProductStatus((v.getStatusStr()));
                        packageBO.getLogisticsProductList().add(logisticsProductBO);
                        for (Integer i = 0; i < logisticsPurchaseProduct.getProductCount(); i++) {
                            LogisticsPurchasePackageErrorProductDO errorProductDO = new LogisticsPurchasePackageErrorProductDO();
                            errorProductDO.setProductId(logisticsPurchaseProduct.getId());
                            errorProductDO.setPurchasePackageId(packageDO.getId());
                            errorProductDO.setChannleSku(StringUtils.substring(logisticsPurchaseProduct.getSkuChannel(),0,90));
                            logisticsPurchasePackageErrorProductMapper.insert(errorProductDO);
                            LogisticsPurchasePackageBO.LogisticsPurchasePackageErrorProductBO errorProductBO = new LogisticsPurchasePackageBO.LogisticsPurchasePackageErrorProductBO();
                            errorProductBO.setId(errorProductDO.getId());
                            errorProductBO.setProductId(logisticsProductBO.getId());
                            errorProductBO.setPackageId(packageDO.getId());
                            errorProductBO.setPurchaseId(errorProductDO.getPurchaseId());
                            errorProductBO.setSaleOrderId(errorProductDO.getSalesOrderId());
                            logisticsProductBO.getLogisticsPurchasePackageErrorProductList().add(errorProductBO);
                        }
                    });
        }
        return packageBOList;
    }

    /**
     * @param orderId             1688?????????
     * @param purchasePackageList ??????
     *                            ?????????????????????id??????
     */
    public List<Long> translate1688SkuToLocate(String orderId, List<LogisticsPurchasePackageBO> purchasePackageList) {
        List<Long> purchasePackageIdList = new ArrayList();
        try {
            // gprc????????????id
            ChannelPurchaseProductGTO.ChannelPurchaseProductRequest channelReq = ChannelPurchaseProductGTO.ChannelPurchaseProductRequest.newBuilder()
                    .setOrderChannel(ChannelEnum.CHANNEL_1688.getCode())
                    .setChannelOrderId(orderId)
                    .build();
            ChannelPurchaseProductGTO.ChannelPurchaseProductResult channelPurchaseProductResult = channelPurchaseProductClient.getChannelPurchaseProduct(channelReq);
            log.info("?????????????????????????????????GRPC????????????id?????????????????????{}???????????????{}", orderId, GrpcJsonUtil.jsonFormat.printToString(channelPurchaseProductResult));
            if (!channelPurchaseProductResult.getSuccess()) {
                log.error("?????????????????????????????????GRPC????????????id?????????????????????{}???????????????{}", orderId, GrpcJsonUtil.jsonFormat.printToString(channelPurchaseProductResult));
                return purchasePackageIdList;
            }
            List<ChannelPurchaseProductGTO.ChannelPurchaseProductResponse> dataList = channelPurchaseProductResult.getDataList();
            // ????????????
            ArrayList<ChannelOrderDetail> channelOrderDetails = new ArrayList<>();
            for (ChannelPurchaseProductGTO.ChannelPurchaseProductResponse response : dataList) {
                ChannelOrderDetail channelOrderDetail = new ChannelOrderDetail();
                channelOrderDetail.setSku(response.getSku());
                // ????????????channel_sku,?????????????????????
                if (StrUtil.isBlank(response.getChannelSku()) && StrUtil.isNotBlank(response.getOriginalSkuCode())) {
                    Matcher matcher = pattern.matcher(response.getOriginalSkuCode());
                    matcher.find();
                    channelOrderDetail.setSkuChannel(matcher.group());
                } else {
                    channelOrderDetail.setSkuChannel(response.getChannelSku());
                }
                channelOrderDetail.setSpu(response.getSpu());
                channelOrderDetail.setItemId(response.getItemId());
                channelOrderDetail.setPurchaseId(response.getPurchaseId());
                channelOrderDetail.setSalesOrderId(response.getSaleOrderId());
                channelOrderDetail.setChannelType(response.getSkuChannel());
                channelOrderDetail.setBindStatus(response.getBindStatus());
                channelOrderDetail.setOriginalSkuCode(response.getOriginalSkuCode());
                channelOrderDetails.add(channelOrderDetail);
            }
            // ??????1688sku??????????????????????????????
            ArrayList<Long> failChannelSkuToSkuIds = new ArrayList<>();
            for (LogisticsPurchasePackageBO packageBO : purchasePackageList) {
                packageBO.getLogisticsProductList().stream().forEach(product -> {
                    List<ChannelOrderDetail> detailOptionalList = channelOrderDetails.stream().filter(channel -> StrUtil.isNotBlank(channel.getSkuChannel()) && channel.getSkuChannel().equals(product.getSkuChannel()) /*&& SUCCESS_EXCHANGE.equals(product.getProductStatus())*/)
                            .collect(Collectors.toList());
                    if (!Objects.isNull(detailOptionalList) && detailOptionalList.size() > 0) {
                        LogisticsPurchaseProductDO updateProduct = new LogisticsPurchaseProductDO();
                        updateProduct.setId(product.getId());
                        updateProduct.setSku(detailOptionalList.get(0).getSku());
                        updateProduct.setSpu(detailOptionalList.get(0).getSpu());
                        updateProduct.setSpuChannel(detailOptionalList.get(0).getSpuChannel());
                        logisticsPurchaseProductMapper.updateById(updateProduct);
                        List<LogisticsPurchasePackageBO.LogisticsPurchasePackageErrorProductBO> errorProductDOList = product.getLogisticsPurchasePackageErrorProductList();
                        for (int i = 0, j = 0; i < detailOptionalList.size() && j < errorProductDOList.size(); i++, j++) {
                            LogisticsPurchasePackageErrorProductDO updateErrorProduct = new LogisticsPurchasePackageErrorProductDO();
                            updateErrorProduct.setId(errorProductDOList.get(j).getId());
                            updateErrorProduct.setItemId(detailOptionalList.get(i).getItemId());
                            updateErrorProduct.setPurchaseId(detailOptionalList.get(i).getPurchaseId());
                            updateErrorProduct.setSalesOrderId(detailOptionalList.get(i).getSalesOrderId());
                            updateErrorProduct.setSku(detailOptionalList.get(i).getSku());
                            logisticsPurchasePackageErrorProductMapper.updateById(updateErrorProduct);
                            updateErrorProduct.setChannleSku(detailOptionalList.get(0).getSkuChannel());
                            updateErrorProduct.setPurchasePackageId(packageBO.getId());
                            casecadeUpdateDepositorySaleOderProperty(updateErrorProduct);
                        }
                        // ??????????????????
                        purchasePackageIdList.add(packageBO.getId());
                    } else {
                        failChannelSkuToSkuIds.add(packageBO.getId());
                    }
                });
            }
            if (!failChannelSkuToSkuIds.isEmpty()) {
                updateFailReasonForPurchasePackageTable(failChannelSkuToSkuIds, "1688channelSku??????????????????sku???????????????????????????id");
            }
        } catch (Exception e) {
            log.error("?????????1688????????????,????????????GRPC???sku???????????????{}", e);
            List<Long> errorIds = purchasePackageList.stream().map(LogisticsPurchasePackageBO::getId).collect(Collectors.toList());
            updateFailReasonForPurchasePackageTable(errorIds, "????????????GRPC???sku????????????: " + org.apache.commons.lang3.StringUtils.substring(e.getMessage(), 0, 140));
        }
        return purchasePackageIdList;
    }

    private void updateFailReasonForPurchasePackageTable(List<Long> purchasePackageIds, String failMessage) {
        if (purchasePackageIds.isEmpty()) {
            return;
        }
        LambdaUpdateWrapper<LogisticsPurchasePackageDO> updateWrapper = Wrappers.<LogisticsPurchasePackageDO>lambdaUpdate()
                .set(LogisticsPurchasePackageDO::getPredictionFailReason, StringUtils.isBlank(failMessage) ? "---error---" : failMessage)
                .in(LogisticsPurchasePackageDO::getId, purchasePackageIds);
        logisticsPurchasePackageMapper.update(null, updateWrapper);
    }

    // ?????? `t_depository_sale_order_detail` ???`t_depository_sale_order_detail` ?????????
    private void casecadeUpdateDepositorySaleOderProperty(LogisticsPurchasePackageErrorProductDO updateErrorProduct) {
        LambdaQueryWrapper<DepositorySaleOrderDetailDO> queryWrapper = Wrappers.<DepositorySaleOrderDetailDO>lambdaQuery()
                .eq(DepositorySaleOrderDetailDO::getItemId, updateErrorProduct.getItemId());
        DepositorySaleOrderDetailDO saleOrderDetailDO = depositorySaleOrderDetailMapper.selectOne(queryWrapper);
        if (!Objects.isNull(saleOrderDetailDO)) {
            DepositorySaleOrderDetailDO depositorySaleOrderDetail = new DepositorySaleOrderDetailDO();
            depositorySaleOrderDetail.setId(saleOrderDetailDO.getId());
            depositorySaleOrderDetail.setReceivePurchaseId(updateErrorProduct.getPurchaseId());
            depositorySaleOrderDetail.setPurchasePackageId(updateErrorProduct.getPurchasePackageId());
            depositorySaleOrderDetail.setChannelSku(StringUtils.isNotBlank(updateErrorProduct.getChannleSku()) ? (updateErrorProduct.getChannleSku().length() > 90 ? updateErrorProduct.getChannleSku().substring(0, 90) : updateErrorProduct.getChannleSku()) : updateErrorProduct.getChannleSku());
            depositorySaleOrderDetailMapper.updateById(depositorySaleOrderDetail);
        }
    }

    // ????????????????????????channelSku--sku ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????sku????????????????????????????????????????????????????????????
    private LogisticsPurchasePackageBO queryChannelSkuUnbindPurchaseId(LogisticsPurchasePackageDO logisticsPurchasePackageDO) {
        LambdaQueryWrapper<LogisticsPurchasePackageErrorProductDO> queryWrapper = Wrappers.<LogisticsPurchasePackageErrorProductDO>lambdaQuery()
                .eq(LogisticsPurchasePackageErrorProductDO::getPurchasePackageId, logisticsPurchasePackageDO.getId());
        List<LogisticsPurchasePackageErrorProductDO> errorProductDOS = logisticsPurchasePackageErrorProductMapper.selectList(queryWrapper);
        long count = errorProductDOS.stream().filter(item -> StringUtils.isBlank(item.getPurchaseId())).count();
        if (count > 0) {
            LogisticsPurchasePackageBO purchasePackageBO = purchasePackageServiceImpl.translateLogisticPurchasePackageDoToLogisticsPurchasePackageBO(logisticsPurchasePackageDO, errorProductDOS);
            return purchasePackageBO;
        }
        return null;
    }


    @Data
    @ToString
    public static class ChannelOrderDetail {
        private String channelOrderId;
        private String sku;
        private String skuChannel;
        private String spu;
        private String spuChannel;
        private String originalSkuCode;
        private String itemId;
        private String purchaseId;
        private String salesOrderId;
        private boolean bindStatus;
        private int channelType;
        private Date updateTime;
        private Date createTime;
    }
}

