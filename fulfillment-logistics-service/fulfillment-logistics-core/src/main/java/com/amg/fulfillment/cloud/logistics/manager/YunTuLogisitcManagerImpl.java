package com.amg.fulfillment.cloud.logistics.manager;

import com.amg.framework.boot.utils.id.SnowflakeIdUtils;
import com.amg.fulfillment.cloud.basic.common.oss.aliyun.utils.OSSUtils;
import com.amg.fulfillment.cloud.logistics.annotation.RetryAnnotation;
import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.api.dto.logistic.yuntu.*;
import com.amg.fulfillment.cloud.logistics.api.enumeration.LogisticProviderEnum;
import com.amg.fulfillment.cloud.logistics.api.util.YunTuLogisticUtil;
import com.amg.fulfillment.cloud.logistics.dto.depository.AddressDto;
import com.amg.fulfillment.cloud.logistics.dto.logistic.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tom
 * @date 2021-04-16-15:58
 */
@Slf4j
@Component("yunTuLogistcManagerImpl")
public class YunTuLogisitcManagerImpl implements ILogisticManager {

    @Autowired
    private YunTuLogisticUtil yunTuLogisticUtil;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;
    private static final String SUCCESS_CODE = "0000";


    @Override
    public List<String> getLogisticTrackEndStatusList() {
        List<String> list = new ArrayList<>();
        list.add("50");
        list.add("100");
        return list;
    }

