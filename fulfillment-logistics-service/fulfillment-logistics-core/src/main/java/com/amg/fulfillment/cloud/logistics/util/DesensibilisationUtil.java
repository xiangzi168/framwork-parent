package com.amg.fulfillment.cloud.logistics.util;

import com.amg.fulfillment.cloud.logistics.api.enumeration.LogisticTrackCJEnum;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.StringUtils;

import static org.apache.coyote.http11.Constants.a;

/**
 * @ClassName DesensibilisationUtil
 * @Description 字符串脱敏转换处理类
 * @Author 35112
 * @Date 2021/8/12 10:29
 **/
public class DesensibilisationUtil {
    /**
     * 清除字符串中包含china的字段
     *
     * @param address 地区或者物流轨迹
     * @return china
     */
    public static String cleanAddress(String address) {
        if (StringUtils.isNotEmpty(address)) {
            address = address.replace("china", "");
        }
        return address;
    }

    /**
     * 清除字符串中包含china的字段
     *
     * @param address 地区或者物流轨迹
     * @return china
     */
    public static String toLowerCase(String address) {
        String s = null;
        if (StringUtils.isNotEmpty(address)) {
            s = address.toLowerCase();
        }
        return s;
    }

    /**
     * @param trackRemake
     * @return
     */
    public static Integer getCJLogisticsTrackStatus(String trackRemake) {
        String s = toLowerCase(trackRemake);
        if (StringUtils.isNotEmpty(s)) {
            //如果包含  返回包裹退回
            if (s.contains("returned to sender")) {
                return LogisticTrackCJEnum.RETURNED.getCode();
            }
            //如果包含  商家取消发货
            if (s.contains("shipment cancelled")) {
                return LogisticTrackCJEnum.CANCELLED.getCode();
            }
            //如果包含  返回到达待取
            if (s.contains("out for delivery") || s.contains("due to be delivered today")) {
                return LogisticTrackCJEnum.TAKE.getCode();
            }
            //如果包含  返回已妥投elivered、delivery、delivery recorded、successful delivery、Successful delivery、Delivered、DELIVERY RECORDED
            if (s.contains("elivered") || s.contains("delivery") || s.contains("delivery recorded") || s.contains("successful delivery") || s.contains("delivered") || s.contains("delivery recorded")) {
                return LogisticTrackCJEnum.SIGN.getCode();
            }
            //如果都未匹配 返回未知状态
            return LogisticTrackCJEnum.UNKNOWN.getCode();
        }
        //状态字段为空
        return null;
    }

    public static final String IN_TRANSIT = "in transit";

    public static String cjCityReplace(String track) {
        //城市关键名称 脱敏
        //HongKong、shenzhen、zhengzhou、hangzhou、guangzhou、yiwu、dalian、beijing、shanghai、wuhan
        if (StringUtils.isNotEmpty(track)) {
            int hongkong = track.indexOf("hongkong");
            int shenzhen = track.indexOf("shenzhen");
            int zhengzhou = track.indexOf("zhengzhou");
            int hangzhou = track.indexOf("hangzhou");
            int guangzhou = track.indexOf("guangzhou");
            int yiwu = track.indexOf("yiwu");
            int dalian = track.indexOf("dalian");
            int beijing = track.indexOf("beijing");
            int shanghai = track.indexOf("shanghai");
            int wuhan = track.indexOf("wuhan");
            int i = hongkong + shenzhen + zhengzhou + hangzhou + guangzhou + yiwu + dalian + beijing + shanghai + wuhan;
            if (i==-10) {
                return track;
            } else {
                return IN_TRANSIT;
            }
        }
        return track;

    }
}
