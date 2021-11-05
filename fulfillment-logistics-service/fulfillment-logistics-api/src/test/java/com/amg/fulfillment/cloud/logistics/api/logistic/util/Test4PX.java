package com.amg.fulfillment.cloud.logistics.api.logistic.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.amg.fulfillment.cloud.logistics.api.dto.logistic.px4.LogisticOrderResponseFor4PX;
import com.fpx.api.model.AffterentParam;
import com.fpx.api.model.CommonRequestParam;
import com.fpx.api.utils.SignUtil;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

/**
 * @author Tom
 * @date 2021-04-07-10:58
 */
public class Test4PX {
/*

    //    沙箱
//        String appkey="5a8ef26c-b978-41bf-8e7c-6e824813275f";
//        String appSecret="466738f3-db75-4a83-b87d-4804601d3f14";
//        String url="http://open.sandbox.4px.com/router/api/service";



    //     生产 ok
    static String url = "http://open.4px.com/router/api/service";
    static String appkey = "87250cf7-7c89-4542-b3c3-ccf66ab3413e";
    static String appSecret = "225b7c22-07af-41e6-82bc-4015a1c38492";
    static String format = "json";


    */
/*
     * @author Tom
     * @date 2021/4/7  12:15  4PX 轨迹查询  ok
     *//*

    @Test
    public void testTrack() {
        try {
            String method = "tr.order.tracking.get";
            String version = "1.0.0";
            String requestBody = "{\"deliveryOrderNo\":\"H01LFA0002326683\"}";

            String requestUrl = getRequestUrl(url, method, requestBody, version);
            System.out.println("請求地址和参数：" + requestUrl);
            String reponse = HttpUtil.post(requestUrl, requestBody);
            System.out.println("返回消息：" + reponse);
            if (reponse.contains("result") && reponse.contains("errors")) {
                LogisticOrderResponseFor4PX logisticOrderResponseFor4PX = JSON.parseObject(reponse, LogisticOrderResponseFor4PX.class);
                System.out.println(logisticOrderResponseFor4PX);
            } else {
                System.out.println("不是josn格式的错误原因：");
                System.out.println(reponse);
            }
        } catch (Exception e) {
            System.out.println("轨迹查询发生错误，错误原因：" + e);
        }
    }

    */
/*
     * @author Tom
     * @date 2021/4/7  13:58 创建直发委托单 ok
     *//*

    @Test
    public void testCreateOrder() throws UnsupportedEncodingException {
        try {
            String method = "ds.xms.order.create";
            String version = "1.1.0";
            String requestBody = "{\"4px_tracking_no\":\"\",\"ref_no\":\"YIN201803280000002\",\"business_type\":\"BDS\",\"duty_type\":\"U\",\"cargo_type\":\"5\",\"vat_no\":\"8956232323\",\"eori_no\":\"8956232323\",\"buyer_id\":\"aliwangwang\",\"sales_platform\":\"ebay\",\"seller_id\":\"cainiao\",\"logistics_service_info\":{\"logistics_product_code\":\"F3\",\"customs_service\":\"N\",\"signature_service\":\"N\",\"value_added_services\":\"\"},\"label_barcode\":\"\",\"return_info\":{\"is_return_on_domestic\":\"Y\",\"domestic_return_addr\":{\"first_name\":\"ZHANG_return\",\"last_name\":\"YU_return\",\"company\":\"fpx_return\",\"phone\":\"8956232659\",\"phone2\":\"18562356856\",\"email\":\"return_ZHANGYZ@4PX.COM\",\"post_code\":\"518000\",\"country\":\"CN\",\"state\":\"广东省__return\",\"city\":\"深圳市_return\",\"district\":\"宝安区_return\",\"street\":\"财富港大厦D座25楼__return\",\"house_number\":\"16\"},\"is_return_on_oversea\":\"Y\",\"oversea_return_addr\":{\"first_name\":\"ZHANG_return_oversea\",\"last_name\":\"YU_return_oversea\",\"company\":\"fpx_return_oversea\",\"phone\":\"8956232659\",\"phone2\":\"18562356856\",\"email\":\"ZHANGYZ@4PX_return_oversea.COM\",\"post_code\":\"518000\",\"country\":\"CN\",\"state\":\"state_return_oversea\",\"city\":\"city_return_oversea\",\"district\":\"district__return_oversea\",\"street\":\"street_return_oversea\",\"house_number\":\"17\"}},\"parcel_list\":[{\"weight\":22,\"length\":123,\"width\":789,\"height\":456,\"parcel_value\":666.66,\"currency\":\"USD\",\"include_battery\":\"Y\",\"battery_type\":\"966\",\"product_list\":[{\"sku_code\":\"iPhone6  plus_sku_code\",\"standard_product_barcode\":\"56323598\",\"product_name\":\"iPhone6  plus_product_name\",\"product_description\":\"iPhone6  plusiPhone6  plus_product_description\",\"product_unit_price\":3,\"currency\":\"USD\",\"qty\":3}],\"declare_product_info\":[{\"declare_product_code\":\"62323_declare_product_code\",\"declare_product_name_cn\":\"手机贴膜_declare_name_cn\",\"declare_product_name_en\":\"phone_declare_product_name_en\",\"uses\":\"装饰_uses\",\"specification\":\"dgd23_specification\",\"component\":\"塑料_component\",\"unit_net_weight\":20,\"unit_gross_weight\":45,\"material\":\"565323\",\"declare_product_code_qty\":2,\"unit_declare_product\":\"个\",\"origin_country\":\"中国\",\"country_export\":\"越南\",\"country_import\":\"新加坡\",\"hscode_export\":\"45673576397\",\"hscode_import\":\"12332213134\",\"declare_unit_price_export\":23,\"currency_export\":\"USD\",\"declare_unit_price_import\":1.25,\"currency_import\":\"USD\",\"brand_export\":\"象印\",\"brand_import\":\"虎牌\",\"sales_url\":\"http://172.16.30.134:8038/loggerMessage/list/XMSaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\"}]}],\"is_insure\":\"Y\",\"insurance_info\":{\"insure_type\":\"8Y\",\"insure_value\":2,\"currency\":\"USD\",\"insure_person\":\"张三_insure_person\",\"certificate_type\":\"ID\",\"certificate_no\":\"429001198806253256\",\"category_code\":\"\",\"insure_product_name\":\"手机壳insure_product_name\",\"package_qty\":\"2\"},\"sender\":{\"first_name\":\"ZHANG_sender\",\"last_name\":\"YU_sender\",\"company\":\"fpx_sender\",\"phone\":\"8956232659\",\"phone2\":\"18562356856\",\"email\":\"ZHANGYZ_sender@4PX.COM\",\"post_code\":\"518000\",\"country\":\"CN\",\"state\":\"state_sender\",\"city\":\"city_sender\",\"district\":\"district_sender\",\"street\":\"street_sender\",\"house_number\":\"18\",\"certificate_info\":{\"certificate_type\":\"PP\",\"certificate_no\":\"965232323232656532\",\"id_front_url\":\"https://ju.taobao.com/jusp/other/mingpin/tp.htmbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb\",\"id_back_url\":\"https://ju.taobao.com/jusp/other/mingpin/tp.htmcccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc\"}},\"recipient_info\":{\"first_name\":\"ZHANG_recipient\",\"last_name\":\"YU_recipient\",\"company\":\"fpx_recipient\",\"phone\":\"8956232659\",\"phone2\":\"18562356856\",\"email\":\"ZHANGYZ_recipient@4PX.COM\",\"post_code\":\"518000\",\"country\":\"SG\",\"state\":\"state_recipient\",\"city\":\"city_recipient\",\"district\":\"district_recipient\",\"street\":\"street_recipient\",\"house_number\":\"19\",\"certificate_info\":{\"certificate_type\":\"ID\",\"certificate_no\":\"965232323232656532\",\"id_front_url\":\"https://ju.taobao.com/jusp/other/mingpin/tp.htm?spm=875.7931836/ddddddddddddddddddddddddddddddddddddddddddddd\",\"id_back_url\":\"https://ju.taobao.com/jusp/other/mingpin/tp.htmeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee\"}},\"deliver_type_info\":{\"deliver_type\":\"2\",\"warehouse_code\":\"\",\"pick_up_info\":{\"expect_pick_up_earliest_time\":\"1432710115000\",\"expect_pick_up_latest_time\":\"1432710115000\",\"pick_up_address_info\":{\"first_name\":\"ZHANG_pick_up\",\"last_name\":\"YU_pick_up\",\"company\":\"fpx_pick_up\",\"phone\":\"8956232659\",\"phone2\":\"18562356856\",\"email\":\"ZHANGYZ_pick_up@4PX.COM\",\"post_code\":\"518000\",\"country\":\"CN\",\"state\":\"state_pick_up\",\"city\":\"city_pick_up\",\"district\":\"district_pick_up\",\"street\":\"street_pick_up\",\"house_number\":\"20\"}},\"express_to_4px_info\":{\"express_company\":\"4PXexpress_company\",\"tracking_no\":\"8956232323\"},\"self_send_to_4px_info\":{\"booking_earliest_time\":\"1432710115000\",\"booking_latest_time\":\"1432710115000\"}}}";

            String requestUrl = getRequestUrl(url, method, requestBody, version);
            System.out.println("請求地址和参数：" + requestUrl);
            String reponse = HttpUtil.post(requestUrl, requestBody);
            System.out.println("返回消息：" + reponse);
            if (reponse.contains("result") && reponse.contains("errors")) {
                LogisticOrderResponseFor4PX logisticOrderResponseFor4PX = JSON.parseObject(reponse, LogisticOrderResponseFor4PX.class);
                System.out.println(logisticOrderResponseFor4PX);
            } else {
                System.out.println("不是josn格式的错误原因：");
                System.out.println(reponse);
            }
        } catch (Exception e) {
            System.out.println("轨迹查询发生错误，错误原因：" + e);
        }

    }


    private String getRequestUrl(String url, String method, String bodyJson, String vsersion) {
        AffterentParam affterentParam = new AffterentParam();
        affterentParam.setAppKey(appkey);
        affterentParam.setFormat(format);
        affterentParam.setMethod(method);
        affterentParam.setAppSecret(appSecret);
        affterentParam.setVersion(vsersion);
        long timestamp = System.currentTimeMillis();
        String sign = SignUtil.getSingByParam(affterentParam, bodyJson, timestamp);
        CommonRequestParam commonRequestParam = new CommonRequestParam();
        BeanUtil.copyProperties(affterentParam, commonRequestParam);
        commonRequestParam.setTimestamp(timestamp);
        commonRequestParam.setSign(sign);
        return transformToUrlParams(url, commonRequestParam);
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

//    private String getRequestUrl(String method,String requestBody,String version) throws UnsupportedEncodingException {
//
//        StringBuilder sb = new StringBuilder();
//        StringBuilder publicParam = new StringBuilder();
//        linkedHashMap.forEach((key, value) -> sb.append(key).append("=").append(value));
//        linkedHashMap.forEach((key, value) -> publicParam.append(key).append("=").append(value).append("&"));
//        //请求参数字符串化
//        sb.append(requestBody);
//        sb.append(appSecret);
//        String unencyStr = sb.toString().replaceAll("=", "");
//        System.out.println("加密前字符串信息：" + unencyStr);
//        //加密
//        String sign = MD5.getMd5(unencyStr);
//        System.out.println("加密后sign：" + sign);
//        String fxsgin = MD5Util.doMd5(unencyStr).toLowerCase();
//        System.out.println("fxsgin加密后sign：" + fxsgin+"  是否相等："+(sign.equals(fxsgin)));
//        publicParam.append("sign=").append(fxsgin);
//        String requestUrl = url + "?" + publicParam.toString();
//        System.out.println("请求url：" + requestUrl);
//        return requestUrl;
//    }
//
//
//    private LinkedHashMap<String, String> publicParams(String method, String vsersion) {
//        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>(16);
//        linkedHashMap.put("app_key",appkey);
//        linkedHashMap.put("format",format);
//        linkedHashMap.put("method",method);
//        linkedHashMap.put("timestamp",System.currentTimeMillis()+"");
//        linkedHashMap.put("v",vsersion);
//        return linkedHashMap;
//    }

*/

}
