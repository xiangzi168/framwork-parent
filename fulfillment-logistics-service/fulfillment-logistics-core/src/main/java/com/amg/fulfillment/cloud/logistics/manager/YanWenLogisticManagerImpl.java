package com.amg.fulfillment.cloud.logistics.manager;

import cn.hutool.core.lang.Snowflake;
import com.amg.framework.boot.utils.id.SnowflakeIdUtils;
import com.amg.fulfillment.cloud.basic.common.oss.aliyun.utils.OSSUtils;
import com.amg.fulfillment.cloud.logistics.annotation.RetryAnnotation;
import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.api.dto.logistic.yanwen.*;
import com.amg.fulfillment.cloud.logistics.api.enumeration.LogisticProviderEnum;
import com.amg.fulfillment.cloud.logistics.api.util.YanWenLogisticUtil;
import com.amg.fulfillment.cloud.logistics.dto.depository.AddressDto;
import com.amg.fulfillment.cloud.logistics.dto.logistic.*;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tom
 * @date 2021-04-19-14:14
 * 注意： 燕文快递仅仅支持一个快单详情
 */
@Slf4j
@Component("yanWenLogisticManagerImpl")
public class YanWenLogisticManagerImpl implements ILogisticManager {
    @Autowired
    private YanWenLogisticUtil yanWenLogisticUtil;
    @Autowired
    private SnowflakeIdUtils snowflakeIdUtils;

    protected static XmlMapper xmlMapper;

    private static final Integer SUCCESS_CODE = 0;

    @Override
    public List<String> getLogisticTrackEndStatusList() {
        return new ArrayList<>(Collections.singleton("LM40"));
    }

    @Override
    public List<String> getLogisticReceivedStatusList() {
        return new ArrayList<>(Collections.singleton("PU10"));
    }

    @Override
    public String getLogisticCode() {
        return Constant.LOGISTIC_YANWEN;
    }

    @Override
    public LogisticTrackResponseDto getLogisticTrack(LogisticOrderSearchDto logisticOrderSearchDto) {
        String trackingNumber = logisticOrderSearchDto.getTrackingNumber();
        TrackResponseForYanWen trackResponseForYanWen = yanWenLogisticUtil.getLogisticTrack(Collections.singletonList(trackingNumber));
        Boolean isSuccess = trackResponseForYanWen.getMessage().equals("success");
        List<TrackPointDto> trackPointDtoList = new ArrayList<>();

        List<TrackResponseForYanWen.Track> trackList = Optional.ofNullable(trackResponseForYanWen.getResult()).orElse(new ArrayList<>());
        trackList.stream().forEach(track -> {
            List<TrackResponseForYanWen.Checkpoint> checkpointList = track.getCheckpoints();

            checkpointList.forEach(checkpoint -> {
                TrackPointDto trackPointDto = TrackPointDto.builder()
                        .status(checkpoint.getTracking_status())
                        .location(checkpoint.getLocation())
                        .content(checkpoint.getMessage())
                        .timeZone(checkpoint.getTime_zone())
                        .eventTime(checkpoint.getTime_stamp())
                        .build();
                trackPointDtoList.add(trackPointDto);
            });
        });

        LogisticTrackResponseDto logisticTrackResponseDto = new LogisticTrackResponseDto();
        logisticTrackResponseDto.setMessage(trackResponseForYanWen.getMessage());
        logisticTrackResponseDto.setSuccessSign(isSuccess);
        logisticTrackResponseDto.setTrackPointDtos(trackPointDtoList);
        return logisticTrackResponseDto;
    }

