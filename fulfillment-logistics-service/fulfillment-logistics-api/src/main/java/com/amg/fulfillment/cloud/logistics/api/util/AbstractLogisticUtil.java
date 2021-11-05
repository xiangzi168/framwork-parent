package com.amg.fulfillment.cloud.logistics.api.util;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by Seraph on 2021/5/6
 */
@Slf4j
public abstract class AbstractLogisticUtil {

    @Autowired
    @Getter
    private RestTemplate restTemplate;

    public abstract String getLogisticName();

    public abstract String getRequestUrl(String path);

    public abstract Map<String, List<String>> getRequestHeader();

    public <T> T convertResponseBody(String response, Class<T> clazz)
    {
        return JSON.parseObject(response, clazz);
    }

    public <T> T sendGetRequest(String path, Class<T> clazz)
    {
        return this.sendRequest(HttpMethod.GET, path, null, clazz);
    }

    public <T> T sendGetRequest(String path, Class<T> clazz, Boolean urlFlag)
    {
        return this.sendRequest(HttpMethod.GET, path, null, clazz, urlFlag);
    }

    public <T> T sendPostRequest(String path, String body, Class<T> clazz)
    {
        return this.sendRequest(HttpMethod.POST, path, body, clazz);
    }

    public <T> T sendPostRequest(String path, String body, Class<T> clazz, Boolean urlFlag)
    {
        return this.sendRequest(HttpMethod.POST, path, body, clazz, urlFlag);
    }

    public <T> T sendRequest(HttpMethod method, String path, String body, Class<T> clazz)
    {
        return this.sendRequest(method, path, body, clazz, Boolean.TRUE);
    }

    public <T> T sendRequest(HttpMethod method, String path, String body, Class<T> clazz, boolean urlFlag)
    {
        String response  = this.doSendRequest(method, path, body, urlFlag, String.class);
        log.info(getLogisticName() + "-返回数据:{}", response);

        return this.convertResponseBody(response, clazz);
    }

    public InputStream sendRequest(HttpMethod method, String path, String body)
    {
        Resource resource = this.doSendRequest(method, path, body, Boolean.TRUE, Resource.class);
        try
        {
            return resource.getInputStream();
        } catch (Exception err)
        {
            log.error("获取流失败", err);
        }
        return null;
    }

    public <T> T doSendRequest(HttpMethod method, String path, String body, boolean urlFlag, Class clazz)
    {
        String requestUrl = path;
        if(urlFlag)     //判断请求地址是否需要处理
        {
            requestUrl = this.getRequestUrl(path, body);
        }

        log.info(getLogisticName() + "-请求路径:{}", requestUrl);
        log.info(getLogisticName() + "-请求参数:{}", StringUtils.isNoneBlank(body) ? body : "");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.putAll(getRequestHeader());
        HttpEntity httpEntity = new HttpEntity(body, httpHeaders);
        ResponseEntity<T> resourceResponseEntity = restTemplate.exchange(requestUrl, method, httpEntity, clazz);
        if(resourceResponseEntity.getStatusCode().equals(HttpStatus.OK))
        {
            return resourceResponseEntity.getBody();
        }

        throw new RuntimeException("Http request err …… ");
//
//        try {
//            InputStream inputStream = resourceResponseEntity.getBody().getInputStream();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//        return new HttpRequest(requestUrl).method(method)
//                .header(this.getRequestHeader())
//                .body(body)
//                .execute();
    }

    public String getRequestUrl(String path, String body)
    {
        return this.getRequestUrl(path);
    }
}
