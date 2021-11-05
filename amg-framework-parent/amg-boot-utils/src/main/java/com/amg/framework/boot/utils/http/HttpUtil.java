package com.amg.framework.boot.utils.http;

import com.alibaba.fastjson.JSON;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import javax.net.ssl.SSLContext;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * http 工具类
 */
public class HttpUtil {

    private static CloseableHttpClient httpclient;

    // 编码格式
    private static String ENCODING = "UTF-8";

    // 请求 ContentType
    private static String CONTENT_TYPE_JSON = "application/json";

    static {
//            SSLContextBuilder builder = new SSLContextBuilder();
//            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy()); // 可配置真实 ssl 证书校验
//            SSLConnectionSocketFactory ssl = new SSLConnectionSocketFactory(builder.build());
//            LayeredConnectionSocketFactory ssl = new SSLConnectionSocketFactory(SSLContext.getDefault());
//            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
//                    .register("https", ssl)
//                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
//                    .build();
//            PoolingHttpClientConnectionManager pollingConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

        // 设置请求参数
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setConnectionRequestTimeout(5000).setSocketTimeout(5000).build();
        PoolingHttpClientConnectionManager pollingConnectionManager = new PoolingHttpClientConnectionManager(30, TimeUnit.MILLISECONDS);
        pollingConnectionManager.setMaxTotal(1000); // 设置连接池的最大连接数
        pollingConnectionManager.setDefaultMaxPerRoute(200); // 设置每个路由上的默认连接个数
        httpclient = HttpClients.custom().setConnectionManager(pollingConnectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(new DefaultHttpRequestRetryHandler(3, false))  // 设置重试次数，默认关闭
                .build();

        // 启动定时器，定时回收过期的连接
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                pollingConnectionManager.closeExpiredConnections();
                pollingConnectionManager.closeIdleConnections(5, TimeUnit.SECONDS);
            }
        }, 10 * 1000, 5 * 1000);
    }

    /**
     * 发送 get 请求
     * @param url
     * @return
     * @throws Exception
     */
    public static String get(String url) throws Exception {
        CloseableHttpResponse response = null;
        try {
            HttpGet get = new HttpGet(url);
            response = httpclient.execute(get);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, ENCODING);
            }
            return null;
        } finally {
            if (response != null)
                response.close();
        }
    }

    /**
     * 发送 post 请求
     * @param url
     * @param paramsMap
     * @return
     */
    public static String post(String url, Map<String, String> paramsMap) throws Exception {
        CloseableHttpResponse response = null;
        try {
            HttpPost method = new HttpPost(url);
            if (paramsMap != null) {
                List<NameValuePair> paramList = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> param : paramsMap.entrySet()) {
                    NameValuePair pair = new BasicNameValuePair(param.getKey(), param.getValue());
                    paramList.add(pair);
                }
                method.setEntity(new UrlEncodedFormEntity(paramList, ENCODING));
            }
            response = httpclient.execute(method);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, ENCODING);
            }
            return null;
        } finally {
            if (response != null)
                response.close();
        }
    }

    /**
     * 发送 xml 参数请求
     * */
    public static String postXml(String url, String xml) throws Exception {
        CloseableHttpResponse response = null;
        try {
            HttpPost post = new HttpPost(url);
            StringEntity se = new StringEntity(xml, ENCODING);
            post.setEntity(se);
            response = httpclient.execute(post);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, ENCODING);
            }
            return null;
        } finally {
            if (response != null)
                response.close();
        }
    }

    /**
     * 发送 json 请求
     * */
    public static String postJson(String url, Map map) throws Exception {
        CloseableHttpResponse response = null;
        try {
            HttpPost post = new HttpPost(url);
            StringEntity entity = new StringEntity(JSON.toJSONString(map),ENCODING);
            entity.setContentEncoding(ENCODING);
            entity.setContentType(CONTENT_TYPE_JSON);
            post.setEntity(entity);
            response = httpclient.execute(post);
            HttpEntity res = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, ENCODING);
            }
            return null;
        } finally {
            if (response != null)
                response.close();
        }
    }

    /**
     * 上传文件
     * */
    public static String postUploadFile(String url, CommonsMultipartFile file, String filePath, String fileName) throws Exception {
        CloseableHttpResponse response = null;
        try {
            HttpPost post = new HttpPost(url);
            StringBody filepath = new StringBody(filePath, ContentType.create("text/plain", ENCODING));
            StringBody filename = new StringBody(fileName, ContentType.create("text/plain", ENCODING));
            CommonsMultipartFile cf= (CommonsMultipartFile)file;
            DiskFileItem fi = (DiskFileItem)cf.getFileItem();
            // 把文件转换成流对象FileBody
            FileBody file1= new FileBody(fi.getStoreLocation());
            // 相当于<input type="file" name="file"/>
            HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("file", file1).addPart("filePath", filepath).addPart("fileName", filename).build();
            post.setEntity(reqEntity);
            response = httpclient.execute(post);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, ENCODING);
            }
            return null;
        } finally {
            if (response != null)
                response.close();
        }
    }

}