    @Override
    public LogisticOrderResponseDto addLogisticOrder(LogisticOrderDto logisticOrderDto) {
        String userId = yanWenLogisticUtil.getYanWenConfig().getUserId();
        LogisticOrderForYanWen.Receiver receiver = this.getReceiver(userId, logisticOrderDto);      //收件人
        LogisticOrderForYanWen.GoodsName goodsName = this.getGoodsName(userId, logisticOrderDto);

        LogisticOrderForYanWen orderForYanWen = LogisticOrderForYanWen.builder()
                .Userid(userId)
                .Channel(logisticOrderDto.getChannel())
                .UserOrderNumber(logisticOrderDto.getLogisticOrderNo())
                .SendDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"))
                .Receiver(receiver)
                .Quantity(logisticOrderDto.getWaybillGoodDetailDtos().size())
                .GoodsName(goodsName)
                .build();

        LogisticOrderResponseForYanWen logisticOrderResponseForYanWen = yanWenLogisticUtil.addLogisticOrder(orderForYanWen);
        LogisticOrderResponseForYanWen.CreatedExpress createdExpress = logisticOrderResponseForYanWen.getCreatedExpress();
        LogisticOrderResponseDto logisticOrderResponseDto = new LogisticOrderResponseDto();
        this.convertResponse(logisticOrderResponseForYanWen, logisticOrderResponseDto);
        logisticOrderResponseDto.setLogisticsOrderNo(createdExpress.getUserOrderNumber());
//        logisticOrderResponseDto.setTrackingNumber(createdExpress.getYanwenNumber());
        logisticOrderResponseDto.setTrackingNumber(createdExpress.getEpcode()); // 燕文追踪号是快递单号
        logisticOrderResponseDto.setChannelTrackingNum(createdExpress.getYanwenNumber());
        logisticOrderResponseDto.setProcessCode(createdExpress.getYanwenNumber());
        logisticOrderResponseDto.setWaybillNo(createdExpress.getEpcode());
        return logisticOrderResponseDto;
    }

    @RetryAnnotation
    @Override
    public LogisticPrintLabelResponseDto getLogisticPrintLabel(LogisticOrderSearchDto logisticOrderSearchDto) {

        LogisticLabelForYanWen logisticLabelForYanWen = LogisticLabelForYanWen.builder()
                .epCode(logisticOrderSearchDto.getWayBillNo())
                .labelSize(logisticOrderSearchDto.getLabelSize())
                .build();
        LogisticLabelResponseForYanWen logisticLabelResponseForYanWen = yanWenLogisticUtil.getLogisticPrintLabel(logisticLabelForYanWen);
        String publicUrl = null;
        if(logisticLabelResponseForYanWen.getCallSuccess())
        {
            LogisticLabelResponseForYanWen.Response response = logisticLabelResponseForYanWen.getResponse();
            InputStream inputStream = response.getInputStream();
            String uniquePath = StringUtils.isNotBlank(logisticOrderSearchDto.getTrackingNumber()) ? logisticOrderSearchDto.getTrackingNumber() : snowflakeIdUtils.getId()+"";
            String filePathUri = LogisticProviderEnum.FULFILLMENT + LogisticProviderEnum.LOGISTIC + LogisticProviderEnum.YANWEN.getDirectory() + uniquePath + ".pdf";
            Map map = OSSUtils.uploadFileInputStream(filePathUri, inputStream);
            if(map.containsKey("public_url"))
            {
                publicUrl = map.get("public_url").toString();
            }
        }

        LogisticPrintLabelResponseDto logisticPrintLabelResponseDto = new LogisticPrintLabelResponseDto();
        this.convertResponse(logisticLabelResponseForYanWen, logisticPrintLabelResponseDto);

//        logisticPrintLabelResponseDto.setSourceDate(logisticLabelResponseForYanWen);
        logisticPrintLabelResponseDto.setLogisticsLabel(publicUrl);
//        logisticPrintLabelResponseDto.setSuccessSign(logisticLabelResponseForYanWen.getCallSuccess());
//        logisticPrintLabelResponseDto.setCode(logisticLabelResponseForYanWen.getResponse().getReason());
//        logisticPrintLabelResponseDto.setMessage(logisticLabelResponseForYanWen.getResponse().getReasonMessage());
//        logisticPrintLabelResponseDto.setError(logisticLabelResponseForYanWen.getResponse().getReasonMessage());
        return logisticPrintLabelResponseDto;
    }

