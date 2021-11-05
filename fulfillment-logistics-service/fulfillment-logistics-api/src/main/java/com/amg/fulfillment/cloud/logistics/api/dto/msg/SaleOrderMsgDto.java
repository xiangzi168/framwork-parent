package com.amg.fulfillment.cloud.logistics.api.dto.msg;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.api.dto.depository.ProductVariant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tom
 * @date 2021-05-13-11:09
 */
@Data
@Slf4j
public class SaleOrderMsgDto {
    @NotBlank(message = "销售订单号 不能为null")
    @ApiModelProperty(name = "销售订单号")
    private String saleOrderId;
    private String depositoryCode = Constant.DEPOSITORY_WB;
    private String depositoryName = Constant.DEPOSITORY_WB_NAME;
    private String remark;
    @NotBlank(message = "产品列表 不能为null")
    @ApiModelProperty(name = "产品列表")
    private List<Product> products;

    @Data
    public static class Product {

        @NotBlank(message = "name 不能为null")
        @ApiModelProperty(name = "产品名称")
        private String productName;
        @NotBlank(message = "sku 不能为null")
        @ApiModelProperty(name = "sku")
        private String sku;
        @NotBlank(message = "spu 不能为null")
        @ApiModelProperty(name = "spu")
        private String spu;
        @NotBlank(message = "itemId 不能为null")
        @ApiModelProperty(name = "itemId")
        private String itemId;
        @NotBlank(message = "purchaseId 不能为null")
        @ApiModelProperty(name = "purchaseId")
        private String purchaseId;
        @NotBlank(message = "pattern 不能为null")
        @ApiModelProperty(name = "skuChannel  1:临采1688、淘宝 2：AE 3:备货")
        private Integer skuChannel;
        @ApiModelProperty(name = "检测类型")
        private List<String> checkTypes;
        @NotBlank(message = "quantity 不能为null")
        @ApiModelProperty(name = "quantity 预计收货总数量")
        private Integer quantity;
        @ApiModelProperty(name = "产品属性")
        private List<ProductVariant> variants;
        @ApiModelProperty(name = "产品属性字符串标识")
        private String variantStr;
        @ApiModelProperty(name = "图⽚地址 jpg 或者 png 格式图⽚地址不⽀持 WebP 等非常规格式")
        private List<String> imageUrls;
        @ApiModelProperty(name = "重量")
        private Double weightInKg;


        public List<ProductVariant> tranVariantstrToObj(String variantStr) {
            if (!StringUtils.isBlank(variantStr)) {
                try{
                    JSONObject jsonObject = JSON.parseObject(variantStr);
                    List<ProductVariant> variantList = jsonObject.entrySet().stream().map(entity -> {
                        return new ProductVariant(entity.getKey(), String.valueOf(entity.getValue()));
                    }).collect(Collectors.toList());
                    return variantList;
                }catch (Exception e) {
                    log.error("解析字符串variantStr失败，失败内容是：{}，原因：{}", variantStr, e);
                }
            }
            return Collections.EMPTY_LIST;
        }
    }
}
