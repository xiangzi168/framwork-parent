package com.amg.fulfillment.cloud.logistics.api.dto.wanb;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * Created by Seraph on 2021/5/28
 */

@Data
public class Warehouse {

    @JSONField(name = "Code")
    private String code;        //仓库代码
    @JSONField(name = "Name")
    private String name;        //仓库名称
}
