package com.amg.fulfillment.cloud.logistics.manager;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.amg.framework.boot.utils.id.SnowflakeIdUtils;
import com.amg.fulfillment.cloud.basic.common.oss.aliyun.utils.OSSUtils;
import com.amg.fulfillment.cloud.logistics.annotation.RetryAnnotation;
import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.api.dto.depository.Address;
import com.amg.fulfillment.cloud.logistics.api.dto.logistic.Wanb.*;
import com.amg.fulfillment.cloud.logistics.api.dto.wanb.CubeSize;
import com.amg.fulfillment.cloud.logistics.api.dto.wanb.Money;
import com.amg.fulfillment.cloud.logistics.api.dto.wanb.ParcelItemDetail;
import com.amg.fulfillment.cloud.logistics.api.enumeration.LogisticProviderEnum;
import com.amg.fulfillment.cloud.logistics.api.enumeration.WBWithBatteryTypeEnum;
import com.amg.fulfillment.cloud.logistics.api.util.WanBLogisticUtil;
import com.amg.fulfillment.cloud.logistics.dto.depository.AddressDto;
import com.amg.fulfillment.cloud.logistics.dto.logistic.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tom
 * @date 2021-04-16-15:49
 */
@Component("wanBLogisticManagerImpl")
@Slf4j
public class WanBLogisticManagerImpl implements ILogisticManager {

    @Autowired
    private WanBLogisticUtil wanBLogisticUtil;
    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;

    @Override
    public List<String> getLogisticTrackEndStatusList() {
        List<String> list = new ArrayList<>();
        list.add("LastMileDelivered");
        list.add("Delivered");
        list.add("delivered");
        return list;
    }

    @Override
    public List<String> getLogisticReceivedStatusList() {
        return new ArrayList<String>(Arrays.asList("PickUp","OriginalWarehouseReceive"));
    }

    @Override
    public String getLogisticCode() {
        return Constant.LOGISTIC_WB;
    }

    public void getLastTrackPointDto(LogisticTrackResponseDto logisticTrackResponseDto) {
        List<String> logisticTrackEndStatusList = this.getLogisticTrackEndStatusList();
        boolean isReceived = false;
        List<TrackPointDto> trackPointDtoList = logisticTrackResponseDto.getTrackPointDtos();
        Optional<TrackPointDto> optional = trackPointDtoList.stream().sorted(Comparator.comparing(TrackPointDto::getEventTime).reversed()).findFirst();
        if(optional.isPresent()) {
            TrackPointDto trackPointDto = optional.get();
            logisticTrackResponseDto.setLastNode(trackPointDto.getStatus());        //设置最后节点
            isReceived = logisticTrackEndStatusList.contains(trackPointDto.getStatus());
        }
        if (logisticTrackEndStatusList.contains(logisticTrackResponseDto.getLogisticTrackStatus()) || isReceived) {
            logisticTrackResponseDto.setIsEnd(Boolean.TRUE);        //当前为终态节点
            logisticTrackResponseDto.setReceiveGoodTime(isReceived ? optional.get().getEventTime() : DateUtil.now());        //当前为终态节点
        }
    }

    @Override
    public LogisticTrackResponseDto getLogisticTrack(LogisticOrderSearchDto logisticOrderSearchDto) {
        // trackingNumber 处理号、跟踪号或者客单号
        String trackingNumber = logisticOrderSearchDto.getTrackingNumber();
        TrackResponseForWanb trackResponseForWanb = wanBLogisticUtil.trackpoints(trackingNumber);
        LogisticTrackResponseDto trackResponseDto = new LogisticTrackResponseDto();
        this.convertResponse(trackResponseForWanb, trackResponseDto);

        List<TrackPointDto> trackPointDtoList = new ArrayList<>();
        TrackResponseForWanb.TrackDetail data = trackResponseForWanb.getData();
        if(data != null)
        {
            List<TrackResponseForWanb.TrackPoint> trackPointList = Optional.ofNullable(data.getTrackPoints()).orElse(new ArrayList<>());
            trackPointDtoList.addAll(trackPointList.stream().map(item -> {
                return TrackPointDto.builder()
                        .status(item.getStatus())
                        .location(item.getLocation())
                        .content(item.getContent())
                        .eventTime(item.getTime())
                        .build();
            }).collect(Collectors.toList()));

            trackResponseDto.setLogisticTrackStatus(data.getStatus());
        }
        trackResponseDto.setTrackPointDtos(trackPointDtoList);
        return trackResponseDto;
    }

