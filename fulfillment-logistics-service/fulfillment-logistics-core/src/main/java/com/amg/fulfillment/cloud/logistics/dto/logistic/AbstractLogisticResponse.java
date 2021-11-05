package com.amg.fulfillment.cloud.logistics.dto.logistic;

import lombok.Data;

/**
 * Created by Seraph on 2021/5/21
 */

@Data
public abstract class AbstractLogisticResponse {
    private String logsiticCode;        //物流公司code
    private boolean successSign;        //是否成功标识
    private String code;        //物流商返回结果
    private String message;     //返回信息
    private Object sourceDate;      //源数据
    private String error;       //错误信息
    private String status;      //物流状态
}
