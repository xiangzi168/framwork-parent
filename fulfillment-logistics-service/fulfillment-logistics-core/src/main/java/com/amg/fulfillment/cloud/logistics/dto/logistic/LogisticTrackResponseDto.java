package com.amg.fulfillment.cloud.logistics.dto.logistic;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Tom
 * @date 2021-04-16-15:46
 */
@ApiModel("物流轨迹响应对象")
@Data
@ToString(callSuper = true)
public class LogisticTrackResponseDto extends AbstractLogisticResponse {

    private Integer received;        //物流是否公司揽收 1:已揽收
    private String lastNode;        //最新物
    // 流节点
    private Boolean isEnd = Boolean.FALSE;       //是否终态
    private String receiveGoodTime;       //是否终态
    private String logisticTrackStatus;     //物流轨迹状态
    private List<TrackPointDto> trackPointDtos;     //物流详情


    public List<TrackPointDto> getTrackPointDtos() {
        if (Objects.isNull(trackPointDtos)) {
            return Collections.EMPTY_LIST;
        }
        return trackPointDtos;
    }
}
