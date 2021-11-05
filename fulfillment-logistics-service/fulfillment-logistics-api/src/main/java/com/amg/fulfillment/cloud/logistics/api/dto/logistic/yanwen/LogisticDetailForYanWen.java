package com.amg.fulfillment.cloud.logistics.api.dto.logistic.yanwen;

import lombok.Data;

/**
 * Created by Seraph on 2021/5/28
 */

@Data
public class LogisticDetailForYanWen {

    private Integer page = 1;       //页码数
    private String code;        //运单号
    private String receiver;        //收货人姓名
    private String channel;     //发货方式
    private String start;       //开始时间	必填
    private String end;     //结束时间        必填
    private Integer isstatus = 1;       //快件状态。支持的值：1（代表正常），0（代表已删除）。
}
