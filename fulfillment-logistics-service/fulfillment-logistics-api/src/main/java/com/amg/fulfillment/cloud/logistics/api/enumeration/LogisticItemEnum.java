package com.amg.fulfillment.cloud.logistics.api.enumeration;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by Seraph on 2021/5/21
 */

@Getter
public class LogisticItemEnum {

    ;

    @Getter
    public enum LogisticItemEnumPX4
    {
        ;

        @Getter
        public enum OrderModelEnum
        {
            C("C", "创建订单时取号"),
            U("U", "仓库作业时取号");

            OrderModelEnum(String code, String msg)
            {
                this.code = code;
                this.msg = msg;
            }

            private String code;
            private String msg;
        }
    }

    @Getter
    public enum LogisticItemEnumWanB
    {
        ;

        @Getter
        public enum DepositoryChannelOrderStatusEnum
        {
            REQUESTED("Requested", "已申请取消"),
            ACCEPTED("Accepted", "取消成功"),
            REJECTED("Rejected", "取消失败"),
            ;

            DepositoryChannelOrderStatusEnum(String status, String msg)
            {
                this.status = status;
                this.msg = msg;
            }

            private String status;
            private String msg;
        }

        @Getter
        public enum WithBatteryTypeEnum     //包裹带电类型
        {
            NOBattery(1, "不带电","NOBattery"),
            WithBattery(2, "带电","WithBattery"),
            Battery(3, "纯电池","Battery");

            private Integer id;
            private String name;
            private String code;

            WithBatteryTypeEnum(Integer id, String name, String code) {
                this.id = id;
                this.name = name;
                this.code = code;
            }
        }

        @Getter
        public enum ParcelItemTypeEnum        //包裹类型
        {
            DOC("DOC", "文件"),
            SPX("SPX", "包裹")
            ;

            private String type;
            private String nane;

            ParcelItemTypeEnum(String type, String name)
            {
                this.type = type;
                this.nane = name;
            }
        }


        @Getter
        public enum ParcelMPSTypeEnum       //包裹一票多件类型
        {
            Normal("Normal", "Normal"),
            FBA("FBA", "FBA，亚马逊FBA");

            private String type;
            private String nane;

            ParcelMPSTypeEnum(String type, String name)
            {
                this.type = type;
                this.nane = name;
            }
        }

        @Getter
        public enum ParcelTradeTypeEnum     //包裹对应订单交易类型
        {
            B2C("B2C", "B2C"),
            B2B("B2B", "B2B");

            private String type;
            private String nane;

            ParcelTradeTypeEnum(String type, String name)
            {
                this.type = type;
                this.nane = name;
            }
        }

        @Getter
        public enum DutyPaymentMethodEmum       //关税付款方式方式
        {
            DDP("DDP", "Delivered Duty Paid，完税后交货"),
            DDU("DDU", "Delivered Duty Unpaid，未完税交货");

            private String type;
            private String nane;

            DutyPaymentMethodEmum(String type, String name)
            {
                this.type = type;
                this.nane = name;
            }
        }
    }

    @Getter
    public enum LogisticItemEnumYanWen
    {
        ;
    }

    @Getter
    public enum LogisticItemEnumYunTu
    {
        ;
    }
}
