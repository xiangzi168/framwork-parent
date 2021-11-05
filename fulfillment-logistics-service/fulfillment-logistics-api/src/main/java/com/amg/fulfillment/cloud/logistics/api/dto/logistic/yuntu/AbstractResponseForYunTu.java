package com.amg.fulfillment.cloud.logistics.api.dto.logistic.yuntu;

import cn.hutool.core.date.DateUtil;
import com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb.AbstractResponseMsg;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Tom
 * @date 2021-04-16-15:36
 */
@Getter
@Setter
public abstract class AbstractResponseForYunTu <T extends AbstractResponseForYunTu>{

    protected static final String SUCCESS_CODE_GENERAL = "0000";
    protected static final String ERROR_CODE_GENERAL="500";

    @ApiModelProperty(name = "结果代码")
    private String Code;
    @ApiModelProperty(name = "结果描述")
    private String Message;
    @ApiModelProperty(name = "请求唯一标识")
    private String RequestId;
    @ApiModelProperty(name = "时间戳")
    private String TimeStamp;

    public T fail500(String responseMessage){
        this.setCode(ERROR_CODE_GENERAL);
        this.setMessage(responseMessage);
        this.setTimeStamp(DateUtil.now());
        return (T)this;
    }

    public Boolean isSuccess()
    {
        return SUCCESS_CODE_GENERAL.equals(Code);
    }
}
