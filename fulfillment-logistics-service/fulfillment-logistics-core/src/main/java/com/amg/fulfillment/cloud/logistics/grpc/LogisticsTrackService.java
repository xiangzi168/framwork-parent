package com.amg.fulfillment.cloud.logistics.grpc;

import com.alibaba.fastjson.JSON;
import com.amg.framework.cloud.grpc.utils.GrpcJsonFormatUtils;
import com.amg.fulfillment.cloud.logistics.api.grpc.LogisticsTrackGrpc;
import com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackGTO;
import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticOrderSearchDto;
import com.amg.fulfillment.cloud.logistics.dto.logistic.LogisticTrackResponseDto;
import com.amg.fulfillment.cloud.logistics.dto.logistic.TrackPointDto;
import com.amg.fulfillment.cloud.logistics.service.ILogisticService;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by Seraph on 2021/4/28
 */
@Slf4j
@GrpcService
public class LogisticsTrackService extends LogisticsTrackGrpc.LogisticsTrackImplBase {

    @Autowired
    private ILogisticService logisticService;

    @Override
    public List<LogisticsTrackGTO.LogisticsTrackResponse> getLogisticsTrack(LogisticsTrackGTO.LogisticsTrackRequest request,
                                                                            StreamObserver<LogisticsTrackGTO.LogisticsTrackResponseResult> responseObserver)
    {
        String requestStr = GrpcJsonFormatUtils.toJsonString(request);
        log.info("GRPC--远程调用物流轨迹的接口请求参数转成JSON是：{}", requestStr);
        LogisticOrderSearchDto logisticOrderSearchDto = JSON.parseObject(requestStr, LogisticOrderSearchDto.class);
        LogisticTrackResponseDto logisticTrackResponseDto = logisticService.getLogisticTrack(logisticOrderSearchDto);
        log.info("GRPC--远程调用物流轨迹返回信息是：{}", JSON.toJSONString(logisticTrackResponseDto));
//        if (!logisticTrackResponseDto.isSuccessSign()) {
//            String message = "code:" + logisticTrackResponseDto.getCode() + "msg:" + logisticTrackResponseDto.getMessage() + "," + logisticTrackResponseDto.getError();
//            throw new GlobalException(ExceptionEnum.LOGISTIC_REPONSE_EXCEPTION.getCode(), message);
//        }
        List<LogisticsTrackGTO.LogisticsTrackResponse> logisticsTrackResponseList = new ArrayList<>();
        List<TrackPointDto> trackPointDtoList = logisticTrackResponseDto.getTrackPointDtos();
        // 排序
        Collections.sort(trackPointDtoList,((o1, o2) -> o2.getEventTime().compareTo(o1.getEventTime())));
        trackPointDtoList.forEach(trackPointDto -> {
            LogisticsTrackGTO.LogisticsTrackResponse logisticsTrackResponse = LogisticsTrackGTO.LogisticsTrackResponse.newBuilder()
                    .setCreatTime(Optional.ofNullable(trackPointDto.getEventTime()).orElse(""))
                    .setTimeZone(Optional.ofNullable(trackPointDto.getTimeZone()).orElse(""))
                    .setStatus(Optional.ofNullable(trackPointDto.getStatus()).orElse(""))
                    .setContent(Optional.ofNullable(trackPointDto.getContent()).orElse(""))
                    .setLocation(Optional.ofNullable(trackPointDto.getLocation()).orElse(""))
                    .build();

            logisticsTrackResponseList.add(logisticsTrackResponse);
        });
        return logisticsTrackResponseList;
    }
}
