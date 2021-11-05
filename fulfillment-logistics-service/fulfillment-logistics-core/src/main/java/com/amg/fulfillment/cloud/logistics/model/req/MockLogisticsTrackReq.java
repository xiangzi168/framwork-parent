package com.amg.fulfillment.cloud.logistics.model.req;

import com.amg.fulfillment.cloud.logistics.api.dto.logistic.Wanb.TrackResponseForWanb;
import com.amg.fulfillment.cloud.logistics.api.dto.logistic.px4.TrackResponseFor4PX;
import com.amg.fulfillment.cloud.logistics.api.dto.logistic.yanwen.TrackResponseForYanWen;
import com.amg.fulfillment.cloud.logistics.api.dto.logistic.yuntu.TrackResponseForYunTu;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPackageDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@ApiModel(value = "MockLogisticsTrackReq")
@Data
public class MockLogisticsTrackReq {

    @NotBlank(message = "追踪号或运单号 不能为null")
    @ApiModelProperty(value = "追踪号或运单号",required = true)
    private String trackingNumber;
    @NotBlank(message = "logisticsNode 不能为null")
    @ApiModelProperty(value = "物流节点 不能为null",required = true)
    private String logisticsNode;
    @NotBlank(message = "logisticsContent 不能为null")
    @ApiModelProperty(value = "物流内容 不能为null",required = true)
    private String logisticsContent;

    @ApiModelProperty(value = "万邦",hidden = true)
    private TrackResponseForWanb trackResponseForWanb = new TrackResponseForWanb();
    @ApiModelProperty(value = "4px",hidden = true)
    private TrackResponseFor4PX trackResponseFor4PX = new TrackResponseFor4PX();
    @ApiModelProperty(value = "燕文",hidden = true)
    private TrackResponseForYanWen trackResponseForYanWen = new TrackResponseForYanWen();
    @ApiModelProperty(value = "云图",hidden = true)
    private TrackResponseForYunTu trackResponseForYunTu = new TrackResponseForYunTu();
    @ApiModelProperty(hidden = true)
    private LogisticsPackageDO logisticsPackageDO;
    @ApiModelProperty(value = "物流商 code ",hidden = true)
    private String code;
    @ApiModelProperty(hidden = true)
    private Date currentDate = new Date();

}
