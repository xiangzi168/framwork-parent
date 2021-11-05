package com.amg.fulfillment.cloud.logistics.api.dto.wanb;

import com.alibaba.fastjson.annotation.JSONField;
import com.amg.fulfillment.cloud.logistics.api.dto.wanb.Money;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Created by Seraph on 2021/5/28
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParcelItemDetail {

    @JSONField(name = "GoodsId")
    private String goodsId;     //货物编号
    @JSONField(name = "GoodsTitle")
    private String goodsTitle;     //货物描述
    @JSONField(name = "DeclaredNameEn")
    private String declaredNameEn;     //英文申报名称
    @JSONField(name = "DeclaredNameCn")
    private String declaredNameCn;     //中文申报名称
    @JSONField(name = "DeclaredValue")
    private Money declaredValue;     //单件申报价值
    @JSONField(name = "WeightInKg")
    private BigDecimal weightInKg;     //单件重量(KG)
    @JSONField(name = "Quantity")
    private Integer quantity;     //件数
    @JSONField(name = "HSCode")
    private String hSCode;     //海关编码
    @JSONField(name = "CaseCode")
    private String caseCode;     //箱号(一票多件)
    @JSONField(name = "SalesUrl")
    private String salesUrl;     //销售平台链接
    @JSONField(name = "IsSensitive")
    private Boolean isSensitive;     //是否为敏感货物/带电/带磁等
    @JSONField(name = "Brand")
    private String brand;     //品牌
    @JSONField(name = "Model")
    private String model;     //型号
    @JSONField(name = "MaterialCn")
    private String materialCn;     //材质（中文）
    @JSONField(name = "MaterialEn")
    private String materialEn;     //材质（英文）
    @JSONField(name = "UsageCn")
    private String usageCn;     //用途（中文）
    @JSONField(name = "UsageEn")
    private String usageEn;     //用途（英文）
}
