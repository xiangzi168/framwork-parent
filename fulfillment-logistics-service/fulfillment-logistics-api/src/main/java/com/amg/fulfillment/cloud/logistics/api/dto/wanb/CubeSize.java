package com.amg.fulfillment.cloud.logistics.api.dto.wanb;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by Seraph on 2021/5/28
 */

@Data
public class CubeSize {

    @JSONField(name = "Height")
    private BigDecimal height;      //高
    @JSONField(name = "Length")
    private BigDecimal length;      //长
    @JSONField(name = "Width")
    private BigDecimal width;       //宽
    @JSONField(name = "Unit")
    private String unit;        //单位 CM=厘米, M=米
}
