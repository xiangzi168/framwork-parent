package com.amg.fulfillment.cloud.logistics.manager;

import com.amg.fulfillment.cloud.logistics.dto.logistic.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public interface ILogisticManager {

    default void getLastTrackPointDto(LogisticTrackResponseDto logisticTrackResponseDto) {
        List<TrackPointDto> trackPointDtoList = logisticTrackResponseDto.getTrackPointDtos();
        // 云途“成功签收”后会继续追加无用的轨迹
        List<TrackPointDto> pointDtoList = trackPointDtoList.stream().sorted(Comparator.comparing(TrackPointDto::getEventTime)).collect(Collectors.toList());
        if (!Objects.isNull(pointDtoList) && !pointDtoList.isEmpty()) {
            List<String> logisticTrackEndStatusList = this.getLogisticTrackEndStatusList();
            for (TrackPointDto trackPointDto : pointDtoList) {
                if (StringUtils.isNotBlank(trackPointDto.getStatus())) {
                    logisticTrackResponseDto.setLastNode(trackPointDto.getStatus()); //设置节点
                }
                if (logisticTrackEndStatusList.contains(trackPointDto.getStatus())) {
                    logisticTrackResponseDto.setIsEnd(Boolean.TRUE);        //当前为终态节点
                    logisticTrackResponseDto.setReceiveGoodTime(trackPointDto.getEventTime());
                    break;
                }
            }
        }
    }

    default LogisticTrackResponseDto getLogisticTrackList(LogisticOrderSearchDto logisticOrderSearchDto) {
        LogisticTrackResponseDto logisticTrackResponseDto = getLogisticTrack(logisticOrderSearchDto);
        this.getLastTrackPointDto(logisticTrackResponseDto);
        return logisticTrackResponseDto;
    }

    /**
     * 判断是否揽收，如果是揽收则
     * @auth  qiuhao
     * @param logisticTrackResponseDto
     * @return
     */
    default List<TrackPointDto> getLogisticCompanyReceivedPackage(LogisticTrackResponseDto logisticTrackResponseDto) {
        List<TrackPointDto> trackPointDtos = logisticTrackResponseDto.getTrackPointDtos();
        List<TrackPointDto> trackPointList = trackPointDtos.stream().sorted(Comparator.comparing(TrackPointDto::getEventTime)).collect(Collectors.toList());
       // boolean match = trackPointList.stream().map(TrackPointDto::getStatus).anyMatch(status -> getLogisticReceivedStatusList().contains(status));
        return trackPointList.stream().filter(trackPointDto -> getLogisticReceivedStatusList().contains(trackPointDto.getStatus())).collect(Collectors.toList());
    }

    List<String> getLogisticReceivedStatusList();

    List<String> getLogisticTrackEndStatusList();

    String getLogisticCode();

    LogisticTrackResponseDto getLogisticTrack(LogisticOrderSearchDto logisticOrderSearchDto);

    LogisticOrderResponseDto addLogisticOrder(LogisticOrderDto logisticOrderDto);

    LogisticPrintLabelResponseDto getLogisticPrintLabel(LogisticOrderSearchDto logisticOrderSearchDto);

    LogisticOrderDetailResponseDto getLogisticDetail(LogisticOrderSearchDto logisticOrderSearchDto);
}
