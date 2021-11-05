package com.amg.fulfillment.cloud.logistics.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amg.framework.boot.utils.spring.SpringContextUtil;
import com.amg.framework.cloud.grpc.utils.GrpcJsonFormatUtils;
import com.amg.fulfillment.cloud.basic.common.oss.aliyun.utils.OSSUtils;
import com.amg.fulfillment.cloud.logistics.api.client.InventoryStoreClient;
import com.amg.fulfillment.cloud.logistics.api.client.LogisticClient;
import com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb.PurchaseIntoDepositoryDetailForWanB;
import com.amg.fulfillment.cloud.logistics.api.dto.msg.DeliveryPackageMsgDto;
import com.amg.fulfillment.cloud.logistics.api.enumeration.DeliveryPackageTypeEnum;
import com.amg.fulfillment.cloud.logistics.api.proto.*;
import com.amg.fulfillment.cloud.logistics.api.util.WanBDepistoryUtil;
import com.amg.fulfillment.cloud.logistics.dto.logistic.*;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPackageDO;
import com.amg.fulfillment.cloud.logistics.entity.TestCurrentDO;
import com.amg.fulfillment.cloud.logistics.grpc.*;
import com.amg.fulfillment.cloud.logistics.job.DepositoryDeliveryResultRetryJob;
import com.amg.fulfillment.cloud.logistics.job.Purchase1688NoticeDepositoryRetryJob;
import com.amg.fulfillment.cloud.logistics.job.PurchaseIntoDepositoryRetryJob;
import com.amg.fulfillment.cloud.logistics.job.SaleOrderRetryJob;
import com.amg.fulfillment.cloud.logistics.manager.TestLogisticManagerImpl;
import com.amg.fulfillment.cloud.logistics.manager.WanBLogisticManagerImpl;
import com.amg.fulfillment.cloud.logistics.manager.YunTuLogisitcManagerImpl;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsPackageMapper;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsStatusMapper;
import com.amg.fulfillment.cloud.logistics.mapper.TestCurrentMapper;
import com.amg.fulfillment.cloud.logistics.model.req.*;
import com.amg.fulfillment.cloud.logistics.model.vo.PurchasePackageLogisticsInfoVO;
import com.amg.fulfillment.cloud.logistics.module.rule.LogisticMatchRuleHandler;
import com.amg.fulfillment.cloud.logistics.module.rule.LogisticRuleComposition;
import com.amg.fulfillment.cloud.logistics.mq.consumer.DepositoryIntoDepositoryMsgConsumer;
import com.amg.fulfillment.cloud.logistics.mq.consumer.Purchase1688NoticeDepositoryConsumer;
import com.amg.fulfillment.cloud.logistics.service.impl.DeliveryPackageServiceImpl;
import com.amg.fulfillment.cloud.logistics.service.impl.DeliveryProductServiceImpl;
import com.amg.fulfillment.cloud.logistics.service.impl.PurchasePackageServiceImpl;
import com.amg.fulfillment.cloud.logistics.util.GrpcJsonUtil;
import com.amg.fulfillment.cloud.order.api.client.MetadataClient;
import com.amg.fulfillment.cloud.order.api.proto.MetadatapbMetadata;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Tom
 * @date 2021-04-26-16:39
 */
@Slf4j
@RestController
@RequestMapping("client")
public class MockGrpcClientController {

