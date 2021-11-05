package com.amg.fulfillment.cloud.logistics.api.dto.logistic.px4;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.Data;

/**
 * Created by Seraph on 2021/4/29
 */

@Data
@Builder
public class LogisticLabelSearchFor4PX {

    @JSONField(name = "request_no")
    private String requestNo;       //请求单号（支持4PX单号、客户单号和面单号）
    @JSONField(name = "response_label_format")
    private String responseLabelFormat;     //返回面单的格式（PDF：返回PDF下载链接；IMG：返回IMG图片下载链接） 默认为PDF；
    @JSONField(name = "label_size")
    private String labelSize;       //标签大小（label_80x90：标签纸80.5mm×90mm； label_90x100：标签纸85mm×98mm； label_100x100：标签纸98mm×98mm； label_100x150：标签纸100mm×150mm； label_100x200：标签纸100mm×200mm；）默认为label_80x90；
    @JSONField(name = "is_print_time")
    private String isPrintTime;     //是否打印当前时间（Y：打印；N：不打印） 默认为N；
    @JSONField(name = "is_print_buyer_id")
    private String isPrintBuyerId;      //是否打印买家ID（Y：打印；N：不打印） 默认为N；
    @JSONField(name = "is_print_pick_info")
    private String isPrintPickInfo;     //是否在标签上打印配货信息（Y：打印；N：不打印）；默认为N。 注：只对4PX通用标签/普通标签的控制有效；这里的配货信息指是否在标签上打印配货信息。若需单独打印配货单，使用create_package_label字段控制。
    @JSONField(name = "is_print_declaration_list")
    private String isPrintDeclarationList;      //是否打印报关单（Y：打印；N：不打印） 默认为N；
    @JSONField(name = "is_print_customer_weight")
    private String isOrintCustomerWeight;       //报关单上是否打印客户预报重（Y：打印；N：不打印） 默认为N。 注：针对单独打印报关单功能；
    @JSONField(name = "create_package_label")
    private String createPackageLabel;      //是否单独打印配货单（Y：打印；N：不打印） 默认为N。
    @JSONField(name = "is_print_pick_barcode")
    private String isPrintPickBarcode;      //配货单上是否打印配货条形码（Y：打印；N：不打印） 默认为N。 注：针对单独打印配货单功能；
    @JSONField(name = "is_print_merge")
    private String isPrintMerge;        //是否合并打印(Y：合并；N：不合并)默认为N； 注：合并打印，指若报关单和配货单打印为Y时，是否和标签合并到同一个URL进行返回
}
