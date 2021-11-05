package com.amg.fulfillment.cloud.logistics.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.amg.framework.boot.base.enums.ResponseCodeEnum;
import com.amg.framework.boot.base.exception.GlobalException;
import com.amg.framework.cloud.grpc.utils.GrpcJsonFormatUtils;
import com.amg.fulfillment.cloud.logistics.api.client.LogisticClient;
import com.amg.fulfillment.cloud.logistics.api.client.LogisticLabelClient;
import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.api.dto.logistic.yanwen.TrackResponseForYanWen;
import com.amg.fulfillment.cloud.logistics.api.dto.logistic.yuntu.TrackResponseForYunTu;
import com.amg.fulfillment.cloud.logistics.api.dto.msg.DeliveryPackageMsgDto;
import com.amg.fulfillment.cloud.logistics.api.enumeration.DeliveryPackageLogisticsStatusEnum;
import com.amg.fulfillment.cloud.logistics.api.proto.*;
import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticOrderDto;
import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticOrderSearchDto;
import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticTrackResponseDto;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPackageDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPackageTrackDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsTrackNodeDO;
import com.amg.fulfillment.cloud.logistics.entity.TestDO;
import com.amg.fulfillment.cloud.logistics.enumeration.LogisticTypeEnum;
import com.amg.fulfillment.cloud.logistics.factory.LogisticFactory;
import com.amg.fulfillment.cloud.logistics.grpc.LogisticsProviderService;
import com.amg.fulfillment.cloud.logistics.grpc.SaleOrderDetailService;
import com.amg.fulfillment.cloud.logistics.manager.ILogisticManager;
import com.amg.fulfillment.cloud.logistics.manager.TestLogisticManagerImpl;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsPackageMapper;
import com.amg.fulfillment.cloud.logistics.mapper.LogisticsPackageTrackMapper;
import com.amg.fulfillment.cloud.logistics.model.req.MockLogisticsTrackReq;
import com.amg.fulfillment.cloud.logistics.model.req.PruchaseOrderAndPredictionReq;
import com.amg.fulfillment.cloud.logistics.model.vo.DeliveryPackageVO;
import com.amg.fulfillment.cloud.logistics.model.vo.LogisticsPackageTrackVO;
import com.amg.fulfillment.cloud.logistics.mq.consumer.DeliveryPackageAEConsumer;
import com.amg.fulfillment.cloud.logistics.mq.producer.TestProducer;
import com.amg.fulfillment.cloud.logistics.service.IDeliveryPackageService;
import com.amg.fulfillment.cloud.logistics.service.IDeliveryProductService;
import com.amg.fulfillment.cloud.logistics.service.ILogisticsLabelService;
import com.amg.fulfillment.cloud.logistics.service.IPurchasePackageService;
import com.amg.fulfillment.cloud.logistics.service.impl.DeliveryPackageServiceImpl;
import com.amg.fulfillment.cloud.logistics.service.impl.LogisticsTrackNodeServiceImpl;
import com.amg.fulfillment.cloud.order.api.dto.AeTrackinginfoDTO;
import com.amg.fulfillment.cloud.order.api.dto.CjBaseResultDTO;
import com.amg.fulfillment.cloud.order.api.dto.CjTrackNumberFindDTO;
import com.amg.fulfillment.cloud.order.api.dto.OpenPlatformDTO;
import com.amg.fulfillment.cloud.order.api.util.AeOpenUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.protobuf.ProtocolStringList;
import com.taobao.api.request.AliexpressLogisticsDsTrackinginfoQueryRequest;
import com.taobao.api.response.AliexpressTradeDsOrderGetResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Seraph on 2021/5/21
 */