    @Autowired
    InventoryStoreClient inventoryStoreClient;
    @Autowired
    MetadataClient metadataClient;


//    @PostMapping("/outDepository")
//    public void outDepository(String logisicCode) {
//        // 地址
//        AutoOutDepositoryGTO.AddressDto receiverAddress = AutoOutDepositoryGTO.AddressDto.newBuilder()
//                .setContacter("tom").setCountryCode("GB").setPostCode("NW1 6XE").setProvince("NY").setCity("NewYork").setStreet1("207 TELLURIDE DR")
//                .setTel("15811563568")
//                .build();
//        //物流订单详情
//        AutoOutDepositoryGTO.WaybillGoodDetailDto goodDetailDto = AutoOutDepositoryGTO.WaybillGoodDetailDto.newBuilder()
//                .setDeclaredNameCn("mp4 播放器").setDeclaredNameEn("mp4 medie player").setDeclaredValue(12.2)
//                .setGoodsTitle("万能播放器")
//                .setWeightInKg(1.23).setQuantity(1)
//                .setCurrencyCode(Constant.CURRENT_USD)
//                .build();
    //订单金额
//        AutoOutDepositoryGTO.CurrencyDto currencyDto = AutoOutDepositoryGTO.CurrencyDto.newBuilder().setMoneyCode(Constant.CURRENT_USD)
//                .setTotalMoney(12.22).build();
    //特殊标签
//        AutoOutDepositoryGTO.RemarkOrderDto remarkOrderDto = AutoOutDepositoryGTO.RemarkOrderDto.newBuilder()
//                .setChannel("895")
//                .setLogisticsProductCode("F3")
//                .setShippingMethod("")
//                .build();
    //仓库出库详情
//        AutoOutDepositoryGTO.OutDepositoryOrderItemDto itemDto1 = AutoOutDepositoryGTO.OutDepositoryOrderItemDto.newBuilder()
//                .setSku("YCSBNG8361455").setQuantity(1).build();
//        AutoOutDepositoryGTO.OutDepositoryRequest outDepositoryRequest = AutoOutDepositoryGTO.OutDepositoryRequest.newBuilder()
////                .set("mall20210426001")
//                .setLogisticCode(logisicCode)
//                .setPackageCount(1)
//                .setReceiverAddress(receiverAddress)
////                .setQuantity(1)
//                .setEstimatedWeight(1.2)
//                .addWaybillGoodDetailDtos(goodDetailDto)
//                .setCurrencyDto(currencyDto)
//                .setRemarkOrderDto(remarkOrderDto)
//                .addOrderItems(itemDto1)
////                .setSendDate(DateUtil.today())
//                .build();
//        AutoOutDepositoryGTO.OutDepositoryResponseResult outDepositoryResponseResult = depositoryClient.addOutDepository(outDepositoryRequest);
//        System.out.println(GrpcJsonUtil.jsonFormat.printToString(outDepositoryResponseResult));
//    }


    @PostMapping("/getInventoryStore")
    public void getInventoryStore(String spuCode) {
        InventoryStoreGTO.GetSPUSizeTableReq request = InventoryStoreGTO.GetSPUSizeTableReq.newBuilder()
                .setSpuCode(spuCode)
                .build();
        InventoryStoreGTO.GetSPUSizeTableReply spuSizeTable = inventoryStoreClient.getSpuSizeTable(request);
        System.out.println("输出结构是:" + GrpcJsonUtil.jsonFormat.printToString(spuSizeTable));
    }

    @PostMapping("/getMetadata")
    public void getMetadata(String id) {
        MetadatapbMetadata.CategoryMeta meta = metadataClient.getCategory(id);
        System.out.println("输出结构是:" + GrpcJsonUtil.jsonFormat.printToString(meta));
    }

    @Autowired
    private LogisticMatchRuleHandler logisticMatchRuleHandler;

    @PostMapping("/ruleValidate")
    public LogisticRuleComposition ruleValidate(@RequestBody LogisticOrderDto dto) {
        LogisticRuleComposition firstSatisfyRule = logisticMatchRuleHandler.getFirstSatisfyRule(dto);
        System.out.println(firstSatisfyRule);
        return firstSatisfyRule;
    }

    @PostMapping("/ruleAllValidate")
    public List<LogisticRuleComposition> ruleAllValidate(@RequestBody LogisticOrderDto dto) {
        List<LogisticRuleComposition> allSatisfyRule = logisticMatchRuleHandler.getAllSatisfyRule(dto);
        System.out.println(allSatisfyRule);
        return allSatisfyRule;
    }

    @Autowired
    private SaleOrderRetryJob saleOrderRetryJob;

    @PostMapping("/getRetry")
    public String ruleAllValidate() throws Exception {
        saleOrderRetryJob.execute(null);
        return "ok";
    }


