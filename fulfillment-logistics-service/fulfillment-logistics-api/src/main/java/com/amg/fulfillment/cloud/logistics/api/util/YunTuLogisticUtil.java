package com.amg.fulfillment.cloud.logistics.api.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSON;
import com.amg.fulfillment.cloud.logistics.api.config.logistic.YunTuConfig;
import com.amg.fulfillment.cloud.logistics.api.dto.logistic.yuntu.*;
import com.amg.fulfillment.cloud.logistics.api.enumeration.UrlConfigEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Tom
 * @date 2021-04-16-14:53
 */
@Component
@Slf4j
public class YunTuLogisticUtil extends AbstractLogisticUtil{

    @Autowired
    private YunTuConfig yunTuConfig;

    public TrackResponseForYunTu trackingGettrackallinfo(String trackingNumber)
    {
        String url = String.format(UrlConfigEnum.UrlConfigEnumYunTu.TRACKING_GETTRACKALLINFO.getUrl(), trackingNumber);
        return this.sendGetRequest(url, TrackResponseForYunTu.class);
    }

    public LogisticOrderResponseForYunTu waybillCreateorder(LogisticOrderForYunTu logisticOrderForYunTu)
    {
        String body = JSON.toJSONString(Collections.singletonList(logisticOrderForYunTu));
        return this.sendPostRequest(UrlConfigEnum.UrlConfigEnumYunTu.WAYBILL_CREATEORDER.getUrl(), body, LogisticOrderResponseForYunTu.class);
    }

    public LogisticLabelResponseForYunTu labelPrint(String orderNo)
    {
        String body = JSON.toJSONString(Collections.singletonList(orderNo));
        return this.sendPostRequest(UrlConfigEnum.UrlConfigEnumYunTu.LABEL_PRINT.getUrl(), body, LogisticLabelResponseForYunTu.class);
    }

    public LogisticDetailResponseForYunTu waybillGetorder(String orderNo)
    {
        return this.sendGetRequest(String.format(UrlConfigEnum.UrlConfigEnumYunTu.WAYBILL_GETORDER.getUrl(), orderNo), LogisticDetailResponseForYunTu.class);
    }

    @Override
    public String getLogisticName() {
        return "云途物流";
    }

    @Override
    public String getRequestUrl(String path) {
        return yunTuConfig.getBaseUrl() + path;
    }

    @Override
    public Map<String, List<String>> getRequestHeader() {
        Map<String, List<String>> headerMap = new HashMap<>();
        headerMap.put("Accept", Collections.singletonList("application/json"));
        headerMap.put("Content-Type", Collections.singletonList("application/json"));
        headerMap.put("Authorization", Collections.singletonList(yunTuConfig.getAuthorization()));
        return headerMap;
    }
}
