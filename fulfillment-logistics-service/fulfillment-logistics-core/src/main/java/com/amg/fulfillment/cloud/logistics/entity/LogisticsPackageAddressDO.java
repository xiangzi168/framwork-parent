package com.amg.fulfillment.cloud.logistics.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Created by Seraph on 2021/5/27
 */

@Data
@TableName(value = "t_logistics_package_address")
public class LogisticsPackageAddressDO extends BaseDO {
    private static final long serialVersionUID = 1L;
    @TableId
    private Long id;
    @TableField(value = "package_id")
    private Long packageId;
    @TableField(value = "first_name")
    private String firstName;
    @TableField(value = "last_name")
    private String lastName;
    private String company;
    @TableField(value = "country_code")
    private String countryCode;
    private String province;
    private String city;
    private String street1;
    private String street2;
    @TableField(value = "post_code")
    private String postCode;
    private String tel;
    private String email;
    @TableField(value = "tax_number")
    private String taxNumber;
}