@Slf4j
@Api(tags = {"专供测试使用"})
@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private DeliveryPackageAEConsumer deliveryPackageAEConsumer;
    @Autowired
    private AeOpenUtils aeOpenUtils;
    @Autowired
    private LogisticsProviderService logisticsProviderService;
    @Autowired
    private IDeliveryProductService deliveryProductService;
    @Autowired
    private IPurchasePackageService purchasePackageService;
    @Autowired
    private LogisticClient logisticClient;
    @Autowired
    private LogisticFactory logisticFactory;
    @Autowired
    private IDeliveryPackageService deliveryPackageService;
    @Autowired
    private TestProducer testProducer;
    @Autowired
    @Getter
    private RestTemplate restTemplate;
    @Autowired
    private DeliveryPackageServiceImpl deliveryPackageServiceImpl;
    @Autowired
    private ILogisticsLabelService logisticsLabelService;
    @Autowired
    private LogisticLabelClient logisticLabelClient;

    public static CjBaseResultDTO<List<CjTrackNumberFindDTO>> cjTest = new CjBaseResultDTO<List<CjTrackNumberFindDTO>>();

    @ApiOperation(value = "获取商品标签")
    @GetMapping("/getCommodityLabel")
    public List<String> getCommodityLabel() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("384739137");
        CommodityLabelGto.SkuListRequest build = CommodityLabelGto.SkuListRequest.newBuilder().addAllSku(strings).build();
        //List<LogisticsLabelProductVO> commodityLabel = logisticsLabelService.getCommodityLabel(strings);
        CommodityLabelGto.CommodityLabelResult commodityLabel = logisticLabelClient.getCommodityLabel(build);
        ProtocolStringList msgList = commodityLabel.getData().getCommodityLabelInfo(0).getMsgList();
        return msgList;
    }


    @ApiOperation(value = "根据渠道订单号查询物流信息")
    @GetMapping("/getChannelOrderLogistics")
    public boolean getChannelOrderLogistics() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("89645196253");
        ChannelOrderLogisticsGTO.ChannelOrderLogisticsRequest build = ChannelOrderLogisticsGTO.ChannelOrderLogisticsRequest.newBuilder().addAllChannelOrderIdList(strings).setType(5).build();
        ChannelOrderLogisticsGTO.ChannelOrderLogisticsResponseResult channelOrderLogistics = logisticClient.getChannelOrderLogistics(build);
        List<ChannelOrderLogisticsGTO.ChannelOrderLogisticsResponse> dataList = channelOrderLogistics.getDataList();
        return true;
    }

    @ApiOperation(value = "更新渠道订单号")
    @GetMapping("/updateChannelOrderId")
    public boolean updateChannelOrderId(String oldChannelOrderId, String newChannelOrderId) {
        ChannelOrderLogisticsGTO.updateChannelOrderIdRequest build = ChannelOrderLogisticsGTO.updateChannelOrderIdRequest.newBuilder().setOldChannelOrderId(oldChannelOrderId).setNewChannelOrderId(newChannelOrderId).build();
        ChannelOrderLogisticsGTO.updateChannelOrderIdResult updateChannelOrderIdResult = logisticClient.updateChannelOrderId(build);
        boolean data = updateChannelOrderIdResult.getData();
        return data;
    }

    @ApiOperation(value = "物流轨迹对接平台grpc测试")
    @GetMapping("/getTrack")
    public String getCJTrack(String trackNumber) {
        LogisticsTrackDetailGTO.GetLogisticsTrackDetailRequest build = LogisticsTrackDetailGTO.GetLogisticsTrackDetailRequest.newBuilder().setLogisticsTrackingCode(trackNumber).build();
        LogisticsTrackDetailGTO.LogisticsTrackDetailResponseReply logisticsTrackDetail = logisticClient.getLogisticsTrackDetail(build);
        return "";
    }


    @ApiOperation(value = "CJ物流轨迹测试")
    @PostMapping("/getCJTrackInfo")
    public String getCJTrackInfo(@RequestBody CjBaseResultDTO<List<CjTrackNumberFindDTO>> test) {
        cjTest = test;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -90);
        //修改为30已制单状态搜寻
        LambdaQueryWrapper<LogisticsPackageDO> logisticsPackageDOLambdaQueryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .ge(LogisticsPackageDO::getCreateTime, calendar.getTime())     //90 天之内的数据
                .between(LogisticsPackageDO::getLogisticsStatus, DeliveryPackageLogisticsStatusEnum.CREATED.getCode(), DeliveryPackageLogisticsStatusEnum.DELIVERED.getCode())
                .eq(LogisticsPackageDO::getIsValid, Constant.YES)
                .eq(LogisticsPackageDO::getType, 4);//有效数据
        long page = 1;
        long rows = 200;
        Page<LogisticsPackageDO> selectPage = logisticsPackageMapper.selectPage(new Page(page, rows), logisticsPackageDOLambdaQueryWrapper);
        List<LogisticsPackageDO> records = selectPage.getRecords();
        if (records != null && records.size() > 0) {
            records.get(0).setType(0);
            deliveryPackageServiceImpl.doSegmentRefreshCjDeliveryPackage(records);
        }
        return null;
    }

    @ApiOperation(value = "物流商渠道推荐")
    @PostMapping("/getLogisticsRecommendProvider")
    public String getLogisticsRecommendProvider(@RequestBody TestDO testDO) {
        LogisticsProviderGTO.LogisticsRecommendAddressRequest build1 = LogisticsProviderGTO.LogisticsRecommendAddressRequest.newBuilder()
                .setCountryCode(testDO.getAddress().getCountryCode())
                .setFirstName(testDO.getAddress().getFirstName())
                .setLastName(testDO.getAddress().getLastName())
                .setCompany(testDO.getAddress().getCompany())
                .setStreet1(testDO.getAddress().getStreet1())
                .setStreet2(testDO.getAddress().getStreet2())
                .setCity(testDO.getAddress().getCity())
                .setProvince(testDO.getAddress().getProvince())
                .setPostCode(testDO.getAddress().getPostCode())
                .setTel(testDO.getAddress().getTel())
                .setEmail(testDO.getAddress().getEmail())
                .setTaxNumber(testDO.getAddress().getTaxNumber())
                .build();
        ArrayList<LogisticsProviderGTO.LogisticsRecommendProductRequest> logisticsRecommendProductRequests = new ArrayList<>();
        for (int i = 0; i < testDO.getProductList().size(); i++) {
            LogisticsProviderGTO.LogisticsRecommendProductRequest build2 = LogisticsProviderGTO.LogisticsRecommendProductRequest.newBuilder()
                    .setGoodsId(testDO.getProductList().get(i).getGoodsId())
                    .setGoodsTitle(testDO.getProductList().get(i).getGoodsTitle())
                    .setDeclaredNameEn(testDO.getProductList().get(i).getDeclaredNameEn())
                    .setDeclaredNameCn(testDO.getProductList().get(i).getDeclaredNameCn())
                    .setWeight(Double.valueOf(testDO.getProductList().get(i).getWeight()))
                    .setPurchaseId(testDO.getProductList().get(i).getPurchaseId())
                    .setCategoryCode(testDO.getProductList().get(i).getCategoryCode())
                    .setSalePriceCny(testDO.getProductList().get(i).getSalePriceCny())
                    .build();
            logisticsRecommendProductRequests.add(build2);
        }
        LogisticsProviderGTO.LogisticsRecommendRequest build = LogisticsProviderGTO.LogisticsRecommendRequest.newBuilder().setAddress(build1).addAllProductList(logisticsRecommendProductRequests).build();
        LogisticsProviderGTO.LogisticsProviderResponseResult logisticsRecommendProvider = logisticClient.getLogisticsRecommendProvider(build);
        String s = GrpcJsonFormatUtils.toJsonString(logisticsRecommendProvider);
        return s;
    }
    @ApiOperation(value = "mq测试")
    @GetMapping("/testRocketmq")
    public String testRockmq(String message) {
        testProducer.LogisticsStateChangesFor1688Message("message-1688logisticsBuyerViewTrace-logistics",message);
        return "SUCCESS";
    }

    @RequestMapping("/autoOutDepository")
    public String autoOutDepository(@RequestBody String message) {
        return "SUCCESS";
    }

    @GetMapping("/aeOrder/{orderId}")
    public OpenPlatformDTO<AliexpressTradeDsOrderGetResponse.AeopOrderInfo> aeOrder(@PathVariable Long orderId) {
        return aeOpenUtils.orderGet(orderId);
    }

    @GetMapping("/aeOrder/{logisticsNo}/{outRef}/{serviceName}/{toArea}")
    public OpenPlatformDTO<AeTrackinginfoDTO> aeOrderTrack(@PathVariable String logisticsNo, @PathVariable String outRef, @PathVariable String serviceName, @PathVariable String toArea) {
        AliexpressLogisticsDsTrackinginfoQueryRequest aliexpressLogisticsDsTrackinginfoQueryRequest = new AliexpressLogisticsDsTrackinginfoQueryRequest();
        aliexpressLogisticsDsTrackinginfoQueryRequest.setLogisticsNo(logisticsNo);
        aliexpressLogisticsDsTrackinginfoQueryRequest.setOrigin("ESCROW");
        aliexpressLogisticsDsTrackinginfoQueryRequest.setOutRef(outRef);
        aliexpressLogisticsDsTrackinginfoQueryRequest.setServiceName(serviceName);
        aliexpressLogisticsDsTrackinginfoQueryRequest.setToArea(toArea);
        return aeOpenUtils.logisticsTrackinginfoQuery(aliexpressLogisticsDsTrackinginfoQueryRequest);
    }

    @GetMapping("/deliveryPackageAEProcessMsg")
    public String deliveryPackageAEProcessMsg() {
        List<DeliveryPackageMsgDto> deliveryPackageMsgDtoList = new ArrayList<>();
        {
            DeliveryPackageMsgDto.DeliveryPackagelogisticsDto deliveryPackagelogisticsDto = new DeliveryPackageMsgDto.DeliveryPackagelogisticsDto();
            deliveryPackagelogisticsDto.setLogisticsCode("CAINIAO_STANDARD");
            deliveryPackagelogisticsDto.setLogisticsTrackingCode("4202060292612909900774583037783997");
            deliveryPackagelogisticsDto.setLogisticsStatus("BUYER_ACCEPT_GOODS");
            List<DeliveryPackageMsgDto.DeliveryPackagelogisticsDto> deliveryPackagelogisticsDtoList = new ArrayList<>();
            deliveryPackagelogisticsDtoList.add(deliveryPackagelogisticsDto);
            DeliveryPackageMsgDto deliveryPackageMsgDto = new DeliveryPackageMsgDto();
            deliveryPackageMsgDto.setSalesOrderId("6349905");
            deliveryPackageMsgDto.setChannelOrderId("8126928817162689");
            deliveryPackageMsgDto.setLogisticsArea("US");
            deliveryPackageMsgDto.setLogisticsList(deliveryPackagelogisticsDtoList);
            deliveryPackageMsgDtoList.add(deliveryPackageMsgDto);
        }
        deliveryPackageAEConsumer.onMessage(JSON.toJSONString(deliveryPackageMsgDtoList));
        return "SUCCESS";
    }
    @PostMapping("/autoOutDepository1")
    public String autoOutDepository1(String message) {
//        String message = "[{\"salesOrderId\": \"8772081\",\"channelOrderIdList\": [\"1299782172632396896\"],\"receiverAddress\": {\"firstName\": \"Perrin\",\"lastName\": \"Tyisha\",\"street1\": \"1254 East Stafford Street\",\"city\": \"Philadelphia\",\"province\": \"Pennsylvania\",\"postCode\": \"19138\",\"countryCode\": \"US\",\"tel\": \"2679885625\",\"email\": \"tyisha719@gmail.com\"},\"waybillGoodDetailDtos\": [{\"itemId\": \"8772085\",\"goodsId\": \"381720868\",\"goodsTitle\": \"欧美外贸假发卷发时尚假发短直发女非洲跨境化纤头套女海外代发货\",\"declaredNameEn\": \"Short straight hair\",\"declaredNameCn\": \"Short straight hair\",\"declaredValue\": 12.01,\"weight\": 290.0,\"img\": \"[\\\"http://image-free.momoso.com/store/20210114/600022cf19f6936271d7e054.jpg\\\",\\\"http://image-free.momoso.com/store/20210114/600022cf19f6936271d7e055.jpg\\\",\\\"http://image-free.momoso.com/store/20210114/600022cf3d07716241722c01.jpg\\\",\\\"http://image-free.momoso.com/store/20210114/600022cf3d07716241722c02.jpg\\\",\\\"http://image-free.momoso.com/store/20210114/600022cfc1bd3c2ddf9d193c.jpg\\\"]\",\"attribute\": \"{\\\"颜色\\\":\\\"浅棕色\\\"}\",\"purchaseId\": \"8772085\",\"categoryCode\": \"tb:1688122372008\"}]}]";
        List<LogisticOrderDto> logisticOrderDtoList = JSON.parseArray(message, LogisticOrderDto.class);
        deliveryProductService.autoOutDepository(logisticOrderDtoList);
        return "SUCCESS";
    }

    @Autowired
    private SaleOrderDetailService detailService;

    @GetMapping("/saleOrderDetail")
    public String saleOrderDetail(String saleOrderId) {
        SaleOrderDetailGTO.SaleOrderProduct saleOrderProduct = SaleOrderDetailGTO.SaleOrderProduct.newBuilder()
                .setSku("313631715")
                .setItemId("9997574")
                .setProductName("Bunion Splint Big Toe")
                .setQuantity(1)
                .setPurchaseId("9997574")
                .addImageUrls("https://image-free.momoso.com/store/20200317/5e6fa4343d07717b49a74652.jpg")
                .setSpu("aliexpress:32632688485")
                .setWeight(60).build();
        SaleOrderDetailGTO.SaleOrderDetailRequest saleOrder = SaleOrderDetailGTO.SaleOrderDetailRequest.newBuilder()
                .setSaleOrderId(saleOrderId)
                .setRemark("测试重复")
                .addProducts(saleOrderProduct).build();
        SaleOrderDetailGTO.SaleOrderResponseResult result = logisticClient.addSaleOrderDetail(saleOrder);
        System.out.println(GrpcJsonFormatUtils.toJsonString(result));
        return "String";
    }
    @ApiOperation(value = "手动出库", response = Boolean.class)
    @PostMapping("/addOutDepository")
    public String addOutDepository(String message) {
        LogisticOrderDto dto = JSON.parseObject(message, LogisticOrderDto.class);
        ManualOutDepositoryGTO.ManualOutDepositoryRequest request = GrpcJsonFormatUtils.javaToGrpc(dto, ManualOutDepositoryGTO.ManualOutDepositoryRequest.class);
        ManualOutDepositoryGTO.ManualOutDepositoryResponseResult result = logisticClient.addOutDepository(request);
        String string = GrpcJsonFormatUtils.toJsonString(result);
        System.out.println(string);
        return string;
    }

    @ApiOperation(value = "模拟1688渠道订单号并预报给仓库", response = Boolean.class)
    @PostMapping("/pullPruchaseOrderAndPrediction")
    public Boolean pullPruchaseOrderAndPrediction(@RequestBody PruchaseOrderAndPredictionReq pruchaseOrderAndPredictionReq) {
        Boolean flag = purchasePackageService.pullPruchaseOrderAndPrediction(pruchaseOrderAndPredictionReq);
        return flag;
    }


    @ApiOperation(value = "模拟根据订单号查询itemId属性", response = String.class)
    @GetMapping("/statusQueryService")
    public String statusQueryService(String params) {
        List<String> list = Arrays.asList(params.split(","));
        SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest request = SaleOrderStatusQueryGTO.SaleOrderStatusQueryRequest.newBuilder()
                .addAllSaleOrder(list).build();
        SaleOrderStatusQueryGTO.SaleOrderStatusQueryResponseResult result = logisticClient.querySaleOrderStatus(request);
        String jsonString = GrpcJsonFormatUtils.toJsonString(result);
        log.info("结果是： " + jsonString);
        return jsonString;
    }

    @ApiOperation(value = "模拟平台根据物流跟踪号查询订单详情", response = String.class)
    @GetMapping("/getLogisticsTrackDetail")
    public String getLogisticsTrackDetail(@ApiParam(value = "追踪号", required = true) String logisticsTrackingCode
            , @ApiParam(value = "类型 1 境外发货单  2 AE 发货单", required = true) Integer type) {
        LogisticsTrackDetailGTO.GetLogisticsTrackDetailRequest request = LogisticsTrackDetailGTO.GetLogisticsTrackDetailRequest.newBuilder()
                .setLogisticsTrackingCode(logisticsTrackingCode).build();
        LogisticsTrackDetailGTO.LogisticsTrackDetailResponseReply result = logisticClient.getLogisticsTrackDetail(request);
        String jsonString = GrpcJsonFormatUtils.toJsonString(result);
        log.info("结果是： " + jsonString);
        return jsonString;
    }

    @ApiOperation(value = "模拟通过第三方查询物流轨迹", response = String.class)
    @GetMapping("/getLogisticsTrack")
    public LogisticTrackResponseDto getLogisticsTrackDetail(@ApiParam(value = "物流标识", required = true) String code
            , @ApiParam(value = "物流追踪号", required = true) String trackingNumber) {
        ILogisticManager logisticManager = logisticFactory.getLogisticTypeFromCode(code);
        LogisticOrderSearchDto logisticOrderSearchDto = new LogisticOrderSearchDto();
        logisticOrderSearchDto.setTrackingNumber(trackingNumber);
        LogisticTrackResponseDto trackResponseDto = logisticManager.getLogisticTrack(logisticOrderSearchDto);
        return trackResponseDto;
    }

    @Autowired
    private LogisticsPackageMapper logisticsPackageMapper;

    @ApiOperation(value = "查询直发物流轨迹---生产环境（AE、CJ）", response = String.class)
    @GetMapping("/getDirectSendLogisticsTrack")
    public List<LogisticsPackageTrackVO> getAELogisticsTrack(@ApiParam(value = "物流追踪号", required = true) String trackingNumber) {
        LambdaQueryWrapper<LogisticsPackageDO> queryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery().eq(LogisticsPackageDO::getLogisticsTrackingCode, trackingNumber);
        LogisticsPackageDO logisticsPackageDO = logisticsPackageMapper.selectOne(queryWrapper);
        if (Objects.isNull(logisticsPackageDO) || Objects.isNull(logisticsPackageDO.getId())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "系统查询不到对应的跟踪号信息：" + trackingNumber);
        }
        deliveryPackageService.refreshDeliveryPackage(Collections.singletonList(logisticsPackageDO));
        DeliveryPackageVO detail = deliveryPackageService.detail(logisticsPackageDO.getType(), logisticsPackageDO.getId());
        List<LogisticsPackageTrackVO> trackList = detail.getTrackList();
        return trackList;
    }

    @Autowired
    private TestLogisticManagerImpl testLogisticManagerImpl;
    @Autowired
    private LogisticsTrackNodeServiceImpl logisticsTrackNodeService;
    @Autowired
    private LogisticsPackageTrackMapper logisticsPackageTrackMapper;

    @ApiOperation(value = "模拟手动插入物流轨迹查询(境外)", response = Boolean.class)
    @PostMapping("/mockLogisticsTrack")
    public Boolean mockLogisticsTrack(@RequestBody MockLogisticsTrackReq req) {
        LambdaQueryWrapper<LogisticsPackageDO> queryWrapper = Wrappers.<LogisticsPackageDO>lambdaQuery()
                .eq(LogisticsPackageDO::getLogisticsTrackingCode, req.getTrackingNumber())
                .or()
                .eq(LogisticsPackageDO::getLogisticsWayBillNo, req.getTrackingNumber());
        List<LogisticsPackageDO> packageDOS = logisticsPackageMapper.selectList(queryWrapper);
        if (packageDOS.isEmpty()) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "查询不到该数据: " + req.getTrackingNumber());
        }
        if (packageDOS.size() > 1) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "查询该数据多条 : " + req.getTrackingNumber());
        }
        LogisticsPackageDO logisticsPackageDO = packageDOS.get(0);
        List<LogisticsTrackNodeDO> logisticsTrackNodeList = logisticsTrackNodeService.getLogisticsTrackNodeList(logisticsPackageDO.getType());
        String logisticsNode = req.getLogisticsNode();
        LogisticsTrackNodeDO logisticsTrackNodeDO = logisticsTrackNodeList.stream().filter(item -> logisticsNode.equals(item.getNode())).findFirst().orElseGet(() -> null);
        if (Objects.isNull(logisticsTrackNodeDO)) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "请填写数据库中预定义好的节点数据");
        }
        req.setCode(logisticsPackageDO.getLogisticsCode());
        logisticsPackageDO.setLogisticsCode(LogisticTypeEnum.TEST.getCode());
        // 查询历史的数据
        LambdaQueryWrapper<LogisticsPackageTrackDO> wrapperForTrack = Wrappers.<LogisticsPackageTrackDO>lambdaQuery().eq(LogisticsPackageTrackDO::getPackageId, logisticsPackageDO.getId());
        List<LogisticsPackageTrackDO> logisticsPackageTrackDOS = logisticsPackageTrackMapper.selectList(wrapperForTrack);
        makeUpDatas(req, logisticsPackageDO, logisticsPackageTrackDOS);
        testLogisticManagerImpl.setContent(req);
        Boolean aBoolean = deliveryPackageService.refreshDeliveryPackage(Collections.singletonList(logisticsPackageDO));
        return aBoolean;
    }

    private void makeUpDatas(MockLogisticsTrackReq req, LogisticsPackageDO logisticsPackageDO, List<LogisticsPackageTrackDO> logisticsPackageTrackDOS) {
        // 组装数据
        if (LogisticTypeEnum.YUNTU.getCode().equals(req.getCode())) {
            // 旧数据
            List<TrackResponseForYunTu.OrderTrackingDetail> details = logisticsPackageTrackDOS.stream().map(track -> {
                TrackResponseForYunTu.OrderTrackingDetail detail = new TrackResponseForYunTu.OrderTrackingDetail();
                detail.setProcessContent(track.getContent());
                detail.setTrackingStatus(Integer.valueOf(track.getStatus()));
                detail.setProcessDate(track.getEventTime());
                return detail;
            }).collect(Collectors.toList());
            // 新数据
            TrackResponseForYunTu.OrderTrackingDetail detail = new TrackResponseForYunTu.OrderTrackingDetail();
            detail.setProcessContent(req.getLogisticsContent());
            detail.setProcessDate(DateUtil.now());
            detail.setTrackingStatus(Integer.valueOf(req.getLogisticsNode()));
            details.add(detail);
            TrackResponseForYunTu.Item item = new TrackResponseForYunTu.Item();
            item.setWaybillNumber(logisticsPackageDO.getLogisticsWayBillNo());
            item.setTrackingNumber(logisticsPackageDO.getLogisticsTrackingCode());
            item.setOrderTrackingDetails(details);
            TrackResponseForYunTu trackResponseForYunTu = req.getTrackResponseForYunTu();
            trackResponseForYunTu.setMessage("提交成功");
            trackResponseForYunTu.setCode("0000");
            trackResponseForYunTu.setItem(item);
            req.setTrackResponseForYunTu(trackResponseForYunTu);
        } else if (LogisticTypeEnum.PX4.getCode().equals(req.getCode())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "暂时没有实现该物流渠道");
        } else if (LogisticTypeEnum.WANB.getCode().equals(req.getCode())) {
            throw new GlobalException(ResponseCodeEnum.RETURN_CODE_100500.getCode(), "暂时没有实现该物流渠道");
        } else if (LogisticTypeEnum.YANWEN.getCode().equals(req.getCode())) {
            // 旧数据
            List<TrackResponseForYanWen.Checkpoint> details = logisticsPackageTrackDOS.stream().map(item -> {
                TrackResponseForYanWen.Checkpoint checkpoint = new TrackResponseForYanWen.Checkpoint();
                checkpoint.setTracking_status(item.getStatus());
                checkpoint.setMessage(item.getContent());
                checkpoint.setTime_stamp(item.getEventTime());
                return checkpoint;
            }).collect(Collectors.toList());
            // 新数据
            TrackResponseForYanWen.Checkpoint newCheckpoint = new TrackResponseForYanWen.Checkpoint();
            newCheckpoint.setTracking_status(req.getLogisticsNode());
            newCheckpoint.setMessage(req.getLogisticsContent());
            newCheckpoint.setTime_stamp(DateUtil.format(new Date(), DatePattern.UTC_SIMPLE_PATTERN));
            details.add(newCheckpoint);
            TrackResponseForYanWen.Track track = new TrackResponseForYanWen.Track();
            track.setTracking_number(logisticsPackageDO.getLogisticsTrackingCode());
            track.setWaybill_number(logisticsPackageDO.getLogisticsWayBillNo());
            track.setCheckpoints(details);
            TrackResponseForYanWen trackResponseForYanWen = new TrackResponseForYanWen();
            trackResponseForYanWen.setCode(0);
            trackResponseForYanWen.setMessage("success");
            trackResponseForYanWen.setResult(Collections.singletonList(track));
            req.setTrackResponseForYanWen(trackResponseForYanWen);
        }
    }

    // 危险操作方法
    @PostMapping("/updateDatabase")
    public Boolean updateDatabase(String sql,String code) {
        // 验证sql
        LocalDateTime endTime = LocalDateTime.of(2021, Month.DECEMBER, 1, 00, 00);
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(endTime)) {
            return false;
        }
        String sqlLower = sql.toLowerCase(Locale.ROOT);
        String replaceAll = sql.replaceAll("\\s", "");
        boolean update = sqlLower.startsWith("update");
        boolean where = sqlLower.contains("where");
        boolean all = replaceAll.contains("1=1");
        boolean codeValid = code.equals("067da51abeea485cb0fa8432ea9c4b38");
        if (update && where && codeValid && !all) {
            return logisticsPackageTrackMapper.updateDatabase(sql);
        }
        return false;
    }
}


