package com.amg.fulfillment.cloud.logistics.mq.producer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.trade.param.AlibabaOpenplatformTradeModelTradeInfo;
import com.amg.framework.cloud.rocketmq.utils.RocketmqUtils;
import com.amg.fulfillment.cloud.order.api.dto.OpenPlatformDTO;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestProducer {
    @Autowired
    private RocketmqUtils rocketmqUtils;
    public static final String AFTERSALE_WORD_ORDER_COMMODITY_STATUS = "${mq.order.order-aliChannelOrderInfo-all.topic}";
    public static final String TOPIC_LOGISTICS_STATE_CHANGE_FOR_1688 = "LogisticsStateChangesFor1688Consumer";

    public void wordOrderStatusMessage() {
        String a = "{\"data\":{\"errorCode\":null,\"errorMessage\":null,\"result\":{\"baseInfo\":{\"alipayTradeId\":\"2021070622001875551416529551\",\"allDeliveredTime\":\"2021-07-06 17:16:56\",\"businessType\":\"cb\",\"buyerAlipayId\":null,\"buyerContact\":{\"companyName\":\"深圳云摩科技有限公司\",\"email\":null,\"fax\":null,\"imInPlatform\":\"云摩科技有限公司\",\"mobile\":\"18682350251\",\"name\":\"深圳云摩科技有限公司\",\"phone\":\"86-\"},\"buyerFeedback\":null,\"buyerID\":\"b2b-34053996683d849\",\"buyerLevel\":null,\"buyerLoginId\":\"云摩科技有限公司\",\"buyerMemo\":null,\"buyerRemarkIcon\":null,\"buyerUserId\":null,\"closeOperateType\":\"\",\"closeReason\":null,\"closeRemark\":null,\"completeTime\":null,\"confirmedTime\":null,\"couponFee\":0,\"createTime\":\"2021-07-06 14:53:35\",\"currency\":null,\"discount\":\"-175\",\"flowTemplateCode\":\"assureTrade\",\"id\":\"1345274400972396896\",\"idOfStr\":\"1345274400972396896\",\"modifyTime\":\"2021-07-06 17:16:56\",\"newStepOrderList\":null,\"overSeaOrder\":false,\"payChannelList\":[\"支付宝\"],\"payTime\":\"2021-07-06 15:45:35\",\"preOrderId\":null,\"receiverInfo\":{\"toArea\":\"广东省 东莞市 广东省东莞市清溪镇九乡村东风路182号(万邦速达清溪仓)\",\"toDivisionCode\":\"441900\",\"toFullName\":\"钱志仓\",\"toMobile\":\"18148516712\",\"toPhone\":\"\",\"toPost\":\"523646\",\"toTownCode\":null},\"receivingTime\":null,\"refund\":0,\"refundId\":null,\"refundPayment\":\"0\",\"refundStatus\":null,\"refundStatusForAs\":null,\"remark\":null,\"sellerAlipayId\":null,\"sellerContact\":{\"companyName\":\"艾特尔（广州）贸易有限公司\",\"email\":null,\"fax\":null,\"imInPlatform\":\"颜曦外贸批发\",\"mobile\":\"18620120658\",\"name\":\"陈芳\",\"phone\":\"86-86-18620120658\",\"wgSenderName\":null,\"wgSenderPhone\":null},\"sellerCreditLevel\":null,\"sellerID\":\"b2b-4225457665526c0\",\"sellerLoginId\":\"颜曦外贸批发\",\"sellerMemo\":null,\"sellerOrder\":false,\"sellerRemarkIcon\":null,\"sellerUserId\":null,\"shippingFee\":7,\"status\":\"waitbuyerreceive\",\"stepAgreementPath\":null,\"stepOrderList\":null,\"stepPayAll\":false,\"subBuyerLoginId\":null,\"sumProductPayment\":49.9,\"totalAmount\":55,\"tradeType\":\"50060\",\"tradeTypeCode\":\"fxassure\",\"tradeTypeDesc\":\"担保交易\"},\"customs\":null,\"extAttributes\":[],\"guaranteesTerms\":{\"assuranceInfo\":\"卖家在承诺买家保障服务的基础上，自愿选择向买家提供“72小时发货”的服务。当买家通过支付宝担保交易购买支持“72小时发货”服务的商品，卖家承诺在买家支付成功次日零时起72小时内发货。如卖家未履行前述承诺，买家可在指定期限内发起维权，并申请赔付。\",\"assuranceType\":\"qsexsfh\",\"qualityAssuranceType\":\"72小时发货\"},\"internationalLogistics\":null,\"nativeLogistics\":{\"address\":\"广东省东莞市清溪镇九乡村东风路182号(万邦速达清溪仓)\",\"area\":null,\"areaCode\":\"441900\",\"city\":\"东莞市\",\"contactPerson\":\"钱志仓\",\"fax\":null,\"logisticsItems\":[{\"arriveTime\":null,\"carriage\":null,\"deliveredTime\":\"2021-07-06 17:16:56\",\"fromAddress\":\"龙吟大街2巷1号\",\"fromArea\":\"海珠区\",\"fromCity\":\"广州市\",\"fromMobile\":\"18620120658\",\"fromPhone\":\"86-86-18620120658\",\"fromPost\":\"000000\",\"fromProvince\":null,\"gmtCreate\":\"2021-07-06 17:16:55\",\"gmtModified\":\"2021-07-06 17:16:55\",\"id\":\"147037641861537\",\"isTimePromise\":null,\"logisticsBillNo\":\"75484863576596\",\"logisticsCode\":\"LP00456080652240\",\"logisticsCompanyId\":\"3\",\"logisticsCompanyName\":\"中通快递(ZTO)\",\"logisticsCompanyNo\":\"ZTO\",\"noLogisticsBillNo\":\"\",\"noLogisticsCondition\":\"-1\",\"noLogisticsName\":\"\",\"noLogisticsTel\":\"\",\"status\":\"alreadysend\",\"subItemIds\":\"1345274400972396896\",\"toAddress\":\"广东省东莞市清溪镇九乡村东风路182号(万邦速达清溪仓)\",\"toArea\":null,\"toCity\":\"东莞市\",\"toMobile\":null,\"toPhone\":null,\"toPost\":\"523646\",\"toProvince\":null,\"type\":\"0\"}],\"mobile\":\"18148516712\",\"province\":\"广东省\",\"telephone\":\"\",\"town\":null,\"townCode\":null,\"zip\":\"523646\"},\"orderBizInfo\":{\"accountPeriodTime\":null,\"creditOrder\":false,\"creditOrderDetail\":null,\"lstOrderInfo\":null,\"odsCyd\":false,\"preOrderInfo\":null},\"orderInvoiceInfo\":null,\"orderRateInfo\":{\"buyerRateList\":null,\"buyerRateStatus\":5,\"sellerRateList\":null,\"sellerRateStatus\":5},\"overseasExtraAddress\":null,\"productItems\":[{\"canSendGoods\":null,\"cantSendReason\":null,\"cargoNumber\":\"D048-藏蓝色-L\",\"closeReason\":null,\"description\":null,\"entryDiscount\":\"-175\",\"erpMaterialCode\":null,\"gmtCompleted\":null,\"gmtCreate\":\"2021-07-06 14:53:35\",\"gmtModified\":\"2021-07-06 17:16:56\",\"gmtPayExpireTime\":null,\"guaranteesTerms\":[{\"assuranceInfo\":\"卖家在承诺买家保障服务的基础上，自愿选择向买家提供“72小时发货”的服务。当买家通过支付宝担保交易购买支持“72小时发货”服务的商品，卖家承诺在买家支付成功次日零时起72小时内发货。如卖家未履行前述承诺，买家可在指定期限内发起维权，并申请赔付。\",\"assuranceType\":\"qsexsfh\",\"qualityAssuranceType\":\"72小时发货\"},{\"assuranceInfo\":\"卖家在承诺买家保障服务的基础上，自愿选择向买家提供“破损包赔”服务。在买家使用在线方式订购卖家出售的产品后，如产品存在破损，卖家承诺在保障时效内免费向买家补寄一次，如补寄后买家依旧无法获得完好的产品，卖家需向买家提供破损产品的退货退款服务。\",\"assuranceType\":\"psbj\",\"qualityAssuranceType\":\"破损包赔\"},{\"assuranceInfo\":\"卖家在承诺买家保障服务的基础上，自愿选择向买家提供“7天包换”服务。在买家使用在线方式订购支持“7天包换 ”服务的商品后，卖家承诺在买家签收货物后的7天内，对无人为损坏的商品提供更换服务。如卖家未履行前述承诺，买家可在指定期限内发起维权，并申请赔付。\",\"assuranceType\":\"qtbh\",\"qualityAssuranceType\":\"7天包换\"},{\"assuranceInfo\":\"阿里巴巴向采购等级为L5-L6级且符合一定信用等级，获得极速退款额度的买家提供极速退款服务，当订单支付成功后48小时内且卖家未发货时，买家发起支付宝担保交易订单退款，且退款订单符合极速退款服务所设定的退款条件，卖家需在极速退款服务约定的时间内进行处理的服务，如卖家未在约定的时间内进行处理，卖家授权平台根据买家申请的退款诉求自动达成退款。当订单支付成功后48小时内且卖家未发货时，买家在9点至18点发起的极速退款流程，卖家需在2小时内响应。当订单支付成功后48小时内且卖家未发货时，买家在18点后至次日9点前发起的极速退款流程，卖家需在次日12点前响应。\",\"assuranceType\":\"jstk\",\"qualityAssuranceType\":\"极速退款\"},{\"assuranceInfo\":\"如卖家未能在指定期间内提供相关证据材料，或相关证据材料明显无效，阿里巴巴判定买家赔付申请成立，卖家同意按照本协议之约定向买家赔偿其受到的损失，赔偿金额为买家已实际支付的短少产品价款（仅为配件短少时亦应就整套产品价款进行赔偿）。此等情形下，阿里巴巴有权动用卖家的保证金或通知支付宝公司自卖家的支付宝账户直接扣除相应金额款项先行赔付给买家。\",\"assuranceType\":\"shbp\",\"qualityAssuranceType\":\"少货必赔\"}],\"itemAmount\":48,\"itemDiscountFee\":null,\"logisticsStatus\":2,\"name\":\"欧美跨境货源时尚女裙V领拼接波点网纱开叉外贸衣裙 非洲大码女装\",\"price\":49.9,\"productCargoNumber\":\"D048\",\"productID\":\"610318020832\",\"productImgUrl\":[\"http://cbu01.alicdn.com/img/ibank/O1CN01aOUsu21Bs2eX0262c_!!0-0-cib.80x80.jpg\",\"http://cbu01.alicdn.com/img/ibank/O1CN01aOUsu21Bs2eX0262c_!!0-0-cib.jpg\"],\"productSnapshotUrl\":\"https://trade.1688.com/order/offer_snapshot.htm?order_entry_id=1345274400972396896\",\"quantity\":1,\"quantityFactor\":1,\"refund\":0,\"refundId\":null,\"refundIdForAs\":null,\"refundStatus\":null,\"skuID\":\"4298629589256\",\"skuInfos\":[{\"name\":\"颜色\",\"value\":\"藏蓝色\"},{\"name\":\"尺码\",\"value\":\"L\"}],\"sort\":null,\"specId\":\"de54dfa030147a6cb3040bcb2d1e5334\",\"status\":\"waitbuyerreceive\",\"statusStr\":\"等待买家收货\",\"subItemID\":\"1345274400972396896\",\"subItemIDString\":\"1345274400972396896\",\"type\":\"common\",\"unit\":\"件\",\"weight\":null,\"weightUnit\":null}],\"quoteList\":null,\"tradeTerms\":[{\"cardPay\":false,\"expressPay\":false,\"payStatus\":\"2\",\"payTime\":\"2021-07-06 15:45:35\",\"payWay\":\"13\",\"phasAmount\":55,\"phase\":\"23542248949396896\",\"phaseCondition\":null,\"phaseDate\":null}]},\"subCode\":null,\"success\":true},\"errorCode\":\"100200\",\"msg\":\"请求成功\",\"requestId\":\"a442f858acec46f9b123d766f99f4043\",\"success\":true}";
        JSONObject jsonObject = JSON.parseObject(a);
        JSONObject data = jsonObject.getJSONObject("data");
        OpenPlatformDTO<AlibabaOpenplatformTradeModelTradeInfo> info = data.toJavaObject(OpenPlatformDTO.class);
        JSONObject result = data.getJSONObject("result");
        AlibabaOpenplatformTradeModelTradeInfo alibabaOpenplatformTradeModelTradeInfo = result.toJavaObject(AlibabaOpenplatformTradeModelTradeInfo.class);
        info.setResult(alibabaOpenplatformTradeModelTradeInfo);
        //同步发送  "order-aliChannelOrderInfo-all"
        rocketmqUtils.syncSend(AFTERSALE_WORD_ORDER_COMMODITY_STATUS, info);
        //异步发送消息
        //  rocketmqUtils.asyncSend(TopicConstant.AFTERSALE_WORD_ORDER_COMMODITY_STATUS, workOrderItemStatusDO, null);
    }

    public void LogisticsStateChangesFor1688Message(String topic,String message) {
        //同步发送  "order-aliChannelOrderInfo-all"
        System.out.println(11);
        SendResult sendResult = rocketmqUtils.syncSend(topic, message);
        if (sendResult!=null){
            boolean equals = SendStatus.SEND_OK.equals(sendResult.getSendStatus());
            System.out.println(equals);
        }
        System.out.println(11);
        //异步发送消息
        //  rocketmqUtils.asyncSend(TopicConstant.AFTERSALE_WORD_ORDER_COMMODITY_STATUS, workOrderItemStatusDO, null);
    }


}