    @Override
    public List<TrackPointDto> getLogisticCompanyReceivedPackage(LogisticTrackResponseDto logisticTrackResponseDto) {
        List<TrackPointDto> trackPointDtos = logisticTrackResponseDto.getTrackPointDtos();
        List<TrackPointDto> trackPointList = trackPointDtos.stream().sorted(Comparator.comparing(TrackPointDto::getEventTime)).collect(Collectors.toList());
        return trackPointList.stream()
                .filter(trackPointDto -> StringUtils.isNotBlank(trackPointDto.getContent()) && trackPointDto.getContent().toLowerCase().startsWith(getLogisticReceivedStatusList().get(0)))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getLogisticReceivedStatusList() {
        return new ArrayList<>(Collections.singleton("arrived at sort facility"));
    }

    @Override
    public String getLogisticCode() {
        return Constant.LOGISTIC_YUNTU;
    }

    @Override
    public LogisticTrackResponseDto getLogisticTrack(LogisticOrderSearchDto logisticOrderSearchDto) {
        TrackResponseForYunTu trackResponseForYunTu = yunTuLogisticUtil.trackingGettrackallinfo(logisticOrderSearchDto.getTrackingNumber());
        LogisticTrackResponseDto logisticTrackResponseDto = new LogisticTrackResponseDto();
        this.convertResponse(trackResponseForYunTu, logisticTrackResponseDto);
        List<TrackPointDto> trackPointDtoList = new ArrayList<>();
        if(trackResponseForYunTu.isSuccess())
        {
            TrackResponseForYunTu.Item item = trackResponseForYunTu.getItem();
            if(item != null)
            {
                List<TrackResponseForYunTu.OrderTrackingDetail> orderTrackingDetailList = Optional.ofNullable(item.getOrderTrackingDetails()).orElse(new ArrayList<>());
                orderTrackingDetailList.forEach(orderTrackingDetail -> {
                    TrackPointDto trackPointDto = TrackPointDto.builder()
                            .location(orderTrackingDetail.getProcessLocation())
                            .content(orderTrackingDetail.getProcessContent())
                            .status(String.valueOf(orderTrackingDetail.getTrackingStatus()))
                            .eventTime(orderTrackingDetail.getProcessDate())
                            .build();
                    trackPointDtoList.add(trackPointDto);
                });
            }
        }
        logisticTrackResponseDto.setTrackPointDtos(trackPointDtoList);
        return logisticTrackResponseDto;
    }

    @Override
    public LogisticOrderResponseDto addLogisticOrder(LogisticOrderDto logisticOrderDto) {
        List<LogisticOrderForYunTu.Parcel> parcels = this.getParcel(logisticOrderDto);      //申报信息
        LogisticOrderForYunTu.AddressForYuntu receiver = this.getReceiver(logisticOrderDto);        //收件地址
        BigDecimal weight = this.getWeight(logisticOrderDto);
        ArrayList<LogisticOrderForYunTu.OrderExtra> orderExtras = new ArrayList<>();
        LogisticOrderForYunTu.OrderExtra.OrderExtraBuilder builder = LogisticOrderForYunTu.OrderExtra.builder();
        builder.ExtraCode("V1");
        builder.ExtraName("云途预缴");
        LogisticOrderForYunTu.OrderExtra build = builder.build();
        orderExtras.add(build);
        //物流单
        LogisticOrderForYunTu logisticOrderForYunTu = LogisticOrderForYunTu.builder()
                .CustomerOrderNumber(logisticOrderDto.getLogisticOrderNo())
                .ShippingMethodCode(logisticOrderDto.getChannel())
                .TaxNumber(receiver.getTaxId())
                .PackageCount(1)
                .Weight(weight)
                .Parcels(parcels)
                .Receiver(receiver)
                .OrderExtra(orderExtras)
                .build();
        LogisticOrderResponseForYunTu logisticOrderResponseForYunTu = yunTuLogisticUtil.waybillCreateorder(logisticOrderForYunTu);
        List<LogisticOrderResponseForYunTu.OrderResponse> orderResponseList = logisticOrderResponseForYunTu.getItem();
        LogisticOrderResponseDto logisticOrderResponseDto = new LogisticOrderResponseDto();
        this.convertResponse(logisticOrderResponseForYunTu, logisticOrderResponseDto);
        // 预防一下错误
        // {"Item":null,"Code":"500","Message":"V2创建订单失败:响应状态代码不指示成功: 502 (Bad Gateway)。","RequestId":"1c8f73f7839f47058cc8dc417b137317","TimeStamp":"2021-08-09T02:48:33.8635231+00:00"}
        if (!Objects.isNull(orderResponseList) && !orderResponseList.isEmpty()) {
            LogisticOrderResponseForYunTu.OrderResponse orderResponse = orderResponseList.get(0);
            logisticOrderResponseDto.setWaybillNo(orderResponse.getWayBillNumber());
            logisticOrderResponseDto.setTrackingNumber(orderResponse.getTrackingNumber());
            logisticOrderResponseDto.setChannelTrackingNum(orderResponse.getTrackingNumber());
            logisticOrderResponseDto.setProcessCode(orderResponse.getTrackingNumber());
            logisticOrderResponseDto.setLogisticsOrderNo(orderResponse.getCustomerOrderNumber());

            //运单请求失败反馈失败原因
            if (StringUtils.isNoneBlank(orderResponse.getRemark())) {
                logisticOrderResponseDto.setMessage(logisticOrderResponseDto.getMessage() + " " + orderResponse.getRemark());
            }
            this.getTrackingNumber(logisticOrderResponseDto, orderResponse);
        }
        return logisticOrderResponseDto;
    }

    @RetryAnnotation
    @Override
    public LogisticPrintLabelResponseDto getLogisticPrintLabel(LogisticOrderSearchDto logisticOrderSearchDto) {
        log.debug(" getLogisticPrintLabel 开始时间： {}", LocalDateTime.now());
        long start = System.currentTimeMillis();
        String orderNo = StringUtils.isNoneBlank(logisticOrderSearchDto.getOrderNo()) ? logisticOrderSearchDto.getOrderNo() : logisticOrderSearchDto.getLogisticsOrderNo();
        LogisticLabelResponseForYunTu logisticLabelResponseForYunTu = yunTuLogisticUtil.labelPrint(orderNo);
        LogisticPrintLabelResponseDto logisticPrintLabelResponseDto = new LogisticPrintLabelResponseDto();
        this.convertResponse(logisticLabelResponseForYunTu, logisticPrintLabelResponseDto);
        List<LogisticLabelResponseForYunTu.OrderLabelPrint> orderLabelPrintList = logisticLabelResponseForYunTu.getItem();
        if(orderLabelPrintList != null && orderLabelPrintList.size() != 0) {
            //16小时的时效 ,所以需要先上传
            String publicUrl = null;
            String url = orderLabelPrintList.get(0).getUrl();
            try {
                long start_main = System.currentTimeMillis();
                log.debug(" 云途-打开面单并上传开始时间： {}", LocalDateTime.now());
                ResponseEntity<Resource> responseEntity = restTemplate.getForEntity(url, Resource.class);
                String uniquePath = StringUtils.isNotBlank(logisticOrderSearchDto.getTrackingNumber()) ? logisticOrderSearchDto.getTrackingNumber() : snowflakeIdUtils.getId()+"";
                String filePathUri = LogisticProviderEnum.FULFILLMENT + LogisticProviderEnum.LOGISTIC + LogisticProviderEnum.YANWEN.getDirectory() + uniquePath + ".pdf";
                Map map = OSSUtils.uploadFileInputStream(filePathUri,responseEntity.getBody().getInputStream());
                if(map.containsKey("public_url")) {
                    publicUrl = map.get("public_url").toString();
                    logisticPrintLabelResponseDto.setLogisticsLabel(publicUrl);
                }
                log.debug(" 云途-打开面单并上传结束时间： {}，耗时：{}", LocalDateTime.now(), System.currentTimeMillis() - start_main);
            } catch (IOException e) {
               log.error("云途---打开面单失败：地址是：{}，原因是：{}",url,e);
            }
        }
        long end = System.currentTimeMillis();
        log.debug(" getLogisticPrintLabel 结束时间： {},耗时间：{}", LocalDateTime.now(), end - start);
        return logisticPrintLabelResponseDto;
    }

    @Override
    public LogisticOrderDetailResponseDto getLogisticDetail(LogisticOrderSearchDto logisticOrderSearchDto) {
        LogisticDetailResponseForYunTu logisticDetailResponseForYunTu = yunTuLogisticUtil.waybillGetorder(logisticOrderSearchDto.getLogisticsOrderNo());
        LogisticOrderDetailResponseDto logisticOrderDetailResponseDto = new LogisticOrderDetailResponseDto();
        this.convertResponse(logisticDetailResponseForYunTu, logisticOrderDetailResponseDto);
        LogisticDetailResponseForYunTu.WaybillOrder waybillOrder = logisticDetailResponseForYunTu.getItem();
        if(waybillOrder != null) {
            logisticOrderDetailResponseDto.setTrackingNumber(StringUtils.isNotBlank(waybillOrder.getTrackingNumber()) ? waybillOrder.getTrackingNumber() : null);
            logisticOrderDetailResponseDto.setChannelTrackingNumber(StringUtils.isNotBlank(waybillOrder.getTrackingNumber()) ? waybillOrder.getTrackingNumber() : waybillOrder.getWayBillNumber());
        }
        return logisticOrderDetailResponseDto;
    }

    private BigDecimal getWeight(LogisticOrderDto logisticOrderDto)
    {
        List<WaybillGoodDetailDto> waybillGoodDetailDtoList = logisticOrderDto.getWaybillGoodDetailDtos();
        return waybillGoodDetailDtoList.stream().map(WaybillGoodDetailDto::getWeight).reduce(BigDecimal.ZERO, BigDecimal::add).divide(new BigDecimal("1000"));
    }

    private List<LogisticOrderForYunTu.Parcel> getParcel(LogisticOrderDto logisticOrderDto)
    {
        //需要特殊处理，如果是相同的 sku，需求组合在一起，然后设置数量
        Map<String, LogisticOrderForYunTu.Parcel> parcelMap = new HashMap<>();
        List<WaybillGoodDetailDto> waybillGoodDetailDtoList = logisticOrderDto.getWaybillGoodDetailDtos();
        waybillGoodDetailDtoList.forEach(waybillGoodDetailDto -> {
            LogisticOrderForYunTu.Parcel parcel = parcelMap.get(waybillGoodDetailDto.getGoodsId());
            if(parcel == null)
            {
                parcel = LogisticOrderForYunTu.Parcel.builder()
                        .Quantity(1)
                        .UnitWeight(waybillGoodDetailDto.getWeight().divide(new BigDecimal("1000")))
                        .EName(waybillGoodDetailDto.getDeclaredNameEn())
                        .CName(waybillGoodDetailDto.getDeclaredNameCn())
                        .CurrencyCode("USD")
                        .UnitPrice(waybillGoodDetailDto.getDeclaredValue())
                        .build();
                parcelMap.put(waybillGoodDetailDto.getGoodsId(), parcel);
            } else
            {
                parcel.setQuantity(parcel.getQuantity() + 1);       //设置数量数量
            }
        });
        return new ArrayList<>(parcelMap.values());
    }

    private LogisticOrderForYunTu.AddressForYuntu getReceiver(LogisticOrderDto logisticOrderDto)
    {
        AddressDto receiverAddress = logisticOrderDto.getReceiverAddress();
        LogisticOrderForYunTu.AddressForYuntu receiver = LogisticOrderForYunTu.AddressForYuntu.builder()
                .TaxId(receiverAddress.getTaxNumber())
                .CountryCode(receiverAddress.getCountryCode())
                .State(receiverAddress.getProvince())
                .City(receiverAddress.getCity())
                .Street(receiverAddress.getStreet1())
                .HouseNumber(receiverAddress.getStreet2())
                .Zip(receiverAddress.getPostCode())
                .FirstName(receiverAddress.getFirstName())
                .LastName(receiverAddress.getLastName())
                .Phone(receiverAddress.getTel())
                .build();
        return receiver;
    }

    private void getTrackingNumber(LogisticOrderResponseDto logisticOrderResponseDto, LogisticOrderResponseForYunTu.OrderResponse orderResponse)
    {
        // 一般第一次请求云途创建物流单号时是没有TrackingNumber(末端单号)的，此时要把WayBillNumber(云途内部单号)返回给仓库用于出库
        if(logisticOrderResponseDto.isSuccessSign() && StringUtils.isBlank(logisticOrderResponseDto.getChannelTrackingNum()))
        {
            logisticOrderResponseDto.setChannelTrackingNum(orderResponse.getWayBillNumber());
        }
    }

    private void convertResponse(AbstractResponseForYunTu abstractResponseForYunTu, AbstractLogisticResponse abstractLogisticResponse)
    {
        abstractLogisticResponse.setSourceDate(abstractResponseForYunTu);
        abstractLogisticResponse.setLogsiticCode(getLogisticCode());
        abstractLogisticResponse.setSuccessSign(SUCCESS_CODE.equals(abstractResponseForYunTu.getCode()) ? Boolean.TRUE : Boolean.FALSE);
        abstractLogisticResponse.setCode(abstractResponseForYunTu.getCode());
        abstractLogisticResponse.setMessage(abstractResponseForYunTu.getMessage());
    }
}
