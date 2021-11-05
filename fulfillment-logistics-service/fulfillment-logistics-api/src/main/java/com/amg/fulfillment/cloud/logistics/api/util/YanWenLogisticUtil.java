package com.amg.fulfillment.cloud.logistics.api.util;

import com.alibaba.fastjson.JSON;
import com.amg.fulfillment.cloud.logistics.api.config.logistic.YanWenConfig;
import com.amg.fulfillment.cloud.logistics.api.dto.logistic.yanwen.*;
import com.amg.fulfillment.cloud.logistics.api.enumeration.UrlConfigEnum;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author Tom
 * @date 2021-04-16-14:53
 */
@Component
@Slf4j
@Getter
public class YanWenLogisticUtil extends AbstractLogisticXmlUtil{

    @Autowired
    private YanWenConfig yanWenConfig;

    private static final String URL_PARAM = "?nums=%s";
    private static final String KEY_WORD_1 = "code";
    private static final String KEY_WORD_2 = "message";
    private static final String KEY_WORD_3 = "CallSuccess";
    private static final String KEY_WORD_4 = "Response";
    private static final String KEY_WORD_5 = "<ReasonMessageType>";

    /**
     * @param nums
     * @return com.amg.fulfillment.cloud.logistics.api.dto.logistic.yanwen.TrackResponseForYanWen
     * 轨迹查询
     * @author Tom
     * @date 2021/4/22  11:30
     * 注意： 燕文查询轨迹的路径和授权特殊，单独使用
     */
    public TrackResponseForYanWen getLogisticTrack(List<String> nums) {
        String searchNums = StringUtils.join(nums, ",");
        String requestUrl = yanWenConfig.getTrackUrl() + String.format(URL_PARAM, searchNums);
        log.info(getLogisticName() + "-请求路径:{}", requestUrl);
        HttpHeaders httpHeaders = new HttpHeaders();
        Map<String, List<String>> headerMap = new HashMap<>();
        headerMap.put("Accept", Collections.singletonList("application/json"));
        headerMap.put("Content-Type", Collections.singletonList("text/xml; charset=utf-8"));
        headerMap.put("Authorization", Collections.singletonList(yanWenConfig.getUserId()));
        httpHeaders.putAll(headerMap);
        HttpEntity httpEntity = new HttpEntity(null, httpHeaders);
        ResponseEntity<TrackResponseForYanWen> response = this.getRestTemplate().exchange(requestUrl, HttpMethod.GET, httpEntity, TrackResponseForYanWen.class);
        log.info("燕文物流-返回内容: {}",JSON.toJSONString(response));
        return response.getBody();
    }

    /**
     * @param logisticOrderForYanWen
     * @return com.amg.fulfillment.cloud.logistics.api.dto.logistic.yanwen.LogisticOrderResponseForYanWen
     * 下单
     * @author Tom
     * @date 2021/4/22  11:30
     */
    public LogisticOrderResponseForYanWen addLogisticOrder(LogisticOrderForYanWen logisticOrderForYanWen) {
        try {
            String requestUrl = this.getRequestUrl(String.format(UrlConfigEnum.UrlConfigEnumYanWen.EXPRESSES.getUrl(), yanWenConfig.getUserId()));
            String body = xmlMapper.writeValueAsString(logisticOrderForYanWen);
            body = body.replaceAll("LogisticOrderForYanWen", "ExpressType");

            return this.sendPostRequestForXml(requestUrl, body, LogisticOrderResponseForYanWen.class, Boolean.FALSE);
        } catch (IOException e) {
            log.error("燕文物流-解析XML失败:{}", e);
            log.error("燕文物流-解析XML失败:{}", e.getMessage());
            return LogisticOrderResponseForYanWen.fail500("燕文物流-解析XML失败");
        }
    }

    public LogisticDetailResponseForYanWen getLogisticList(LogisticDetailForYanWen logisticDetailForYanWen)
    {
        try {
            String url = String.format(UrlConfigEnum.UrlConfigEnumYanWen.EXPRESSES_INFO.getUrl(), yanWenConfig.getUserId(),
                    logisticDetailForYanWen.getPage(),
                    logisticDetailForYanWen.getCode(),
                    logisticDetailForYanWen.getReceiver(),
                    logisticDetailForYanWen.getChannel(),
                    logisticDetailForYanWen.getStart(),
                    logisticDetailForYanWen.getEnd(),
                    logisticDetailForYanWen.getIsstatus());

            String requestUrl = this.getRequestUrl(url);
            String body = xmlMapper.writeValueAsString(logisticDetailForYanWen);
            body = body.replaceAll("LogisticOrderForYanWen", "ExpressType");

            return this.sendPostRequestForXml(requestUrl, body, LogisticDetailResponseForYanWen.class, Boolean.FALSE);
        } catch (IOException e) {
            log.error("燕文物流-解析XML失败:{}", e);
            log.error("燕文物流-解析XML失败:{}", e.getMessage());
            return LogisticDetailResponseForYanWen.fail500("燕文物流-解析XML失败");
        }
    }