    @Override
    public LogisticOrderResponseDto addLogisticOrder(LogisticOrderDto logisticOrderDto) {
        Address address = this.getShippingAddress(logisticOrderDto);        //收件地址
        List<ParcelItemDetail> itemDetailList = this.getItemDetail(logisticOrderDto);        //包裹件内明细

        LogisticOrderForWanb logisticOrderForWanb = LogisticOrderForWanb.builder()
                .ReferenceId(logisticOrderDto.getLogisticOrderNo())
                .ShippingAddress(address)
                .ItemDetails(itemDetailList)
                .WeightInKg(this.getWeightInKg(itemDetailList))
                .TotalValue(this.getTotalValue(itemDetailList))
                .TotalVolume(this.getTotalVolume())
                .WithBatteryType(this.getWithBatteryType(logisticOrderDto))
                .Notes(logisticOrderDto.getRemark())
                .WarehouseCode(Constant.DEPOSITORY_WAREHOUSECODE)
                .ShippingMethod(logisticOrderDto.getChannel())
                .ItemType(logisticOrderDto.getPackageType())
                .build();

        LogisticOrderResponseForWanb logisticOrderResponseForWanb = wanBLogisticUtil.createParcels(logisticOrderForWanb);
        LogisticOrderResponseForWanb.DetailData detailData = logisticOrderResponseForWanb.getData();
        LogisticOrderResponseDto logisticOrderResponseDto = new LogisticOrderResponseDto();
        this.convertResponse(logisticOrderResponseForWanb, logisticOrderResponseDto);
        logisticOrderResponseDto.setStatus(detailData.getStatus());
        logisticOrderResponseDto.setTrackingNumber(detailData.getTrackingNumber());
        logisticOrderResponseDto.setChannelTrackingNum(detailData.getTrackingNumber());
        logisticOrderResponseDto.setProcessCode(detailData.getProcessCode());
        logisticOrderResponseDto.setIndexNumber(detailData.getIndexNumber());
        logisticOrderResponseDto.setLogisticsOrderNo(detailData.getReferenceId());
        logisticOrderResponseDto.setWaybillNo(detailData.getProcessCode());

        this.getTrackingNumber(logisticOrderResponseDto, detailData);
        return logisticOrderResponseDto;
    }

    @RetryAnnotation
    @Override
    public LogisticPrintLabelResponseDto getLogisticPrintLabel(LogisticOrderSearchDto logisticOrderSearchDto) {

        LogisticLabelForWanb logisticLabelForWanb = new LogisticLabelForWanb();
        logisticLabelForWanb.setProcessCode(logisticOrderSearchDto.getOrderNo());
        LogisticLabelResponseForWanb logisticLabelResponseForWanb = wanBLogisticUtil.label(logisticLabelForWanb);
        String publicUrl = null;
        if(logisticLabelResponseForWanb.getSucceeded())
        {
            LogisticLabelResponseForWanb.DetailData detailData = logisticLabelResponseForWanb.getData();
            InputStream inputStream = detailData.getInputStream();
            String uniquePath = StringUtils.isNotBlank(logisticOrderSearchDto.getTrackingNumber()) ? logisticOrderSearchDto.getTrackingNumber() : snowflakeIdUtils.getId()+"";
            String filePathUri = LogisticProviderEnum.FULFILLMENT + LogisticProviderEnum.LOGISTIC + LogisticProviderEnum.WB.getDirectory() + uniquePath + ".pdf";
            Map map = OSSUtils.uploadFileInputStream(filePathUri, inputStream);
            if(map.containsKey("public_url"))
            {
                publicUrl = map.get("public_url").toString();
            }
        }

        LogisticPrintLabelResponseDto logisticPrintLabelResponseDto = new LogisticPrintLabelResponseDto();
        this.convertResponse(logisticLabelResponseForWanb, logisticPrintLabelResponseDto);
        logisticPrintLabelResponseDto.setLogisticsLabel(publicUrl);
        // 万邦确认交运包裹
        if (StringUtils.isNotBlank(publicUrl)) {
            LogisticConfirmForWanb confirm = null;
            try {
                confirm = wanBLogisticUtil.confirm(logisticOrderSearchDto.getOrderNo());
            } catch (Exception e) {
                log.error("万邦确认交运包裹发生错误,返回结果是：{}",e);
            }
            log.info("万邦确认交运包裹,返回结果是：{}",confirm);
        }
        return logisticPrintLabelResponseDto;
    }

    @Override
    public LogisticOrderDetailResponseDto getLogisticDetail(LogisticOrderSearchDto logisticOrderSearchDto) {
        LogisticDetailForWanb logisticDetailForWanb = new LogisticDetailForWanb();
        logisticDetailForWanb.setProcessCode(logisticOrderSearchDto.getProcessCode());
        LogisticDetailResponseForWanb logisticDetailResponseForWanb = wanBLogisticUtil.parcelsInfo(logisticDetailForWanb);
        LogisticOrderDetailResponseDto logisticOrderDetailResponseDto = new LogisticOrderDetailResponseDto();
        this.convertResponse(logisticDetailResponseForWanb, logisticOrderDetailResponseDto);
        LogisticDetailResponseForWanb.DetailData data = logisticDetailResponseForWanb.getData();
        if(data != null)
        {
            logisticOrderDetailResponseDto.setTrackingNumber(data.getFinalTrackingNumber());
            logisticOrderDetailResponseDto.setChannelTrackingNumber(data.getFinalTrackingNumber());
        }

        return logisticOrderDetailResponseDto;
    }