    @Autowired
    private SpringContextUtil springContextUtil;

    @GetMapping("/get")
    public void after() {
        //断点这里查看
        System.out.println(this.springContextUtil);
    }

    @Autowired
    private LogisticClient logisticClient;

    @GetMapping("/getPurchaseIds")
    public void getPurchaseIds(String param1, String param2) {
        PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoRequest request = PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoRequest.newBuilder()
                .addPurchaseId(param1).addPurchaseId(param2).build();
        PurchasePackageLogisticsInfoGTO.PurchasePackageLogisticsInfoResult result = logisticClient.getPurchasePackageLogisticsInfo(request);
        System.out.println("输出结构是:" + GrpcJsonUtil.jsonFormat.printToString(result));
    }


    @Autowired
    Purchase1688NoticeDepositoryConsumer purchase1688NoticeDepositoryConsumer;

    @GetMapping("/consumer")
    public void consumer(String orderId, String currentStatus, String msgSendTime, String buyerMemberId, String sellerMemberId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("orderId", orderId);
        jsonObject.put("currentStatus", currentStatus);
        jsonObject.put("msgSendTime", msgSendTime);
        jsonObject.put("buyerMemberId", buyerMemberId);
        jsonObject.put("sellerMemberId", sellerMemberId);
//        purchase1688NoticeDepositoryConsumer.purchase(orderId);
    }

    @Autowired
    private Purchase1688NoticeDepositoryRetryJob purchase1688NoticeDepositoryRetryJob;

    @GetMapping("/job1")
    public void consumer() throws Exception {
        purchase1688NoticeDepositoryRetryJob.execute("");
    }

    @Autowired
    private DeliveryPackageServiceImpl deliveryPackageService;

    @GetMapping("/syncUnfinishedPackageList")
    public void syncUnfinishedPackageList() throws Exception {
        deliveryPackageService.syncUnfinishedPackageList();
    }

    @Autowired
    private WanBLogisticManagerImpl wanBLogisticManager;

    @GetMapping("/getLogisticCompanyReceivedPackage")
    public void getLogisticCompanyReceivedPackage() throws Exception {
        LogisticTrackResponseDto logisticTrack = wanBLogisticManager.getLogisticTrack(null);
//        List<TrackPointDto> logisticCompanyReceivedPackage = wanBLogisticManager.getLogisticCompanyReceivedPackage(logisticTrack);
//        System.out.println(logisticCompanyReceivedPackage);
    }






 /*   @GetMapping("/addChannelOrderFor1688")
    public String addChannelOrderFor1688(String params) throws Exception {
        List<String> list = Arrays.asList(params.split(","));
       *//* ChannelOrderFor1688GTO.ChannelOrderFor1688Request request = ChannelOrderFor1688GTO.ChannelOrderFor1688Request.newBuilder()
                .addAllChannelOrders(list).build();
        ChannelOrderFor1688GTO.ChannelOrderFor1688Result result = logisticClient.addChannelOrderFor1688(request);
        String jsonString = GrpcJsonFormatUtils.toJsonString(result);*//*
        log.info("结果是： "+ jsonString);
        return jsonString;
    }*/


    @GetMapping("/getChannelOrderLogistics")
    public String getChannelOrderLogistics(String params) throws Exception {
        List<String> list = Arrays.stream(params.split(",")).collect(Collectors.toList());
        ChannelOrderLogisticsGTO.ChannelOrderLogisticsRequest request = ChannelOrderLogisticsGTO.ChannelOrderLogisticsRequest.newBuilder().addAllChannelOrderIdList(list).build();
        ChannelOrderLogisticsGTO.ChannelOrderLogisticsResponseResult result = logisticClient.getChannelOrderLogistics(request);
        String jsonString = GrpcJsonFormatUtils.toJsonString(result);
        log.info("结果是： "+ jsonString);
        return jsonString;
    }
    @Autowired
    WanBDepistoryUtil wanBDepistoryUtil;
    @GetMapping("/getPurchaseDetail")
    public PurchaseIntoDepositoryDetailForWanB getPurchaseDetail(String purchaseId) {
        PurchaseIntoDepositoryDetailForWanB purchaseIntoDepositoryDetailForWanB = wanBDepistoryUtil.queryPurchaseOrderDetail(purchaseId);
        return purchaseIntoDepositoryDetailForWanB;
    }

