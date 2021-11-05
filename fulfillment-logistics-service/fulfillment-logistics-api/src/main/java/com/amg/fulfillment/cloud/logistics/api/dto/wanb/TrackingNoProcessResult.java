package com.amg.fulfillment.cloud.logistics.api.dto.wanb;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * Created by Seraph on 2021/5/28
 */

@Data
public class TrackingNoProcessResult {

    @JSONField(name = "Code")
    private String code;        //Processing: 系统正在分配跟踪号     Success: 分配跟踪号成功        Error: 分配跟踪号失败
    @JSONField(name = "Message")
    private String message;     //消息文本
    @JSONField(name = "IsVirtual")
    private Boolean isVirtual;      //是否为虚拟单号
}
