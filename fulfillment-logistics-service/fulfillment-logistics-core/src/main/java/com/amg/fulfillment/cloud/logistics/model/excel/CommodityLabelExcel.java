package com.amg.fulfillment.cloud.logistics.model.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName CommodityLabelExcel
 * @Description 物流标签
 * @Author 35112
 * @Date 2021/8/31 14:47
 **/
@Data
public class CommodityLabelExcel {
    @NotNull
    @Excel(name = "SKU", orderNum = "0")
    private Long sku;
    @NotNull
    @Excel(name = "是否带电", orderNum = "1")
    private String electricity;
    @NotNull
    @Excel(name = "是否液体", orderNum = "2")
    private String fluid;
    @NotNull
    @Excel(name = "是否活动商品C项链", orderNum = "3")
    private String activity3;
    @NotNull
    @Excel(name = "是否活动商品B耳环", orderNum = "4")
    private String activity2;
    @NotNull
    @Excel(name = "是否活动商品A手表", orderNum = "5")
    private String activity1;
    @NotNull
    @Excel(name = "是否粉末", orderNum = "6")
    private String powder;
    @NotNull
    @Excel(name = "是否膏体", orderNum = "7")
    private String paste;
    @NotNull
    @Excel(name = "是否服装", orderNum = "8")
    private String clothing;
    @NotNull
    @Excel(name = "是否低价引流品", orderNum = "9")
    private String benefits;
    @NotNull
    @Excel(name = "错误信息", orderNum = "10")
    private String errorMsg;

}