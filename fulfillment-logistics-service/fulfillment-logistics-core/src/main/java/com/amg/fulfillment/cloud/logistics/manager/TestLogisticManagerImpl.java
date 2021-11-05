package com.amg.fulfillment.cloud.logistics.manager;

import com.alibaba.fastjson.JSONObject;
import com.amg.fulfillment.cloud.logistics.api.dto.logistic.Wanb.AbstractResponseWanb;
import com.amg.fulfillment.cloud.logistics.api.dto.logistic.Wanb.TrackResponseForWanb;
import com.amg.fulfillment.cloud.logistics.api.dto.logistic.px4.AbstractResponseFor4PX;
import com.amg.fulfillment.cloud.logistics.api.dto.logistic.px4.LogisticOrderResponseFor4PX;
import com.amg.fulfillment.cloud.logistics.api.dto.logistic.px4.TrackResponseFor4PX;
import com.amg.fulfillment.cloud.logistics.api.dto.logistic.yanwen.TrackResponseForYanWen;
import com.amg.fulfillment.cloud.logistics.api.dto.logistic.yuntu.AbstractResponseForYunTu;
import com.amg.fulfillment.cloud.logistics.api.dto.logistic.yuntu.TrackResponseForYunTu;
import com.amg.fulfillment.cloud.logistics.dto.logistic.*;
import com.amg.fulfillment.cloud.logistics.enumeration.LogisticTypeEnum;
import com.amg.fulfillment.cloud.logistics.factory.LogisticFactory;
import com.amg.fulfillment.cloud.logistics.model.req.MockLogisticsTrackReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("testLogisticManagerImpl")
public class TestLogisticManagerImpl implements ILogisticManager {

    private ThreadLocal<MockLogisticsTrackReq> threadLocal = new ThreadLocal<>();
    @Autowired
    private LogisticFactory logisticFactory;

    private static final String SUCCESS_CODE = "0000";

    @Override
    public List<String> getLogisticReceivedStatusList() {
        MockLogisticsTrackReq logisticsTrackReq = getThreadLocal().get();
        String code = logisticsTrackReq.getCode();
        ILogisticManager LogisticManager = logisticFactory.getLogisticTypeFromCode(code);
        return LogisticManager.getLogisticReceivedStatusList();
    }

    @Override
    public List<String> getLogisticTrackEndStatusList() {
        MockLogisticsTrackReq logisticsTrackReq = getThreadLocal().get();
        String code = logisticsTrackReq.getCode();
        ILogisticManager LogisticManager = logisticFactory.getLogisticTypeFromCode(code);
        return LogisticManager.getLogisticTrackEndStatusList();
    }

    @Override
    public String getLogisticCode() {
        return null;
    }

    @Override
    public LogisticTrackResponseDto getLogisticTrack(LogisticOrderSearchDto logisticOrderSearchDto) {
        MockLogisticsTrackReq logisticsTrackReq = getThreadLocal().get();
        String code = logisticsTrackReq.getCode();
        if (LogisticTypeEnum.WANB.getCode().equals(code)) {
            return getWanBTrack(logisticsTrackReq);
        } else if (LogisticTypeEnum.YUNTU.getCode().equals(code)) {
            return getYunTuTrack(logisticsTrackReq);
        } else if (LogisticTypeEnum.YANWEN.getCode().equals(code)) {
            return getYanWenTrack(logisticsTrackReq);
        } else if (LogisticTypeEnum.PX4.getCode().equals(code)) {
            return getPX4Track(logisticsTrackReq);
        }
        LogisticTrackResponseDto responseDto = new LogisticTrackResponseDto();
        responseDto.setTrackPointDtos(Collections.EMPTY_LIST);
        return responseDto;
    }

    private LogisticTrackResponseDto getWanBTrack(MockLogisticsTrackReq logisticsTrackReq) {
        TrackResponseForWanb trackResponseForWanb = logisticsTrackReq.getTrackResponseForWanb();
        LogisticTrackResponseDto trackResponseDto = new LogisticTrackResponseDto();
        this.convertResponseForWanB(trackResponseForWanb, trackResponseDto);
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
        trackResponseDto.setSuccessSign(true);
        return trackResponseDto;
    }

    private void convertResponseForWanB(AbstractResponseWanb abstractResponseWanb, AbstractLogisticResponse abstractLogisticResponse) {
        AbstractResponseWanb.Error error = Optional.ofNullable(abstractResponseWanb.getError()).orElse(new AbstractResponseWanb.Error());
        abstractLogisticResponse.setSourceDate(abstractResponseWanb);
        abstractLogisticResponse.setLogsiticCode(getLogisticCode());
        abstractLogisticResponse.setSuccessSign(abstractResponseWanb.getSucceeded());
        abstractLogisticResponse.setMessage(error.getMessage());
        abstractLogisticResponse.setCode(error.getCode());
        abstractLogisticResponse.setError(JSONObject.toJSONString(error));
    }

