package com.amg.fulfillment.cloud.logistics.api.dto.logistic.px4;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.security.auth.login.FailedLoginException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Tom
 * @date 2021-04-08-17:30
 */
@Data
@ToString(callSuper = true)
public class LogisticOrderResponseFor4PX extends AbstractResponseFor4PX<LogisticOrderResponseFor4PX> {

    private OrderDetail data;

    @Data
    public static class OrderDetail {
        @ApiModelProperty(name = "")
        private String ds_consignment_no;
        @ApiModelProperty(name = "")
        @JSONField(name="4px_tracking_no")
        private String tracking_no;
        @ApiModelProperty(name = "")
        private String label_barcode;
        @ApiModelProperty(name = "")
        private String logistics_channel_name;
        @ApiModelProperty(name = "")
        private String ref_no;
        @ApiModelProperty(name = "")
        private String logistics_channel_no;
    }

    public OrderDetail getData() {
        if (data==null) {
            data=new OrderDetail();
        }
        return data;
    }
}