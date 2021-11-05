package com.amg.fulfillment.cloud.logistics.api.logistic.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.junit.Test;

/**
 * @author Tom
 * @date 2021-04-07-18:39
 */
public class TestYanWen {

/*

    //测试
//    String account="100000";
//    String authorization="basic D6140AA383FD8515B09028C586493DDB";
//    String url = "http://47.96.220.163:802";

    //生产
    String account = "30035179";
    String authorization = "basic MzAwMzUxNzk6MjAxNDgwMWltZw==";
    String url = "http://online.yw56.com.cn";

    // ok
    @Test
    public void testTrack() {
        String url = "http://trackapi.yanwentech.com/api/tracking";
        String requestUrl = url + "?nums=6A16723741816";
        String body = HttpRequest.get(requestUrl).header("Authorization", account).execute().body();
        System.out.println(body);
    }

    //ok
    @Test
    public void testCreateOrder() {
        String requestUrl = url + "/service/Users/100000/EXPRESSES";
        String body = "<ExpressType><Epcode></Epcode><Userid>100000</Userid><Channel>895</Channel><UserOrderNumber>asbdsf123456</UserOrderNumber><SendDate>2020-08-29T00:00:00</SendDate><Receiver><Userid>100000</Userid><Name>tang</Name><Phone>1236548</Phone><Email>jpcn@mpc.com.br</Email><Company></Company><Country>美国</Country><Postcode>14201</Postcode><State>FL</State><City>City</City><Address1>String content1</Address1><Address2>String content2</Address2></Receiver><Sender><TaxNumber>String content</TaxNumber></Sender><Memo></Memo><Quantity>1</Quantity><GoodsName><NameCh>多媒体播放器</NameCh><NameEn>String content</NameEn><Weight>213</Weight><DeclaredValue>125</DeclaredValue><DeclaredCurrency>EUR</DeclaredCurrency><MoreGoodsName>多媒体播放器 23 MedialPlayer 2</MoreGoodsName></GoodsName></ExpressType>";
        HttpResponse response = HttpRequest.post(url).header("Authorization", authorization)
                .header("Content-Type", " text/xml; charset=utf-8")
                .header("Accept", " application/xml")
                .body(body)
                .execute();
        System.out.println(response);
    }

    @Test
    public void testGetOrderInfo() {
        String code = "UG034878013YP";
        String start = DateUtil.lastWeek().toString();
        String end = DateUtil.now();
        String requestUrl = url + String.format("/service/Users/%s/Expresses?page=1&code=%s&start=%s&end=%s&isstatus=1", account, code, start, end);
        System.out.println(requestUrl);
        HttpResponse response = HttpRequest.get(requestUrl).header("Authorization", authorization)
                .header("Content-Type", " text/xml; charset=utf-8")
                .header("Accept", " application/xml")
                .execute();
        System.out.println(response);
    }
*/

}
