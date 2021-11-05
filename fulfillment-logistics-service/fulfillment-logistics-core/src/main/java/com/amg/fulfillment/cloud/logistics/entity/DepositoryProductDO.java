package com.amg.fulfillment.cloud.logistics.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.base.Objects;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author zzx
 * @since 2021-06-15
 */
@TableName("t_depository_product")
@ApiModel(value="DepositoryProductDO对象", description="")
public class DepositoryProductDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "渠道sku")
    private String channelSku;

    @ApiModelProperty(value = "商品名")
    private String name;

    @ApiModelProperty(value = "属性")
    private String variants;

    @ApiModelProperty(value = "期望重量（g）")
    private BigDecimal expireWeight;

    @ApiModelProperty(value = "实际重量（g）")
    private BigDecimal realWeight;

    @ApiModelProperty(value = "图片")
    private String images;

    private Date createTime;

    private String createBy;

    private Date updateTime;

    private String updateBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
    public String getChannelSku() {
        return channelSku;
    }

    public void setChannelSku(String channelSku) {
        this.channelSku = channelSku;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getVariants() {
        return variants;
    }

    public void setVariants(String variants) {
        this.variants = variants;
    }
    public BigDecimal getExpireWeight() {
        return expireWeight;
    }

    public void setExpireWeight(BigDecimal expireWeight) {
        this.expireWeight = expireWeight;
    }
    public BigDecimal getRealWeight() {
        return realWeight;
    }

    public void setRealWeight(BigDecimal realWeight) {
        this.realWeight = realWeight;
    }
    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    @Override
    public String toString() {
        return "DepositoryProductDO{" +
        "id=" + id +
        ", sku=" + sku +
        ", channelSku=" + channelSku +
        ", name=" + name +
        ", variants=" + variants +
        ", expireWeight=" + expireWeight +
        ", realWeight=" + realWeight +
        ", images=" + images +
        ", createTime=" + createTime +
        ", createBy=" + createBy +
        ", updateTime=" + updateTime +
        ", updateBy=" + updateBy +
        "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DepositoryProductDO that = (DepositoryProductDO) o;
        return Objects.equal(sku, that.sku) && Objects.equal(name, that.name) && Objects.equal(variants, that.variants) && Objects.equal(expireWeight, that.expireWeight);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sku, name, variants, expireWeight);
    }
}
