package com.amg.fulfillment.cloud.logistics.api.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.ExtraProcessor;
import com.alibaba.fastjson.parser.deserializer.PropertyProcessable;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.amg.fulfillment.cloud.logistics.api.config.logistic.PX4Config;
import com.amg.fulfillment.cloud.logistics.api.dto.logistic.px4.*;
import com.amg.fulfillment.cloud.logistics.api.enumeration.UrlConfigEnum;
import com.fpx.api.model.AffterentParam;
import com.fpx.api.model.CommonRequestParam;
import com.fpx.api.utils.SignUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.sql.SQLOutput;
import java.util.*;

/**
 * @author Tom
 * @date 2021-04-20-14:15
 */
@Component
@Slf4j
@Getter
public class PX4LogisticUtil extends AbstractLogisticUtil{

    @Autowired
    private PX4Config px4Config;

    private static final String KEY_WORD_1 = "result";
    private static final String KEY_WORD_2 = "errors";
    private static final String KEY_WORD_3 = "msg";

    public TrackResponseFor4PX trOrderTrackingGet(LogisticTrackSearchFor4PX logisticTrackSearchFor4PX)
    {
        String requestBody = JSON.toJSONString(logisticTrackSearchFor4PX);
        return this.sendPostRequest(UrlConfigEnum.UrlConfigEnumPX4.TR_ORDER_TRACKING_GET.getUrl(), requestBody, TrackResponseFor4PX.class);
    }

    public LogisticOrderResponseFor4PX dsXmsOrderCreate(LogisticOrderFor4PX orderFor4PX)
    {
        String requestBody = JSON.toJSONString(orderFor4PX);
        return this.sendPostRequest(UrlConfigEnum.UrlConfigEnumPX4.DS_XMS_ORDER_CREATE.getUrl(), requestBody, LogisticOrderResponseFor4PX.class);
    }

    public LogisticLabelResponseFor4PX dsXmsLabelGet(LogisticLabelSearchFor4PX logisticLabelSearchFor4PX)
    {
        String requestBody = JSON.toJSONString(logisticLabelSearchFor4PX);
        return this.sendPostRequest(UrlConfigEnum.UrlConfigEnumPX4.DS_XMS_LABEL_GET.getUrl(), requestBody, LogisticLabelResponseFor4PX.class);
    }

    public LogisticOrderInfoResponseFor4PX dsXmsOrderGet(LogisticOrderInfoSearchFor4PX logisticOrderInfoSearchFor4PX)
    {
        String requestBody = JSON.toJSONString(logisticOrderInfoSearchFor4PX);
        return this.sendPostRequest(UrlConfigEnum.UrlConfigEnumPX4.DS_XMS_ORDER_GET.getUrl(), requestBody, LogisticOrderInfoResponseFor4PX.class);
    }

    public LogisticProductResponseFor4PX dsXmsLogisticsProductGetlist(LogisticProductSearchFor4PX logisticProductSearchFor4PX)
    {
        String requestBody = JSON.toJSONString(logisticProductSearchFor4PX);
        return this.sendPostRequest(UrlConfigEnum.UrlConfigEnumPX4.DS_XMS_LOGISTICS_PRODUCT_GETLIST.getUrl(), requestBody, LogisticProductResponseFor4PX.class);
    }

    private String getRequestUrl(String url, String bodyJson, String version) {
        AffterentParam affterentParam = new AffterentParam();
        affterentParam.setAppKey(px4Config.getAppkey());
        affterentParam.setFormat(px4Config.getFormat());
        affterentParam.setMethod(url);
        affterentParam.setAppSecret(px4Config.getAppSecret());
        affterentParam.setVersion(version);
        long timestamp = System.currentTimeMillis();
        String sign = SignUtil.getSingByParam(affterentParam, bodyJson, timestamp);
        CommonRequestParam commonRequestParam = new CommonRequestParam();
        BeanUtil.copyProperties(affterentParam, commonRequestParam);
        commonRequestParam.setTimestamp(timestamp);
        commonRequestParam.setSign(sign);
        return transformToUrlParams(px4Config.getBaseUrl(), commonRequestParam);
    }

    private String transformToUrlParams(String url, CommonRequestParam parm) {
        StringBuilder sb = new StringBuilder();
        sb.append(url).append("?")
                .append("app_key=").append(parm.getAppKey()).append("&")
                .append("format=").append(parm.getFormat()).append("&")
                .append("method=").append(parm.getMethod()).append("&")
                .append("timestamp=").append(parm.getTimestamp()).append("&")
                .append("v=").append(parm.getVersion()).append("&")
                .append("sign=").append(parm.getSign());
        return sb.toString();
    }

    @Override
    public String getLogisticName() {
        return "4PX物流";
    }

    @Override
    public String getRequestUrl(String path) {
        return null;
    }

    @Override
    public Map<String, List<String>> getRequestHeader() {
        return Collections.EMPTY_MAP;
    }

    public String  getRequestUrl(String path, String body)
    {
        return this.getRequestUrl(path, body, px4Config.getVersion_1_0());
    }
}