    private LogisticTrackResponseDto getPX4Track(MockLogisticsTrackReq logisticsTrackReq) {

        TrackResponseFor4PX trackResponseFor4PX = logisticsTrackReq.getTrackResponseFor4PX();
        LogisticTrackResponseDto responseDto = new LogisticTrackResponseDto();
        this.convertResponseFor4PX(trackResponseFor4PX, responseDto);

        List<TrackPointDto> trackPointDtoList = new ArrayList<>();
        TrackResponseFor4PX.TrackDetail data = trackResponseFor4PX.getData();
        if(data != null)
        {
            List<TrackResponseFor4PX.Tracking> trackingList = Optional.ofNullable(data.getTrackingList()).orElse(new ArrayList<>());
            trackPointDtoList.addAll(trackingList.stream().map(item -> {
                return TrackPointDto.builder()
                        .content(item.getTrackingContent())
                        .location(item.getOccurLocation())
                        .status(item.getBusinessLinkCode())
                        .eventTime(item.getOccurDatetime())
                        .build();
            }).collect(Collectors.toList()));
        }
        responseDto.setTrackPointDtos(trackPointDtoList);
        responseDto.setSuccessSign(true);
        return responseDto;
    }

    private void convertResponseFor4PX(AbstractResponseFor4PX abstractResponseFor4PX, AbstractLogisticResponse abstractLogisticResponse) {
        abstractLogisticResponse.setSourceDate(abstractResponseFor4PX);
        abstractLogisticResponse.setLogsiticCode(getLogisticCode());
        abstractLogisticResponse.setSuccessSign(LogisticOrderResponseFor4PX.RESULT_SUCCESS.equals(abstractResponseFor4PX.getResult()) ? Boolean.TRUE : Boolean.FALSE);
        abstractLogisticResponse.setMessage(abstractResponseFor4PX.getMsg());
        abstractLogisticResponse.setCode(abstractResponseFor4PX.getResult());
        abstractLogisticResponse.setError(JSONObject.toJSONString(abstractResponseFor4PX.getErrors()));
    }

    private LogisticTrackResponseDto getYanWenTrack(MockLogisticsTrackReq logisticsTrackReq) {
        TrackResponseForYanWen trackResponseForYanWen = logisticsTrackReq.getTrackResponseForYanWen();
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

    private LogisticTrackResponseDto getYunTuTrack(MockLogisticsTrackReq logisticsTrackReq) {
        TrackResponseForYunTu trackResponseForYunTu = logisticsTrackReq.getTrackResponseForYunTu();
        LogisticTrackResponseDto logisticTrackResponseDto = new LogisticTrackResponseDto();
        this.convertResponseForYunTU(trackResponseForYunTu, logisticTrackResponseDto);
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
        logisticTrackResponseDto.setSuccessSign(true);
        return logisticTrackResponseDto;
    }

    private void convertResponseForYunTU(AbstractResponseForYunTu abstractResponseForYunTu, AbstractLogisticResponse abstractLogisticResponse) {
        abstractLogisticResponse.setSourceDate(abstractResponseForYunTu);
        abstractLogisticResponse.setLogsiticCode(getLogisticCode());
        abstractLogisticResponse.setSuccessSign(SUCCESS_CODE.equals(abstractResponseForYunTu.getCode()) ? Boolean.TRUE : Boolean.FALSE);
        abstractLogisticResponse.setCode(abstractResponseForYunTu.getCode());
        abstractLogisticResponse.setMessage(abstractResponseForYunTu.getMessage());
    }

    @Override
    public LogisticOrderResponseDto addLogisticOrder(LogisticOrderDto logisticOrderDto) {
        return null;
    }

    @Override
    public LogisticPrintLabelResponseDto getLogisticPrintLabel(LogisticOrderSearchDto logisticOrderSearchDto) {
        return null;
    }

    @Override
    public LogisticOrderDetailResponseDto getLogisticDetail(LogisticOrderSearchDto logisticOrderSearchDto) {
        return null;
    }

    public ThreadLocal<MockLogisticsTrackReq> getThreadLocal() {
        return threadLocal;
    }

    public void setContent(MockLogisticsTrackReq req) {
        threadLocal.set(req);
    }


}
