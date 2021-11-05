package com.amg.fulfillment.cloud.logistics.api.dto.logistic.yanwen;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Tom
 * @date 2021-04-19-12:00
 */
@Data
public class TrackResponseForYanWen {

    @ApiModelProperty(name = "")
    private Integer code;
    @ApiModelProperty(name = "")
    private String message;
    @ApiModelProperty(name = "")
    private String requestTime;
    @ApiModelProperty(name = "")
    private ElapsedMilliseconds elapsedMilliseconds;
    @ApiModelProperty(name = "")
    private String ip;
    @ApiModelProperty(name = "")
    private List<Track> result;

    @Data
    public static class Track {
        @ApiModelProperty(name = "")
        private String tracking_number;
        @ApiModelProperty(name = "")
        private String waybill_number;
        @ApiModelProperty(name = "")
        private String exchange_number;
        @ApiModelProperty(name = "")
        private List<Checkpoint> checkpoints;
        @ApiModelProperty(name = "")
        private String tracking_status;
        @ApiModelProperty(name = "")
        private String last_mile_tracking_expected;
        @ApiModelProperty(name = "")
        private String origin_country;
        @ApiModelProperty(name = "")
        private String destination_country;

        public List<Checkpoint> getCheckpoints() {
            if (Objects.isNull(checkpoints)) {
                checkpoints = Collections.EMPTY_LIST;
            }
            return checkpoints;
        }
    }

    @Data
    public static class Checkpoint {
        @ApiModelProperty(name = "")
        private String time_stamp;
        @ApiModelProperty(name = "")
        private String time_zone;
        @ApiModelProperty(name = "")
        private String tracking_status;
        @ApiModelProperty(name = "")
        private String message;
        @ApiModelProperty(name = "")
        private String location;
    }

    @Data
    public static class ElapsedMilliseconds {
        private Integer total;
    }

    public static TrackResponseForYanWen fail500(String response) {
        TrackResponseForYanWen trackResponseForYanWen = new TrackResponseForYanWen();
        trackResponseForYanWen.setCode(0);
        trackResponseForYanWen.setMessage(response);
        return trackResponseForYanWen;
    }

}
