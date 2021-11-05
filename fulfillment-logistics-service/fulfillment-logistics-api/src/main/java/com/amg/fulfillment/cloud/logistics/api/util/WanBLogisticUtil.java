package com.amg.fulfillment.cloud.logistics.api.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSON;
import com.amg.fulfillment.cloud.logistics.api.config.depository.WanBConfig;
import com.amg.fulfillment.cloud.logistics.api.dto.logistic.Wanb.*;
import com.amg.fulfillment.cloud.logistics.api.enumeration.UrlConfigEnum;
import io.swagger.annotations.ApiModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tom
 * @date 2021-04-15-18:04
 */
@Component
@ApiModel("万邦快递远程访问工具类")
@Slf4j
public class WanBLogisticUtil extends AbstractLogisticUtil{

    @Autowired
    private WanBConfig wanBConfig;

    public TrackResponseForWanb trackpoints(String trackingNumber)
    {
        String url = String.format(UrlConfigEnum.UrlConfigEnumWanB.TRACKPOINTS.getUrl(), trackingNumber);
        return this.sendGetRequest(url, TrackResponseForWanb.class);
    }

    public LogisticDetailResponseForWanb parcelsInfo(LogisticDetailForWanb logisticDetailForWanb)
    {
        return this.sendGetRequest(String.format(UrlConfigEnum.UrlConfigEnumWanB.PARCELS_INFO.getUrl(), logisticDetailForWanb.getProcessCode()), LogisticDetailResponseForWanb.class);
    }

    public LogisticLabelResponseForWanb label(LogisticLabelForWanb logisticLabelForWanb)
    {
        InputStream inputStream = null;
        try {
            inputStream = sendRequest(HttpMethod.GET, String.format(UrlConfigEnum.UrlConfigEnumWanB.LABEL.getUrl(), logisticLabelForWanb.getProcessCode()), null);
        } catch (Exception e) {
           log.error("万邦物流获取面单失败，原因：{}",e);
        }
        if(inputStream != null) {
            LogisticLabelResponseForWanb.DetailData detailData = new LogisticLabelResponseForWanb.DetailData();
            detailData.setInputStream(inputStream);
            LogisticLabelResponseForWanb logisticLabelResponseForWanb = new LogisticLabelResponseForWanb();
            logisticLabelResponseForWanb.setData(detailData);
            logisticLabelResponseForWanb.setSucceeded(Boolean.TRUE);
            return logisticLabelResponseForWanb;
        }
        LogisticLabelResponseForWanb logisticLabelResponseForWanb = new LogisticLabelResponseForWanb();
        AbstractResponseWanb.Error error = new AbstractResponseWanb.Error();
        error.setMessage("打印物流标签失败");

        logisticLabelResponseForWanb.setSucceeded(Boolean.FALSE);
        logisticLabelResponseForWanb.setError(error);
        return logisticLabelResponseForWanb;
    }

    public LogisticOrderResponseForWanb createParcels(LogisticOrderForWanb logisticOrderForWanb)
    {
        String body = JSON.toJSONString(logisticOrderForWanb);
        return this.sendPostRequest(UrlConfigEnum.UrlConfigEnumWanB.CREATE_PARCELS.getUrl(), body, LogisticOrderResponseForWanb.class);
    }

    @Override
    public String getLogisticName() {
        return "万邦物流";
    }

    @Override
    public String getRequestUrl(String path) {
        return wanBConfig.getBaseUrl() + path;
    }

    @Override
    public Map<String, List<String>> getRequestHeader() {
        Map<String, List<String>> headerMap = new HashMap<>();
        headerMap.put("Accept", Collections.singletonList("application/json"));
        headerMap.put("Content-Type", Collections.singletonList("application/json"));
        headerMap.put("Authorization", Collections.singletonList(wanBConfig.getAuthorization()));
        return headerMap;
    }

    public LogisticConfirmForWanb confirm(String processCode) {
        String url = String.format(UrlConfigEnum.UrlConfigEnumWanB.PARCELS_CONFIRMATION_POST.getUrl(), processCode);
        return this.sendPostRequest(url, null, LogisticConfirmForWanb.class);
    }
}