     @Autowired
     YunTuLogisitcManagerImpl yunTuLogisitcManagerImpl;
    @GetMapping("/getLogisitcDetail")
    public LogisticOrderDetailResponseDto getLogisitcDetail(String waybillNo) {
        LogisticOrderSearchDto searchDto = new LogisticOrderSearchDto();
        searchDto.setProcessCode(waybillNo);
        searchDto.setWayBillNo(waybillNo+"000");
        LogisticOrderDetailResponseDto logisticDetail = yunTuLogisitcManagerImpl.getLogisticDetail(searchDto);
        return logisticDetail;
    }

    @Autowired
    DepositoryStockService depositoryStockService;
    @GetMapping("/getDepositoryStockService")
    public void getDepositoryStockService() {
        DepositoryStockGTO.DepositoryStockDetailRequest request = DepositoryStockGTO.DepositoryStockDetailRequest.newBuilder()
                .addSkus("ooo").build();
        depositoryStockService.querySaleOrderStauts(request,null);
    }


    @Autowired
    LogisticsPackageMapper logisticsPackageMapper;
    @GetMapping("/getDepositoryStock")
    public void getDepositoryStock(Long id) {
        LogisticsPackageDO selectPage = logisticsPackageMapper.selectById(id);
        deliveryPackageService.refreshDeliveryPackage(Collections.singletonList(selectPage));
    }


    @Autowired
    DepositoryDeliveryResultRetryJob depositoryDeliveryResultRetryJob;
    @GetMapping("/depositoryDeliveryResultRetryJob")
    public void depositoryDeliveryResultRetryJob(String id) throws Exception {
        depositoryDeliveryResultRetryJob.execute(id);
    }

    @Autowired
    SaleOrderDetailService saleOrderDetailService;
    @PostMapping("/saleOrderDetailService")
    public void saleOrderDetailService(String message) throws Exception {
        DepositorySaleOrderReq depositorySaleOrderReq = JSON.parseObject(message, DepositorySaleOrderReq.class);
        SaleOrderDetailGTO.SaleOrderDetailRequest request = GrpcJsonFormatUtils.javaToGrpc(depositorySaleOrderReq, SaleOrderDetailGTO.SaleOrderDetailRequest.class);
        System.out.println("xxxx： "+GrpcJsonFormatUtils.toJsonString(request));
        saleOrderDetailService.addSaleOrder(request,null);
    }

    @Autowired
    PurchaseIntoDepositoryRetryJob purchaseIntoDepositoryRetryJob;
    @PostMapping("/purchaseIntoDepositoryRetryJob")
    public void PurchaseIntoDepositoryRetryJob(String messsage) throws Exception {
        purchaseIntoDepositoryRetryJob.execute(messsage);
    }


    @Autowired
    private SaleOrderStatusQueryService saleOrderStatusQueryService;
    @GetMapping("/statusQueryService")
    public void saleOrderStatusQueryService(String params) throws Exception {
        SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest request = SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest.newBuilder()
                .addAllSaleOrder(Arrays.asList(params.split(","))).build();
        saleOrderStatusQueryService.querySaleOrderStauts(request,null);
    }

    @Autowired
    private ManualOutDepositoryService manualOutDepositoryService;
    @Autowired
    private DeliveryProductServiceImpl deliveryProductService;
    @PostMapping("/manualOutDepositoryService")
    public void manualOutDepositoryService(String params) throws Exception {
        LogisticOrderDto logisticOrderDto = JSON.parseObject(params, LogisticOrderDto.class);
//        ManualOutDepositoryGTO.ManualOutDepositoryRequest request = GrpcJsonFormatUtils.javaToGrpc(logisticOrderDto, ManualOutDepositoryGTO.ManualOutDepositoryRequest.class);
//        manualOutDepositoryService.addOutDepository(request,null);
        deliveryProductService.manualOutDepository(Collections.singletonList(logisticOrderDto));
    }


