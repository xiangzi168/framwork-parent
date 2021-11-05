package com.amg.fulfillment.cloud.logistics.api.dto.logistic.px4;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Created by Seraph on 2021/5/21
 */

@Data
public class LogisticOrderInfoResponseFor4PX extends AbstractResponseFor4PX<LogisticOrderInfoResponseFor4PX> {

    private String data;
    private List<OrderInfoDetail> dataDetail;

    public List<OrderInfoDetail> getDataDetail() {
        if (StringUtils.isBlank(data)) {
            return Collections.emptyList();
        }
        return JSONObject.parseArray(data,OrderInfoDetail.class);
    }

    @Data
    public static class OrderInfoDetail
    {
        @JSONField(name = "consignment_info")
        private ConsignmentInfo consignmentInfo;        //委托单信息
        @JSONField(name = "parcel_confirm_info")
        private ParcelConfirmInfo parcelConfirmInfo;        //订单信息
    }

    @Data
    public static class ConsignmentInfo
    {
        @JSONField(name = "ds_consignment_no")
        private String dsConsignmentNo;     //客户委托单号
        @JSONField(name = "4px_tracking_no")
        private String trackingNo;      //4PX单号
        @JSONField(name = "ref_no")
        private String refNo;       //客户单号
        @JSONField(name = "get_no_mode")
        private String getNoMode;       //获取末端服务商单号的方式(创建订单时取号：C；仓库作业时取号：U)
        @JSONField(name = "logistics_channel_no")
        private String logisticsChannelNo;      //末端服务商单号（(若此字段为空：①表示系统在异步取号中--再次查询即可获取；②取号失败--此时字段get_no_exmsg会返回相应的报错内容)）
        @JSONField(name = "get_no_exmsg")
        private String getNoExmsg;      //获取服务商单号抛的异常信息（若取号失败/取号异常，此字段将服务商的报错内容，同时logistics_channel_no字段将为空）
        @JSONField(name = "logistics_product_code")
        private String logisticsProductCode;        //运输方式代码
        @JSONField(name = "logistics_product_name")
        private String logisticsProductName;        //运输方式名称
        @JSONField(name = "consignment_status")
        private String consignmentStatus;       //委托单状态（草稿：D；已预报：P；已交接/已交货：V；库内作业中：H；已出库：C；已关闭：X；）
        @JSONField(name = "insure_status")
        private String insureStatus;        //投保状态（Y 已投保；N 未投保）
        @JSONField(name = "insure_type")
        private String insureType;      //投保类型
        @JSONField(name = "has_check_oda")
        private String hasCheckOda;     //是否进行ODA校验（Y：表示已经校验过；N：表示尚未校验）
        @JSONField(name = "oda_result_sign")
        private String odaResultSign;       //ODA标识(偏远地址：Y ；非偏远地址：N)
        @JSONField(name = "is_hold_sign")
        private String isHoldSign;      //拦截标识（申请拦截：Y ；拦截成功：S； 放行：N ）
        @JSONField(name = "consignment_create_date")
        private Long consignmentCreateDate;     //创建委托单时间（*注：时间格式的传入值需要转换为long类型格式。）
        @JSONField(name = "4px_inbound_date")
        private Long inboundDate;       //4PX收货时间（*注：时间格式的传入值需要转换为long类型格式。）
        @JSONField(name = "4px_outbound_date")
        private Long outboundDate;      //4PX出库时间（*注：时间格式的传入值需要转换为long类型格式。）
    }

    @Data
    public static class ParcelConfirmInfo
    {
        @JSONField(name = "confirm_parcel_qty")
        private String confirmParcelQty;        //订单的实际包裹数
        @JSONField(name = "confirm_parcel_weight")
        private BigDecimal confirmParcelWeight;     //订单实重（默认g）
        @JSONField(name = "confirm_parcel_volume_weight")
        private BigDecimal confirmParcelVolumeWeight;       //订单体积重/材积重（默认g，若有才返回）
        @JSONField(name = "confirm_parcel_charge_weight")
        private BigDecimal confirmParcelChargeWeight;       //订单计费重（默认g）
        @JSONField(name = "parcel_list_confirm_info")
        private List<ParcelListConfirmInfo> parcelListConfirmInfoList;      //包裹列表
    }

    @Data
    public static class ParcelListConfirmInfo
    {
        @JSONField(name = "confirm_weight")
        private BigDecimal confirmWeight;       //核实重量（默认g）
        @JSONField(name = "confirm_volume_weight")
        private BigDecimal confirmVolumeWeight;     //包裹体积重（默认g）
        @JSONField(name = "confirm_length")
        private BigDecimal confirmLength;       //核实包裹长（cm，只有库内进行了测量，才有值）
        @JSONField(name = "confirm_width")
        private BigDecimal confirmWidth;        //核实包裹宽（cm，只有库内进行了测量，才有值）
        @JSONField(name = "confirm_high")
        private BigDecimal confirmHigh;     //核实包裹高（cm，只有库内进行了测量，才有值）
        @JSONField(name = "confirm_charge_weight")
        private BigDecimal confirmChargeWeight;     //包裹计费重（默认g）
        @JSONField(name = "confirm_include_battery")
        private String confirmIncludeBattery;       //核实是否含电池（Y/N）
        @JSONField(name = "confirm_battery_type")
        private String confirmBatteryType;      //核实带电类型（内置电池966：1；配套电池967：2）
        @JSONField(name = "parcel_total_value_confirm")
        private BigDecimal parcelTotalValueConfirm;     //核实包裹价值
        @JSONField(name = "currency_code")
        private String currencyCode;        //币别（按照ISO标准，目前只支持USD）
    }
}
