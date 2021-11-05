package com.amg.fulfillment.cloud.logistics.model.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * Created by Seraph on 2021/5/26
 */

@Data
public class LogisticsLabelImportExcel {

    //类目 code
    @Excel(name = "类目 code", orderNum = "0")
    private String categoryCode;
}