    public LogisticLabelResponseForYanWen getLogisticPrintLabel(LogisticLabelForYanWen logisticLabelForYanWen)
    {
        String requestUrl = String.format(UrlConfigEnum.UrlConfigEnumYanWen.LABEL.getUrl(),
                yanWenConfig.getUserId(),
                logisticLabelForYanWen.getEpCode(),
                Optional.ofNullable(logisticLabelForYanWen.getLabelSize()).orElse("A10x10L"));

        LogisticLabelResponseForYanWen logisticLabelResponseForYanWen = new LogisticLabelResponseForYanWen();
        LogisticLabelResponseForYanWen.Response response = new LogisticLabelResponseForYanWen.Response();
        InputStream inputStream = this.sendRequest(HttpMethod.GET, requestUrl, null);
        if (inputStream != null) {
            response.setInputStream(inputStream);
            logisticLabelResponseForYanWen.setCallSuccess(Boolean.TRUE);
            logisticLabelResponseForYanWen.setResponse(response);
            return logisticLabelResponseForYanWen;
        }

        response.setReasonMessage("打印物流标签失败");
        logisticLabelResponseForYanWen.setCallSuccess(Boolean.TRUE);
        logisticLabelResponseForYanWen.setResponse(response);

        return logisticLabelResponseForYanWen;
    }

    @Override
    public String getLogisticName() {
        return "燕文物流";
    }

    @Override
    public String getRequestUrl(String path) {
        return yanWenConfig.getBaseUrl() + path;
    }

    @Override
    public Map<String, List<String>> getRequestHeader() {
        Map<String, List<String>> headerMap = new HashMap<>();
        headerMap.put("Accept", Collections.singletonList("application/json"));
        headerMap.put("Content-Type", Collections.singletonList("text/xml; charset=utf-8"));
        headerMap.put("Authorization", Collections.singletonList(yanWenConfig.getAuthorization()));
        return headerMap;
    }
}

//燕文下单成功返回的xml字符串
//String mes="<CreateExpressResponseType xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><CallSuccess>true</CallSuccess><CreatedExpress><Receiver><Postcode>253400</Postcode><State>FL</State><NationalId /><Email /><District /><Userid>100000</Userid><Name>tom</Name><Phone>15811456692</Phone><Mobile>15811456692</Mobile><Company /><Country>UNITED STATES</Country><CountryType><Id>115</Id><RegionCh>美国</RegionCh><RegionEn>UNITED STATES</RegionEn><RegionCode>US</RegionCode></CountryType><City>city</City><CityCode /><Address1>address</Address1><Address2 /><NationalIdFullName /><NationalIdIssueDate>2021-04-19T00:00:00+08:00</NationalIdIssueDate><NationalIdExpireDate>2021-04-19T00:00:00+08:00</NationalIdExpireDate></Receiver><UserOrderNumber>mall12314545</UserOrderNumber><GoodsName><Id>0</Id><Userid>100000</Userid><NameCh>多媒体播放器</NameCh><NameEn>MedialPlayer</NameEn><Weight>122</Weight><DeclaredValue>12.334</DeclaredValue><DeclaredCurrency>USD</DeclaredCurrency><MoreGoodsName /><HsCode /><ProductBrand /><ProductSize /><ProductColor /><ProductMaterial /></GoodsName><Sender><TaxNumber>xxxx</TaxNumber></Sender><DateOfReceipt /><SalesOfEnterPriseCode /><SalesOfEnterPriseName /><PayOfEnterPriseName /><PayOfEnterPriseCode /><Epcode>UF738017835YP</Epcode><Userid>100000</Userid><ChannelType><Id>895</Id><Name>VIP-燕文专线平邮小包-特货</Name><Status>true</Status><NameEn /><LimitStatus>true</LimitStatus></ChannelType><Channel>VIP-燕文专线平邮小包-特货</Channel><Package>无</Package><SendDate>2021-04-19T00:00:00</SendDate><Quantity>1</Quantity><CustomDeclarationCollection><CustomDeclarationType><Id>0</Id><Userid>100000</Userid><NameCh>多媒体播放器</NameCh><NameEn>MedialPlayer</NameEn><Weight>122</Weight><DeclaredValue>12.334</DeclaredValue><DeclaredCurrency>USD</DeclaredCurrency><MoreGoodsName /><HsCode /><Quantity>1</Quantity></CustomDeclarationType></CustomDeclarationCollection><YanwenNumber>AA002342246YE</YanwenNumber><ReferenceNo /><PackageNo /><Insure>false</Insure><Memo /><TrackingStatus>暂无信息</TrackingStatus><IsPrint>false</IsPrint><CreateDate>2021-04-19T15:38:00.2576246+08:00</CreateDate><MerchantCsName /><ProductLink /><UndeliveryOption>0</UndeliveryOption><IsPostPlatform>false</IsPostPlatform><BatteryStatus>0</BatteryStatus><IsStatus>0</IsStatus></CreatedExpress><Response><Userid>100000</Userid><Operation>Create</Operation><Success>true</Success><Reason>None</Reason><ReasonMessage>没有错误</ReasonMessage><Epcode>UF738017835YP</Epcode></Response></CreateExpressResponseType>";
