package com.amg.fulfillment.cloud.logistics.dto.logistic;

import lombok.Data;


/**
 * Created by Seraph on 2021/4/29
 */

@Data
public class LogisticPrintLabelResponseDto extends AbstractLogisticResponse {

    private String labelBarcode;        //面单条码
    private String logisticsLabel;     //面单链接
//    private String customLabel;        //报关标签链接
//    private String packageLabel;       //配货标签链接
//    private String invoiceLabel;       //DHL发票链接

}
