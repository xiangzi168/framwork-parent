package com.amg.fulfillment.cloud.logistics.api.dto.logistic.px4;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.Data;

/**
 * Created by Seraph on 2021/5/21
 */

@Data
@Builder
public class LogisticOrderInfoSearchFor4PX {

    @JSONField(name = "request_no")
    private String requestNo;      //请求单号
    @JSONField(name = "start_time_of_create_consignment")
    private String startTimeOfCreateConsignment;        //委托单创建时间-开始时间（*注：时间格式的传入值需要转换为long类型格式。）时间差为7天
    @JSONField(name = "end_time_of_create_consignment")
    private String endTimeOfCreateConsignment;      //委托单创建时间-结束时间（（*注：时间格式的传入值需要转换为long类型格式。） 时间差为7天
    @JSONField(name = "consignment_status")
    private String consignmentStatus;      //委托单状态：已预报：P；已交接/已交货：V；库内作业中/已入库：H；已出库：C；已关闭：X；所有：ALL（默认）
}
