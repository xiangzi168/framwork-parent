package com.amg.fulfillment.cloud.logistics.dto.depository;

import com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb.SaleOrderResponseMsgForWanB;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Tom
 * @date 2021-04-29-18:19
 */
@Data
public class SaleOrderResultDto {
    private String depositoryCode;
    private String saleOrderId;
    private String accountNo;
    private List<SaleOrderResponseMsgForWanB.SalesOrderProduct> product;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createOn;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date cancelOn;
    private boolean successSign;
    private String remark;
    private Object sourceData;
    @ApiModelProperty(name = "错误消息")
    private String errorMsg;
    @ApiModelProperty(name = "错误码")
    private String errorCode;
    @ApiModelProperty(name = "系统错误消息")
    private String systemErrorMsg;
}
