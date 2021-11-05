package com.amg.fulfillment.cloud.logistics.api.dto.logistic.px4;

import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Tom
 * @date 2021-04-20-16:05
 */
@Data
public class TrackResponseFor4PX extends AbstractResponseFor4PX<TrackResponseFor4PX> {

    private TrackDetail data;

    @Data
    public static class TrackDetail {
        private String deliveryOrderNo;
        private List<Tracking> trackingList;
    }

    @Data
    public static class Tracking {
        private String businessLinkCode;        //	轨迹代码
        private String occurDatetime;       //轨迹发生时间
        private String occurLocation;       //轨迹发生地
        private String trackingContent;     //轨迹描述
        private String destinationCountry;      //目的地国家
    }

}
