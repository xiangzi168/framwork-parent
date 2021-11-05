package com.amg.fulfillment.cloud.logistics.model.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Seraph on 2021/5/12
 */

@Data
public class BaseReq implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startTime;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @Min(value = 1, message = "页码最小值1")
    @ApiModelProperty(value = "页码", required = true)
    private long page = 1;

    @Min(value = 1, message = "请求行数最小值1")
    @Max(value = 200, message = "请求行数最大值200")
    @ApiModelProperty(value = "请求行数", required = true)
    private long row = 20;

    @ApiModelProperty(value = "请求 id List")
    private List<String> idList;
}
