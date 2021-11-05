package com.amg.fulfillment.cloud.logistics.api.enumeration;

import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import lombok.Getter;


/**
 * Created by Seraph on 2021/4/29
 */

@Getter
public enum  LogisticProviderEnum {

    DEFAULT("DEFAULT", null, null, "000"),
    YUNTU(Constant.LOGISTIC_YUNTU, "云途", "yuntu/", "101"),
    WB(Constant.LOGISTIC_WB, "万邦", "wb/", "102"),
    YANWEN(Constant.LOGISTIC_YANWEN, "燕文", "yanwen/", "103"),
    PX4(Constant.LOGISTIC_PX4, "递四方", "px4/", "104");

    public static String FULFILLMENT = "fulfillment/";
    public static String LOGISTIC = "logistic/";
    private String code;
    private String name;
    private String directory;
    private String serialCode;


    LogisticProviderEnum(String code, String name, String directory, String serialCode) {
        this.code = code;
        this.name = name;
        this.directory = directory;
        this.serialCode = serialCode;
    }

    public static LogisticProviderEnum getLogisticProviderEnumByCode(String code)
    {
        LogisticProviderEnum[] logisticProviderEnumArr = LogisticProviderEnum.values();
        for (LogisticProviderEnum logisticProviderEnum : logisticProviderEnumArr) {
            if(logisticProviderEnum.getCode().equals(code))
            {
                return logisticProviderEnum;
            }
        }
        return null;
    }
}