    @Override
    public LogisticOrderDetailResponseDto getLogisticDetail(LogisticOrderSearchDto logisticOrderSearchDto) {
        LogisticDetailForYanWen logisticDetailForYanWen = new LogisticDetailForYanWen();
        logisticDetailForYanWen.setCode(logisticOrderSearchDto.getLogisticsOrderNo());
        LogisticDetailResponseForYanWen logisticDetailResponseForYanWen = yanWenLogisticUtil.getLogisticList(logisticDetailForYanWen);
        Integer TotalRecordCount = logisticDetailResponseForYanWen.getTotalRecordCount();
        LogisticOrderDetailResponseDto logisticOrderDetailResponseDto = new LogisticOrderDetailResponseDto();
        logisticOrderDetailResponseDto.setSourceDate(logisticDetailResponseForYanWen);
        logisticOrderDetailResponseDto.setLogsiticCode(getLogisticCode());
        if(TotalRecordCount == 0)
        {
            logisticOrderDetailResponseDto.setSuccessSign(Boolean.FALSE);
            logisticOrderDetailResponseDto.setMessage("查询物流信息失败");
            logisticOrderDetailResponseDto.setCode("");
            logisticOrderDetailResponseDto.setError("查询物流信息失败");
            logisticOrderDetailResponseDto.setStatus("");

            return logisticOrderDetailResponseDto;
        }

        LogisticDetailResponseForYanWen.ExpressCollection expressCollection = logisticDetailResponseForYanWen.getExpressCollection();
        List<LogisticDetailResponseForYanWen.ExpressType> expressTypeList = expressCollection.getExpressType();
        LogisticDetailResponseForYanWen.ExpressType expressType = expressTypeList.get(0);
        logisticOrderDetailResponseDto.setSuccessSign(Boolean.TRUE);
        logisticOrderDetailResponseDto.setTrackingNumber(expressType.getYanwenNumber());
        logisticOrderDetailResponseDto.setChannelTrackingNumber(expressType.getYanwenNumber());
        return logisticOrderDetailResponseDto;
    }

    private LogisticOrderForYanWen.Receiver getReceiver(String userId, LogisticOrderDto logisticOrderDto)
    {
        AddressDto receiverAddress = logisticOrderDto.getReceiverAddress();
        LogisticOrderForYanWen.Receiver receiver = LogisticOrderForYanWen.Receiver.builder()
                .Userid(userId)
                .Name(receiverAddress.getFirstName() + " " + receiverAddress.getLastName())
                .Phone(receiverAddress.getTel())
                .Email(receiverAddress.getEmail())
                .Country(receiverAddress.getCountryCode())
                .Postcode(receiverAddress.getPostCode())
                .State(receiverAddress.getProvince())
                .City(receiverAddress.getCity())
                .Address1(receiverAddress.getStreet1())
                .Address2(receiverAddress.getStreet2())
                .NationalId(receiverAddress.getTaxNumber())
                .build();
        return receiver;
    }


    private LogisticOrderForYanWen.GoodsName getGoodsName(String userId, LogisticOrderDto logisticOrderDto)
    {
        List<WaybillGoodDetailDto> waybillGoodDetailDtoList = logisticOrderDto.getWaybillGoodDetailDtos();
        return LogisticOrderForYanWen.GoodsName.builder()
                .Userid(userId)
                .Weight(waybillGoodDetailDtoList.stream().map(WaybillGoodDetailDto::getWeight).reduce(BigDecimal.ZERO,BigDecimal::add).intValue())
                .DeclaredValue(waybillGoodDetailDtoList.stream().map(WaybillGoodDetailDto::getDeclaredValue).reduce(BigDecimal.ZERO,BigDecimal::add))
                .DeclaredCurrency("USD")
                .NameCh(waybillGoodDetailDtoList.stream().map(WaybillGoodDetailDto::getDeclaredNameCn).collect(Collectors.joining(",")))
                .NameEn(waybillGoodDetailDtoList.stream().map(WaybillGoodDetailDto::getDeclaredNameEn).collect(Collectors.joining(",")))
                .build();
    }

    private void convertResponse(AbstractResponseYanWen abstractResponseYanWen, AbstractLogisticResponse abstractLogisticResponse)
    {
        AbstractResponseYanWen.Response response = abstractResponseYanWen.getResponse();
        abstractLogisticResponse.setSourceDate(abstractResponseYanWen);
        abstractLogisticResponse.setLogsiticCode(getLogisticCode());
        abstractLogisticResponse.setSuccessSign(abstractResponseYanWen.getCallSuccess());
        abstractLogisticResponse.setMessage(response.getReasonMessage());
        abstractLogisticResponse.setCode(response.getReason());
        abstractLogisticResponse.setError(response.getReasonMessage());
        abstractLogisticResponse.setStatus(response.getOperation());
    }
}