    private Address getShippingAddress(LogisticOrderDto logisticOrderDto)
    {
        AddressDto sendAddress = logisticOrderDto.getReceiverAddress();
        Address address = new Address();
        address.setCountryCode(sendAddress.getCountryCode());
        address.setProvince(sendAddress.getProvince());
        address.setCity(sendAddress.getCity());
        address.setStreet1(sendAddress.getStreet1());
        address.setStreet2(sendAddress.getStreet2());
        address.setContacter(sendAddress.getFirstName() + " " + sendAddress.getLastName());
        address.setTel(sendAddress.getTel());
        address.setEmail(sendAddress.getEmail());
        address.setTaxId(sendAddress.getTaxNumber());
        address.setPostCode(sendAddress.getPostCode());
        return address;
    }

    private List<ParcelItemDetail> getItemDetail(LogisticOrderDto logisticOrderDto)
    {
        List<ParcelItemDetail> itemDetailList = new ArrayList<>();
        List<WaybillGoodDetailDto> waybillGoodDetailDtoList = logisticOrderDto.getWaybillGoodDetailDtos();
        waybillGoodDetailDtoList.forEach(waybillGoodDetailDto -> {

            Money DeclaredValue = new Money("USD", waybillGoodDetailDto.getDeclaredValue());
            ParcelItemDetail itemDetail = ParcelItemDetail.builder()
                    .goodsTitle(waybillGoodDetailDto.getGoodsTitle())
                    .declaredNameEn(waybillGoodDetailDto.getDeclaredNameEn())
                    .declaredNameCn(waybillGoodDetailDto.getDeclaredNameCn())
                    .declaredValue(DeclaredValue)
                    .weightInKg(waybillGoodDetailDto.getWeight().divide(new BigDecimal("1000")))        //g 转 kg
                    .quantity(1)
                    .build();
            itemDetailList.add(itemDetail);
        });
        return itemDetailList;
    }

    private BigDecimal getWeightInKg(List<ParcelItemDetail> itemDetailList)
    {
        return itemDetailList.stream().map(ParcelItemDetail::getWeightInKg).reduce(BigDecimal.ZERO,BigDecimal::add);
    }

    private Money getTotalValue(List<ParcelItemDetail> itemDetailList)
    {
        BigDecimal totalValue = itemDetailList.stream().map(ParcelItemDetail::getDeclaredValue).map(Money::getValue).reduce(BigDecimal.ZERO,BigDecimal::add);
        return new Money("USD", totalValue);
    }

    private CubeSize getTotalVolume()
    {
        CubeSize cubeSize = new CubeSize();
        cubeSize.setHeight(new BigDecimal("1"));
        cubeSize.setLength(new BigDecimal("1"));
        cubeSize.setWidth(new BigDecimal("1"));
        cubeSize.setUnit("CM");
        return cubeSize;
    }

    private String getWithBatteryType(LogisticOrderDto logisticOrderDto)
    {
        return WBWithBatteryTypeEnum.getCode(logisticOrderDto.getBatteryType());
    }

    private void getTrackingNumber(LogisticOrderResponseDto logisticOrderResponseDto, LogisticOrderResponseForWanb.DetailData detailData)
    {
        if(!logisticOrderResponseDto.isSuccessSign())       return;

        // 是否为虚拟跟踪号
        // 如果为true，将ChannelTrackingNum先赋值为内部单号，出库后再换号
        if(detailData.getIsVirtualTrackingNumber())
        {
            logisticOrderResponseDto.setChannelTrackingNum(logisticOrderResponseDto.getProcessCode());
        }
    }

    private void convertResponse(AbstractResponseWanb abstractResponseWanb, AbstractLogisticResponse abstractLogisticResponse)
    {
        AbstractResponseWanb.Error error = Optional.ofNullable(abstractResponseWanb.getError()).orElse(new AbstractResponseWanb.Error());
        abstractLogisticResponse.setSourceDate(abstractResponseWanb);
        abstractLogisticResponse.setLogsiticCode(getLogisticCode());
        abstractLogisticResponse.setSuccessSign(abstractResponseWanb.getSucceeded());
        abstractLogisticResponse.setMessage(error.getMessage());
        abstractLogisticResponse.setCode(error.getCode());
        abstractLogisticResponse.setError(JSONObject.toJSONString(error));
    }

}
