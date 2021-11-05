package com.amg.fulfillment.cloud.logistics.dto.logistic;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Tom
 * @date 2021-04-19-19:07
 */
@Data
public class WaybillGoodDetailDto {
    @ApiModelProperty(value = "item Id")
    private String itemId;
    @ApiModelProperty(value = "货物编号 sku")
    private String goodsId;
    @ApiModelProperty(value = "货物描述")
    private String goodsTitle;
    @ApiModelProperty(value = "英文申报名称")
    private String declaredNameEn;
    @ApiModelProperty(value = "中文申报名称")
    private String declaredNameCn;
    @ApiModelProperty(value = "单件申报价值")
    private BigDecimal declaredValue;
    @ApiModelProperty(value = "单件重量")
    private BigDecimal weight;
    @ApiModelProperty(value = "sku 图片")
    private String img;
    @ApiModelProperty(value = "sku 属性")
    private String attribute;
    @ApiModelProperty(value = "采购 id")
    private String purchaseId;
    @ApiModelProperty(value = "类目 code")
    private String categoryCode;
    @ApiModelProperty(value = "商品所包含的类目 code")
    private List<String> categoryCodeList;
    @ApiModelProperty(value = "商品所包含的标签")
    private List<Long> labelIdList;
    @ApiModelProperty(value = "商品对应的标签<特别备注：labelIdList包含集合内所有的标签信息，该属性只包含对应商品标签>")
    private List<Long> relevanceLabelIdList;
    @ApiModelProperty(value = "销售价格（元）")
    private BigDecimal salePriceCny;
}
