package com.amg.fulfillment.cloud.logistics.api.util;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;

import java.io.IOException;

/**
 * Created by Seraph on 2021/5/7
 */

@Slf4j
public abstract class AbstractLogisticXmlUtil extends AbstractLogisticUtil{

    protected static XmlMapper xmlMapper;

    static {
        xmlMapper = new XmlMapper();
        //关键：设置首字符大写，否则请求出错
        xmlMapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
    }

    public <T> T sendPostRequestForXml(String path, String body, Class<T> clazz, boolean urlFlag)
    {
        return this.sendRequestForXml(HttpMethod.POST, path, body, clazz, urlFlag);
    }

    public <T> T sendGetRequestForXml(String path, String body, Class<T> clazz, boolean urlFlag)
    {
        return this.sendRequestForXml(HttpMethod.GET, path, body, clazz, urlFlag);
    }

    public <T> T sendRequestForXml(HttpMethod method, String path, String body, Class<T> clazz, boolean urlFlag)
    {
        String response  = this.doSendRequest(method, path, body, urlFlag, String.class);
        log.info(getLogisticName() + "-返回数据:{}", response);

        try {
            return xmlMapper.readValue(response, clazz);
        } catch (IOException err) {
            log.error("燕文物流-解析XML失败:{}", err);
        }
        return null;
    }
}
