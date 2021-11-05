package com.amg.fulfillment.cloud.logistics.api.util;

import com.amg.framework.boot.redis.utils.RedisUtils;
import com.amg.framework.boot.utils.id.SnowflakeIdUtils;
import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.api.enumeration.LogisticProviderEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by Seraph on 2021/5/19
 */


@Slf4j
@Component
public class LogisticsCommonUtils {

    @Autowired
    private RedisUtils redisUtils;

    public static String convertJudgeIntegerToString(Integer integerJudge)
    {
        if(integerJudge == null)    return "N";
        return Constant.YES == integerJudge ? "Y" : "N";
    }

    public static String convertMomosoImg(String imgPath)
    {
        return imgPath.replaceAll("image.momoso.com", "image-free.momoso.com");
    }


    public String generateDeliveryPackageNo() {
        Long generateId = redisUtils.incrt(Constant.LOGISTICS_DELIVERY_PACKAGE_SERIAL_NUMBER + DateFormatUtils.format(new Date(), "yyyyMMdd"));
        return Constant.LOGISTIC_LOGO + DateFormatUtils.format(new Date(), "yyyyMMdd") + String.format("%06d", generateId);
    }

    public String generateDepositoryPredictionPackageNo() {
        Long generateId = redisUtils.incrt(Constant.LOGISTICS_DEPOSITORY_PREDICITON_PACKAGE_SERIAL_NUMBER + DateFormatUtils.format(new Date(), "yyyyMMdd"));
        return Constant.DEPOSITORY_LOGO + DateFormatUtils.format(new Date(), "yyyyMMdd") + String.format("%06d", generateId);
    }
}