    @Autowired
    private SaleOrderAssignmentPurchaseIdService saleOrderAssignmentPurchaseIdService;
    @PostMapping("/saleOrderAssignmentPurchaseIdService")
    public void saleOrderAssignmentPurchaseIdService(String params) throws Exception {
        List<SaleOrderProductsReq> saleOrderProductsReqs = JSON.parseArray(params, SaleOrderProductsReq.class);
        List<SaleOrderAssignmentPurchaseIdGTO.SaleOrder> orderList = saleOrderProductsReqs.stream().map(item -> {
            return SaleOrderAssignmentPurchaseIdGTO.SaleOrder.newBuilder()
                    .setSaleOrderId(item.getSalesOrderId())
                    .setItemId(item.getItemId())
                    .setPurchaseId(item.getPurchaseId())
                    .build();
        }).collect(Collectors.toList());

        SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdRequest request = SaleOrderAssignmentPurchaseIdGTO.SaleOrderAssignmentPurchaseIdRequest
                .newBuilder().addAllSaleOrder(orderList).build();
        saleOrderAssignmentPurchaseIdService.assignmentPurchaseId(request,null);
    }


    @PostMapping("/excel")
    public boolean excel(@RequestBody ExportExcelReq<DeliveryPackageReq> exportExcelReq){
        File file = deliveryPackageService.exportDeliveryPackageExcel(exportExcelReq);
        return true;
    }

    @Autowired
    PurchasePackageServiceImpl purchasePackageService;
    @GetMapping("/listObjects")
    public List<PurchasePackageLogisticsInfoVO> list(String params){
       return purchasePackageService.getPurchasePackageLogisticsInfo(Arrays.asList(params));
    }


    @GetMapping("/getChannelLogistics")
    public String getChannelLogistics(String params,Integer type){
        String[] split = params.split(",");
        ChannelOrderLogisticsDto channelOrderLogisticsDto = new ChannelOrderLogisticsDto();
        channelOrderLogisticsDto.setChannelOrderIdList(Arrays.asList(split));
        channelOrderLogisticsDto.setType(type);
        deliveryPackageService.getChannelLogistics(channelOrderLogisticsDto);
        return "ok";
    }

    @Autowired
    DeliveryPackageService deliveryPackageServiceimp;
    @PostMapping("/pushAeLogisticsOrder")
    public String pushAeLogisticsOrder(@RequestBody DeliveryPackageMsgDto deliveryPackageMsgDto){
        DeliveryPackageGTO.PackageRequest packageRequest = GrpcJsonFormatUtils.jsonToGrpc(DeliveryPackageGTO.PackageRequest.class, JSON.toJSONString(deliveryPackageMsgDto));
        DeliveryPackageGTO.DeliveryPackageRequest deliveryPackageRequest = DeliveryPackageGTO.DeliveryPackageRequest.newBuilder().addDeliveryPackageList(packageRequest).build();
        deliveryPackageServiceimp.pushAeLogisticsOrder(deliveryPackageRequest,null);
        return "ok";
    }


    @Autowired
    ChannelOrderForDirectSend channelOrderForDirectSend;

