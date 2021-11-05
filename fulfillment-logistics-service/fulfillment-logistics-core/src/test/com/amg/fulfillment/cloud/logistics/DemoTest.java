package com.amg.fulfillment.cloud.logistics;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.digest.mac.MacEngine;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amg.framework.boot.utils.thread.ThreadPoolUtil;
import com.amg.fulfillment.cloud.logistics.api.common.Constant;
import com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb.DepositoryProcessMsg;
import com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb.PurchaseIntoDepositoryDetailForWanB;
import com.amg.fulfillment.cloud.logistics.api.dto.depository.wanb.PurchaseIntoDepositoryMsg;
import com.amg.fulfillment.cloud.logistics.api.dto.logistic.px4.LogisticOrderFor4PX;
import com.amg.fulfillment.cloud.logistics.api.dto.logistic.px4.LogisticOrderInfoResponseFor4PX;
import com.amg.fulfillment.cloud.logistics.api.dto.logistic.px4.LogisticOrderResponseFor4PX;
import com.amg.fulfillment.cloud.logistics.api.dto.logistic.px4.TrackResponseFor4PX;
import com.amg.fulfillment.cloud.logistics.api.dto.logistic.yuntu.LogisticOrderForYunTu;
import com.amg.fulfillment.cloud.logistics.api.dto.logistic.yuntu.LogisticOrderResponseForYunTu;
import com.amg.fulfillment.cloud.logistics.api.dto.msg.DepositoryProcessMsgDto;
import com.amg.fulfillment.cloud.logistics.api.dto.msg.DepositoryPurchaseStatusMsgDto;
import com.amg.fulfillment.cloud.logistics.api.dto.msg.PurchaseIntoDepositoryMsgDto;
import com.amg.fulfillment.cloud.logistics.api.enumeration.LogisticsTrackInsideNodeEnum;
import com.amg.fulfillment.cloud.logistics.api.enumeration.PurchasePackageProductErrorTypeEnum;
import com.amg.fulfillment.cloud.logistics.api.enumeration.UrlConfigEnum;
import com.amg.fulfillment.cloud.logistics.api.proto.LogisticsTrackDetailGTO;
import com.amg.fulfillment.cloud.logistics.api.util.BeanConvertUtils;
import com.amg.fulfillment.cloud.logistics.dto.depository.AddressDto;
import com.amg.fulfillment.cloud.logistics.dto.logistic.*;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsChannelDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPackageDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPurchasePackageDO;
import com.amg.fulfillment.cloud.logistics.entity.LogisticsPurchasePackageErrorProductDO;
import com.amg.fulfillment.cloud.logistics.grpc.LogisticsTrackDetailService;
import com.amg.fulfillment.cloud.logistics.job.PurchaseIntoDepositoryRetryJob;
import com.amg.fulfillment.cloud.logistics.manager.ILogisticManager;
import com.amg.fulfillment.cloud.logistics.manager.TestLogisticManagerImpl;
import com.amg.fulfillment.cloud.logistics.module.rule.*;
import com.amg.fulfillment.cloud.logistics.util.MetadataPlusUtils;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import lombok.Data;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.reflection.Reflector;
import org.apache.xmlbeans.impl.xb.xmlconfig.Extensionconfig;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.*;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class DemoTest {
    @Test
    public void test_1() throws SQLException {
        String aa="ddddddddd";
        String bb = null;
        String cc= StringUtils.isNotBlank(bb) ? bb : "";
        System.out.println(cc);
        System.out.println(StringUtils.substring(aa + (StringUtils.isNotBlank(bb) ? bb : ""), 0, 190));
    }

    @Test
    public void test_2() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("test_2_1", String.class,String.class);
        final Annotation[][] paramAnnotations = method.getParameterAnnotations();
        System.out.println(Arrays.toString(paramAnnotations));
    }

    public void test_2_1(@Param("jjj") String aa, @Valid String dd) {
        System.out.println(aa);
    }
    @Test
    public void test_3() throws IOException {
        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.setRequestFactory();
        HttpHeaders httpHeaders = new HttpHeaders();
        HashMap<String, List<String>> map = new HashMap<>();
        map.put("Authorization", Collections.singletonList("basic D6140AA383FD8515B09028C586493DDB"));
        map.put("Content-Type", Collections.singletonList("text/xml; charset=utf-8"));
        map.put("Accept", Collections.singletonList("application/xml"));
        httpHeaders.putAll(map);
        HttpEntity httpEntity = new HttpEntity(httpHeaders);
        String url = "http://47.96.220.163:802/service/Users/100000/channels/485/countries";
        ResponseEntity<String> entity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        System.out.println("输出----》" + entity.getBody());
//        PoolingHttpClientConnectionManager();
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
    }

    @Test
    public void test_4() throws ParseException {
        System.out.println(DateUtil.format(new Date(), DatePattern.UTC_SIMPLE_PATTERN));
//       String receiveGoodTimeStr ="2021-08-23'T'09:10:00";
//       SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//       if (StringUtils.isNotBlank(receiveGoodTimeStr)) {
//            if (receiveGoodTimeStr.contains("T")) {
//                receiveGoodTimeStr = receiveGoodTimeStr.replaceAll("'","").replaceAll("T"," ");;
//            }
//            try {
//                System.out.println("date--"+simpleDateFormat.parse(receiveGoodTimeStr));
//            } catch (ParseException e) {
//                System.out.println(e);
//            }
//        }
    }

    @Test
    public void test_5() {
        ArrayList<String> predictionPurchaseList_primay = new ArrayList<>();
        predictionPurchaseList_primay.add("1");
        predictionPurchaseList_primay.add("3-1");
        predictionPurchaseList_primay.add("2");
        System.out.println(predictionPurchaseList_primay.stream().collect(Collectors.joining()));

    }

    public DemoTest.User createUser() {
        DemoTest.User user = new DemoTest.User();
        user.setName("zhangsan");
        return user;
    }

    @Data
    public static class User {
        private String name;
    }


    @Test
    public void test_6() {
        String message = "{\"data\":\"[{\\\"consignment_info\\\":{\\\"4px_tracking_no\\\":\\\"303847708788\\\",\\\"consignment_create_date\\\":1629417610000,\\\"consignment_status\\\":\\\"P\\\",\\\"ds_consignment_no\\\":\\\"DS303847708788\\\",\\\"get_no_mode\\\":\\\"C\\\",\\\"has_check_oda\\\":\\\"N\\\",\\\"insure_status\\\":\\\"N\\\",\\\"insure_type\\\":\\\"\\\",\\\"is_hold_sign\\\":\\\"N\\\",\\\"logistics_channel_no\\\":\\\"\\\",\\\"logistics_product_code\\\":\\\"O5\\\",\\\"logistics_product_name\\\":\\\"联邮通经济挂号-普货\\\",\\\"oda_result_sign\\\":\\\"N\\\",\\\"ref_no\\\":\\\"LOG20210820000032\\\"},\\\"parcel_confirm_info\\\":{\\\"confirm_parcel_qty\\\":\\\"1\\\",\\\"parcel_list_confirm_info\\\":[{\\\"confirm_charge_weight\\\":0,\\\"confirm_include_battery\\\":\\\"N\\\",\\\"confirm_volume_weight\\\":0,\\\"confirm_weight\\\":\\\"0\\\",\\\"currency_code\\\":\\\"USD\\\",\\\"parcel_total_value_confirm\\\":\\\"0.24\\\"}]}}]\",\"msg\":\"System processing succeeded\",\"result\":\"1\"}";
//        String message = "{\"data\":[{\"consignment_info\":{\"4px_tracking_no\":\"303847708788\",\"consignment_create_date\":1629417610000,\"consignment_status\":\"P\",\"ds_consignment_no\":\"DS303847708788\",\"get_no_mode\":\"C\",\"has_check_oda\":\"N\",\"insure_status\":\"N\",\"insure_type\":\"\",\"is_hold_sign\":\"N\",\"logistics_channel_no\":\"\",\"logistics_product_code\":\"O5\",\"logistics_product_name\":\"联邮通经济挂号-普货\",\"oda_result_sign\":\"N\",\"ref_no\":\"LOG20210820000032\"},\"parcel_confirm_info\":{\"confirm_parcel_qty\":\"1\",\"parcel_list_confirm_info\":[{\"confirm_charge_weight\":0,\"confirm_include_battery\":\"N\",\"confirm_volume_weight\":0,\"confirm_weight\":\"0\",\"currency_code\":\"USD\",\"parcel_total_value_confirm\":\"0.24\"}]}}],\"msg\":\"System processing succeeded\",\"result\":\"1\"}";
//        String message = "[{\\\"consignment_info\\\":{\\\"4px_tracking_no\\\":\\\"303847708788\\\",\\\"consignment_create_date\\\":1629417610000,\\\"consignment_status\\\":\\\"P\\\",\\\"ds_consignment_no\\\":\\\"DS303847708788\\\",\\\"get_no_mode\\\":\\\"C\\\",\\\"has_check_oda\\\":\\\"N\\\",\\\"insure_status\\\":\\\"N\\\",\\\"insure_type\\\":\\\"\\\",\\\"is_hold_sign\\\":\\\"N\\\",\\\"logistics_channel_no\\\":\\\"H01LFA0027911809\\\",\\\"logistics_product_code\\\":\\\"O5\\\",\\\"logistics_product_name\\\":\\\"联邮通经济挂号-普货\\\",\\\"oda_result_sign\\\":\\\"N\\\",\\\"ref_no\\\":\\\"LOG20210820000032\\\"},\\\"parcel_confirm_info\\\":{\\\"confirm_parcel_qty\\\":\\\"1\\\",\\\"parcel_list_confirm_info\\\":[{\\\"confirm_charge_weight\\\":0,\\\"confirm_include_battery\\\":\\\"N\\\",\\\"confirm_volume_weight\\\":0,\\\"confirm_weight\\\":\\\"0\\\",\\\"currency_code\\\":\\\"USD\\\",\\\"parcel_total_value_confirm\\\":\\\"0.24\\\"}]}}]";
//        message = message.replaceAll("\\\\","");
//        List<LogisticOrderInfoResponseFor4PX.OrderInfoDetail> orderInfoDetail = JSONObject.parseArray(message,LogisticOrderInfoResponseFor4PX.OrderInfoDetail.class);
//        System.out.println(orderInfoDetail);

        LogisticOrderInfoResponseFor4PX logisticOrderInfoResponseFor4PX= JSON.parseObject(message,LogisticOrderInfoResponseFor4PX.class);
        List<LogisticOrderInfoResponseFor4PX.OrderInfoDetail> dataDetail = logisticOrderInfoResponseFor4PX.getDataDetail();
        System.out.println(JSON.toJSONString(dataDetail));
    }

    @Test
    public void test_6_1() {
//        RuleObjectFormate ruleObjectFormate = new RuleObjectFormate();
//        ruleObjectFormate.setId(5L);
//        RuleObjectFormate ruleObjectFormate1 = new RuleObjectFormate();
//        ruleObjectFormate1.setId(2L);
//        LableLogisticRule lableLogisticRule = new LableLogisticRule(Arrays.asList(ruleObjectFormate,ruleObjectFormate1));
//        LogisticOrderDto logisticOrderDto = new LogisticOrderDto();
//        WaybillGoodDetailDto waybillGoodDetailDto = new WaybillGoodDetailDto();
//        waybillGoodDetailDto.setLabelIdList(Arrays.asList(1L,2l));
//        logisticOrderDto.setWaybillGoodDetailDtos(Arrays.asList(waybillGoodDetailDto));
//        boolean validate = lableLogisticRule.validate(logisticOrderDto);
//        System.out.println(validate);
        JSON.parseObject("{\"level\":0,\"list\":[{\"arr\":[{\"countryCode\":\"usa\"}]}],\"logisticsCode\"}");
    }

    @Test
    public void test_7() {
        CountryProvinceCity countryProvinceCity = new CountryProvinceCity();
        countryProvinceCity.setCountryCode("usa");
        CountryLogisticRule rule = new CountryLogisticRule(Collections.singletonList(countryProvinceCity));
        ArrayList<RuleValidator> list = new ArrayList();
        list.add(rule);
        LogisticRuleComposition logisticRuleComposition = new LogisticRuleComposition(list);
        logisticRuleComposition.setLogisticsCode("dddddddddd");
        LogisticsChannelDO logisticsChannelDO = BeanConvertUtils.copyProperties(logisticRuleComposition, LogisticsChannelDO.class);
        LogisticsChannelDO channelDO = new LogisticsChannelDO();
        BeanUtils.copyProperties(logisticRuleComposition, channelDO);
    }

    @Test
    public void test_8() throws IOException {
//        String name = "D:\\workspaces\\amg-project\\fulfillment-logistics-service\\fulfillment-logistics-core\\src\\main\\java\\com\\amg\\fulfillment\\cloud\\logistics\\mq\\consumer\\DeliveryPackage1688Consumer.java";
        String name = "D:\\workspaces\\amg-project\\fulfillment-logistics-service\\fulfillment-logistics-core\\target\\classes\\com\\amg\\fulfillment\\cloud\\logistics\\annotation";
        InputStream inputStream = this.getClass().getResourceAsStream(name);
        Properties properties = new Properties();
        properties.load(inputStream);
        System.out.println(properties);
    }


    @Test
    public void test_9() throws Exception {
        List<String> fpx_s_ok = Arrays.asList("FPX_S_OK","FPX_S_OKGP","FPX_S_OKVP","FPX_S_OKPO","FPX_S_OKCC","FPX_S_OKSC");
        System.out.println(fpx_s_ok.contains("FPX_S_OKSC"));
    }


    @Data
    public class Animal {
        private String name;
        private final Dog dog = new Dog(this);

        public Animal(String name) {
            this.name = name;
            System.out.println("ddd"+dog);
        }

    }

    @Data
    public class Dog {
        private Animal animal;

        public Dog(Animal animal) {
            this.animal = animal;
        }
    }

    @Test
    public void test_10() {
        PurchaseIntoDepositoryRetryJob retryJob = new PurchaseIntoDepositoryRetryJob();
        String message = "{\"Item\":[{\"CustomerOrderNumber\":\"LOG20210824000017\",\"Success\":1,\"TrackType\":\"2\",\"Remark\":\"\",\"AgentNumber\":null,\"WayBillNumber\":\"YT2123621272000018\",\"RequireSenderAddress\":0,\"TrackingNumber\":null,\"ShipperBoxs\":null}],\"Code\":\"0000\",\"Message\":\"提交成功!\",\"RequestId\":\"0HMB6ODRKBSTO:00000002\",\"TimeStamp\":\"2021-08-24T11:06:34.1304964+00:00\"}\n";
        LogisticPrintLabelResponseDto logisticPrintLabelResponseDto = JSON.parseObject(message,LogisticPrintLabelResponseDto.class);
        System.out.println(logisticPrintLabelResponseDto);

    }

    @Test
    public void test_11() {
        String name = "tom";
        System.out.println(String.format("%-10s", name).replaceAll(" ", "_"));
        System.out.println(String.format("%05d", 11).replaceAll(" ", "_"));
        
    }

}
