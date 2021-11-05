package com.amg.fulfillment.cloud.logistics.model.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.amg.fulfillment.cloud.logistics.model.vo.PurchasePackageProductVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;


/**
 * Created by Seraph on 2021/5/26
 */

@Data
public class PurchasePackageExcel {
    @Excel(name = "包裹单号")
    private String packageNo;
    @Excel(name = "状态")
    private String status;
    @Excel(name = "异常状态")
    private String label;
    @Excel(name = "是否已预报")
    private String prediction;
    @Excel(name = "是否已入库")
    private String warehousing;
    @Excel(name = "快递名称")
    private String expressCompanyName;
    @Excel(name = "快递单号")
    private String expressBillNo;
    @Excel(name = "渠道订单号")
    private String channelOrderId;
    @Excel(name = "销售订单号")
    private String saleOrder;
    @Excel(name = "采购订单号")
    private String purchaseIds;
    @Excel(name = "创建时间")
    private String createTime;
    @Excel(name = "入库时间")
    private String receivingGoodTime;
    @Excel(name ="揽收时间")
    private String acceptTime;
    @Excel(name ="签收时间")
    private String signTime;
    @Excel(name ="物流状态")
    private String logisticsStatus;
    @Excel(name="添加包裹方式")
    private String typeAdd;
    // 包裹源头类型 标明在哪里加入的包裹 5拼多多 6淘宝 1 1688
    @Excel(name="包裹源头")
    private String packageSourceType;

}