    @GetMapping("/directSendchannel")
    public boolean getDirectSendChannel(String params, Integer type){
        ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendRequest request =  ChannelOrderForDirectSendGTO.ChannelOrderQueryForDirectSendRequest.newBuilder()
                .setType(type)
                .addAllChannelOrders(Arrays.asList(params.split(","))).build();
//        channelOrderForDirectSend.queryChannelOrderForDirectSend(request,null);
        logisticClient.queryChannelOrderForDirectSend(request);
        return false;
    }
//
//    @PostMapping("/directSendchannel")
//    public boolean postDirectSendChannel(String params){
//        ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendRequest request = GrpcJsonFormatUtils.jsonToGrpc(ChannelOrderForDirectSendGTO.DeliveryPackageForDirectSendRequest.class, params);
//        channelOrderForDirectSend.pushDeliveryPack(request,null);
//        return false;
//    }


//    @Autowired
//    PurchaseIntoDepositoryRetryJob purchaseIntoDepositoryRetryJob;
    @PostMapping("/testLabel")
    public boolean testLabel(String params) {
        try {
            purchaseIntoDepositoryRetryJob.execute(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Autowired
    DepositoryIntoDepositoryMsgConsumer depositoryIntoDepositoryMsgConsumer;
    @PostMapping("/upload")
    public void depose(String filePathUri, InputStream inputStream) {
        OSSUtils.uploadFileInputStream(filePathUri,inputStream);
    }


    @PostMapping("/addPurchaseOrder")
    public boolean addPurchaseOrder(@RequestBody ManualPredictionReq manualPredictionReq){
        ChannelOrderLogisticsGTO.AddPurchaseOrderRequest request =  GrpcJsonFormatUtils.javaToGrpc(manualPredictionReq,ChannelOrderLogisticsGTO.AddPurchaseOrderRequest.class);
        ChannelOrderLogisticsGTO.AddPurchaseOrderResult addPurchaseOrderResult = logisticClient.addPurchaseOrder(request);
//        System.out.println(addPurchaseOrderResult);
//        channelOrderLogisticsService.addPurchaseOrder(request);
        return false;
    }

    @Autowired
    ChannelOrderLogisticsService channelOrderLogisticsService;
    @GetMapping("/updatePurchasePackage")
    public boolean updatePurchasePackage(int number) {
//        ChannelOrderLogisticsGTO.UpdatePurchasePackageRequest request = ChannelOrderLogisticsGTO.UpdatePurchasePackageRequest.newBuilder().addPurchaseId("301004147").build();
////        List<ChannelOrderLogisticsGTO.UpdatePurchasePackageResponse> updatePurchasePackageResponses = channelOrderLogisticsService.updatePurchasePackage(request, null);
////        System.out.println(updatePurchasePackageResponses);
//        ChannelOrderLogisticsGTO.UpdatePurchasePackageResult updatePurchasePackageResult = logisticClient.updatePurchasePackage(request);
//        System.out.println(updatePurchasePackageResult);
        return true;
    }

    @Autowired
    private TestCurrentMapper testCurrentMapper;
    @Autowired
    private TransactionTemplate transactionTemplate;
//    @Autowired
//    private RedisTemplate<String,String> redisTemplate;
    @GetMapping("/testCurrent")
    public boolean testCurrent(int number) {
        CountDownLatch countDownLatch = new CountDownLatch(number);
        countDownLatch.countDown();
        for (int i = 0; i < number; i++) {
            Thread thread = new Thread(() -> subDepository());
            countDownLatch.countDown();
            thread.start();
        }
        return true;
    }

    Random random = new Random();

    private void subDepository() {
        int nextInt = random.nextInt(10);
        try {
            System.out.println(Thread.currentThread().getName()+"--sleep----》"+nextInt);
            TimeUnit.SECONDS.sleep(nextInt);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent("KEY", "LOCK");
//        System.out.println(Thread.currentThread().getName()+"--获取锁----》"+aBoolean);
//        if (aBoolean) {
            try {
                transactionTemplate.execute(status -> {
//                TestCurrentDO testCurrentDO = testCurrentMapper.selectByIdForUpdate(1);
                    TestCurrentDO testCurrentDO = testCurrentMapper.selectById(1);
                    System.out.println(Thread.currentThread().getName()+"-数量----》" +  testCurrentDO.getCount());
                    if (testCurrentDO.getCount() > 0) {
                        testCurrentDO.setCount(testCurrentDO.getCount()-1);
                        testCurrentMapper.updateById(testCurrentDO);
                    }
                    if (nextInt / 2 == 0) {
                        throw new RuntimeException();
                    }
                    return "ok";
                });
            } catch (TransactionException e) {
                System.out.println(Thread.currentThread().getName()+"--异常----》");
            }finally {
//                Boolean key = redisTemplate.delete("KEY");
//                System.out.println(Thread.currentThread().getName()+"--释放锁----》"+key);
            }
//        }
    }

}
